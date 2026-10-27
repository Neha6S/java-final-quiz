package edu.sct.library.ui;

import edu.sct.library.exception.LibraryException;
import edu.sct.library.model.LibraryItem;
import edu.sct.library.service.LibraryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LibraryManagementApp extends JFrame {
    private final LibraryService service = new LibraryService();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Item ID", "Title", "Author", "Type", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);
    private final JTextField searchField = new JTextField(18);

    public LibraryManagementApp() {
        super("Library Management & Dynamic Fine System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("LIBRARY MANAGEMENT & DYNAMIC FINE SYSTEM", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 20));
        heading.setBorder(BorderFactory.createEmptyBorder(14, 8, 8, 8));
        add(heading, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Catalog", buildCatalogPanel());
        tabs.addTab("Issue / Return", buildLoanPanel());
        add(tabs, BorderLayout.CENTER);

        JLabel footer = new JLabel("Books ₹2/day  |  Journals ₹5/day  |  Multimedia ₹10/day  |  14-day loan period",
                SwingConstants.CENTER);
        footer.setBorder(BorderFactory.createEmptyBorder(6, 5, 12, 5));
        add(footer, BorderLayout.SOUTH);

        refreshCatalog("");
    }

    private JPanel buildCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search title/author:"));
        top.add(searchField);
        JButton search = new JButton("Search");
        JButton showAll = new JButton("Show All");
        top.add(search);
        top.add(showAll);
        search.addActionListener(e -> refreshCatalog(searchField.getText()));
        showAll.addActionListener(e -> { searchField.setText(""); refreshCatalog(""); });

        JPanel addForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addForm.setBorder(BorderFactory.createTitledBorder("Add catalog item"));
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Book", "Journal", "Multimedia"});
        JTextField titleField = new JTextField(16);
        JTextField authorField = new JTextField(14);
        JButton addButton = new JButton("Add Item");
        addForm.add(new JLabel("Type:"));
        addForm.add(typeBox);
        addForm.add(new JLabel("Title:"));
        addForm.add(titleField);
        addForm.add(new JLabel("Author:"));
        addForm.add(authorField);
        addForm.add(addButton);
        addButton.addActionListener(e -> {
            try {
                int id = service.addItem((String) typeBox.getSelectedItem(), titleField.getText(), authorField.getText());
                JOptionPane.showMessageDialog(this, "Item added successfully. Item ID: " + id);
                titleField.setText("");
                authorField.setText("");
                refreshCatalog(searchField.getText());
            } catch (LibraryException ex) {
                showError(ex.getMessage());
            }
        });

        JPanel north = new JPanel(new BorderLayout());
        north.add(top, BorderLayout.NORTH);
        north.add(addForm, BorderLayout.SOUTH);
        panel.add(north, BorderLayout.NORTH);
        table.setFillsViewportHeight(true);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLoanPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 8, 10, 8);
        g.anchor = GridBagConstraints.WEST;

        JTextField issueItemId = new JTextField(15);
        JTextField memberId = new JTextField(15);
        JButton issueButton = new JButton("Issue Item");
        JTextField returnItemId = new JTextField(15);
        JButton returnButton = new JButton("Return Item & Calculate Fine");

        g.gridx = 0; g.gridy = 0; panel.add(new JLabel("Item ID to issue:"), g);
        g.gridx = 1; panel.add(issueItemId, g);
        g.gridx = 0; g.gridy = 1; panel.add(new JLabel("Member ID:"), g);
        g.gridx = 1; panel.add(memberId, g);
        g.gridx = 1; g.gridy = 2; panel.add(issueButton, g);

        JSeparator separator = new JSeparator();
        g.gridx = 0; g.gridy = 3; g.gridwidth = 2; g.fill = GridBagConstraints.HORIZONTAL;
        panel.add(separator, g);
        g.gridwidth = 1; g.fill = GridBagConstraints.NONE;
        g.gridx = 0; g.gridy = 4; panel.add(new JLabel("Item ID to return:"), g);
        g.gridx = 1; panel.add(returnItemId, g);
        g.gridx = 1; g.gridy = 5; panel.add(returnButton, g);

        issueButton.addActionListener(e -> {
            try {
                service.issueItem(issueItemId.getText(), memberId.getText());
                JOptionPane.showMessageDialog(this, "Item issued. Due date is 14 days from today.");
                issueItemId.setText("");
                memberId.setText("");
                refreshCatalog(searchField.getText());
            } catch (LibraryException ex) { showError(ex.getMessage()); }
        });
        returnButton.addActionListener(e -> {
            try {
                double fine = service.returnItem(returnItemId.getText());
                JOptionPane.showMessageDialog(this, String.format("Item returned successfully.%nLate fine: ₹%.2f", fine));
                returnItemId.setText("");
                refreshCatalog(searchField.getText());
            } catch (LibraryException ex) { showError(ex.getMessage()); }
        });
        return panel;
    }

    private void refreshCatalog(String search) {
        try {
            List<LibraryItem> items = service.listItems(search);
            tableModel.setRowCount(0);
            for (LibraryItem item : items) {
                tableModel.addRow(new Object[]{item.getItemId(), item.getTitle(), item.getAuthor(),
                        item.getItemType(), item.isIssued() ? "Issued" : "Available"});
            }
        } catch (LibraryException ex) {
            // On first launch, show a helpful message without crashing if MySQL is not configured.
            tableModel.setRowCount(0);
            showError(ex.getMessage() + "\n\nRun src/main/resources/schema.sql and check your database settings.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Library System", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryManagementApp().setVisible(true));
    }
}
