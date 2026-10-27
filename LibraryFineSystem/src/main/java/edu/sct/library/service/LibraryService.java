package edu.sct.library.service;

import edu.sct.library.dao.LibraryDAO;
import edu.sct.library.exception.LibraryException;
import edu.sct.library.model.*;
import java.sql.SQLException;
import java.util.List;

public class LibraryService {
    private final LibraryDAO dao = new LibraryDAO();

    public int addItem(String type, String title, String author) throws LibraryException {
        if (title == null || title.isBlank() || author == null || author.isBlank()) {
            throw new LibraryException("Title and author are required.");
        }
        LibraryItem item = switch (type) {
            case "Book" -> new Book(0, title.trim(), author.trim(), false);
            case "Journal" -> new Journal(0, title.trim(), author.trim(), false);
            case "Multimedia" -> new Multimedia(0, title.trim(), author.trim(), false);
            default -> throw new LibraryException("Select a valid item type.");
        };
        try {
            return dao.addItem(item);
        } catch (SQLException ex) {
            throw new LibraryException("Could not save the item. Check your database connection.", ex);
        }
    }

    public List<LibraryItem> listItems(String search) throws LibraryException {
        try { return dao.listItems(search); }
        catch (SQLException ex) { throw new LibraryException("Could not load catalog: " + ex.getMessage(), ex); }
    }

    public void issueItem(String itemIdText, String memberId) throws LibraryException {
        int itemId = parseItemId(itemIdText);
        if (memberId == null || memberId.isBlank()) throw new LibraryException("Member ID is required.");
        try { dao.issueItem(itemId, memberId.trim()); }
        catch (SQLException ex) { throw new LibraryException("Could not issue item: " + ex.getMessage(), ex); }
    }

    public double returnItem(String itemIdText) throws LibraryException {
        int itemId = parseItemId(itemIdText);
        try { return dao.returnItem(itemId); }
        catch (SQLException ex) { throw new LibraryException("Could not return item: " + ex.getMessage(), ex); }
    }

    private int parseItemId(String text) throws LibraryException {
        try {
            int id = Integer.parseInt(text.trim());
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (Exception ex) {
            throw new LibraryException("Enter a valid positive numeric item ID.");
        }
    }
}
