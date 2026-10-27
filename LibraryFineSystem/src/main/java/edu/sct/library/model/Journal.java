package edu.sct.library.model;

public class Journal extends LibraryItem {
    public Journal(int id, String title, String author, boolean issued) {
        super(id, title, author, issued);
    }
    @Override public String getItemType() { return "JOURNAL"; }
    @Override public double calculateLateFine(int daysOverdue) {
        return Math.max(0, daysOverdue) * 5.0;
    }
}
