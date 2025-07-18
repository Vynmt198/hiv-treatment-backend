package com.hivmedical.medical.entitty;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "finance_transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // INCOME, EXPENSE

    private String description;

    private Long amount;

    public Transaction() {
    }

    public Transaction(Long id, LocalDate date, TransactionType type, String description, Long amount) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.description = description;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}