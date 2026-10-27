package edu.sct.library.model;

public class Multimedia extends LibraryItem {
    public Multimedia(int id, String title, String author, boolean issued) {
        super(id, title, author, issued);
    }
    @Override public String getItemType() { return "MULTIMEDIA"; }
    @Override public double calculateLateFine(int daysOverdue) {
        return Math.max(0, daysOverdue) * 10.0;
    }
}
