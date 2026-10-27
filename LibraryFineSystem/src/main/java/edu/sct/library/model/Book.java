package edu.sct.library.model;

public class Book extends LibraryItem {
    public Book(int id, String title, String author, boolean issued) {
        super(id, title, author, issued);
    }
    @Override public String getItemType() { return "BOOK"; }
    @Override public double calculateLateFine(int daysOverdue) {
        return Math.max(0, daysOverdue) * 2.0;
    }
}
