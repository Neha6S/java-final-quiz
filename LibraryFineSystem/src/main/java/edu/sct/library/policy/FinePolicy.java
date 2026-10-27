package edu.sct.library.policy;

public interface FinePolicy {
    double calculateLateFine(int daysOverdue);
}
