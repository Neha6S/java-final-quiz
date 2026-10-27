package edu.sct.library.model;

import java.time.LocalDate;

public class Loan {
    private final int loanId;
    private final int itemId;
    private final String memberId;
    private final LocalDate issueDate;
    private final LocalDate dueDate;

    public Loan(int loanId, int itemId, String memberId, LocalDate issueDate, LocalDate dueDate) {
        this.loanId = loanId;
        this.itemId = itemId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }
    public int getLoanId() { return loanId; }
    public int getItemId() { return itemId; }
    public String getMemberId() { return memberId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
}
