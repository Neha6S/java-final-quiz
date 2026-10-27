CREATE DATABASE IF NOT EXISTS library_fine_system;
USE library_fine_system;

CREATE TABLE IF NOT EXISTS library_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(150) NOT NULL,
    item_type VARCHAR(30) NOT NULL,
    is_issued BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS loans (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    member_id VARCHAR(80) NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    fine_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_loan_item FOREIGN KEY (item_id) REFERENCES library_items(item_id)
);

CREATE INDEX idx_items_title ON library_items(title);
CREATE INDEX idx_loans_item_active ON loans(item_id, return_date);
