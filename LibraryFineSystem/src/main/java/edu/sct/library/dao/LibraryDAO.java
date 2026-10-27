package edu.sct.library.dao;

import edu.sct.library.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibraryDAO {
    public int addItem(LibraryItem item) throws SQLException {
        String sql = "INSERT INTO library_items(title, author, item_type, is_issued) VALUES(?,?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getTitle());
            ps.setString(2, item.getAuthor());
            ps.setString(3, item.getItemType());
            ps.setBoolean(4, false);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("The database did not return an item ID.");
    }

    public List<LibraryItem> listItems(String search) throws SQLException {
        List<LibraryItem> items = new ArrayList<>();
        String sql = "SELECT item_id,title,author,item_type,is_issued FROM library_items " +
                "WHERE title LIKE ? OR author LIKE ? ORDER BY item_id DESC";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String term = "%" + (search == null ? "" : search.trim()) + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) items.add(mapItem(rs));
            }
        }
        return items;
    }

    public LibraryItem findItem(int itemId) throws SQLException {
        String sql = "SELECT item_id,title,author,item_type,is_issued FROM library_items WHERE item_id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapItem(rs) : null;
            }
        }
    }

    public void issueItem(int itemId, String memberId) throws SQLException {
        String update = "UPDATE library_items SET is_issued=TRUE WHERE item_id=? AND is_issued=FALSE";
        String insert = "INSERT INTO loans(item_id,member_id,issue_date,due_date) VALUES(?,?,?,?)";
        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement up = c.prepareStatement(update)) {
                up.setInt(1, itemId);
                if (up.executeUpdate() != 1) throw new SQLException("Item not found or already issued.");
            }
            try (PreparedStatement ps = c.prepareStatement(insert)) {
                LocalDate today = LocalDate.now();
                ps.setInt(1, itemId);
                ps.setString(2, memberId);
                ps.setDate(3, Date.valueOf(today));
                ps.setDate(4, Date.valueOf(today.plusDays(14)));
                ps.executeUpdate();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            }
            c.commit();
        }
    }

    public double returnItem(int itemId) throws SQLException {
        String find = "SELECT loan_id,due_date FROM loans WHERE item_id=? AND return_date IS NULL ORDER BY loan_id DESC LIMIT 1";
        String closeLoan = "UPDATE loans SET return_date=?,fine_amount=? WHERE loan_id=?";
        String updateItem = "UPDATE library_items SET is_issued=FALSE WHERE item_id=?";
        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                int loanId;
                LocalDate due;
                try (PreparedStatement ps = c.prepareStatement(find)) {
                    ps.setInt(1, itemId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("No active loan found for this item.");
                        loanId = rs.getInt("loan_id");
                        due = rs.getDate("due_date").toLocalDate();
                    }
                }
                LibraryItem item;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT item_id,title,author,item_type,is_issued FROM library_items WHERE item_id=?")) {
                    ps.setInt(1, itemId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Item not found.");
                        item = mapItem(rs);
                    }
                }
                LocalDate today = LocalDate.now();
                int overdue = Math.max(0, (int) java.time.temporal.ChronoUnit.DAYS.between(due, today));
                double fine = item.calculateLateFine(overdue);
                try (PreparedStatement ps = c.prepareStatement(closeLoan)) {
                    ps.setDate(1, Date.valueOf(today));
                    ps.setDouble(2, fine);
                    ps.setInt(3, loanId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(updateItem)) {
                    ps.setInt(1, itemId);
                    ps.executeUpdate();
                }
                c.commit();
                return fine;
            } catch (SQLException | RuntimeException ex) {
                c.rollback();
                throw ex;
            }
        }
    }

    private LibraryItem mapItem(ResultSet rs) throws SQLException {
        int id = rs.getInt("item_id");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String type = rs.getString("item_type");
        boolean issued = rs.getBoolean("is_issued");
        return switch (type) {
            case "BOOK" -> new Book(id, title, author, issued);
            case "JOURNAL" -> new Journal(id, title, author, issued);
            case "MULTIMEDIA" -> new Multimedia(id, title, author, issued);
            default -> throw new SQLException("Unknown item type: " + type);
        };
    }
}
