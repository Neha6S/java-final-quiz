package edu.sct.library.model;

import edu.sct.library.policy.FinePolicy;

public abstract class LibraryItem implements FinePolicy {
    private int itemId;
    private final String title;
    private final String author;
    private boolean issued;

    protected LibraryItem(int itemId, String title, String author, boolean issued) {
        this.itemId = itemId;
        this.title = title;
        this.author = author;
        this.issued = issued;
    }

    public abstract String getItemType();

    public int getItemId() { return itemId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isIssued() { return issued; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public void setIssued(boolean issued) { this.issued = issued; }

    @Override
    public String toString() {
        return itemId + " | " + title + " | " + author + " | " + getItemType()
                + " | " + (issued ? "Issued" : "Available");
    }
}
