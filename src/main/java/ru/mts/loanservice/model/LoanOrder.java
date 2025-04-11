package ru.mts.loanservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "loan_order")
public class LoanOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @Column(name = "credit_rating", nullable = false)
    private double creditRating;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "time_insert", nullable = false)
    private Timestamp timeInsert;

    @Column(name = "time_update", nullable = false)
    private Timestamp timeUpdate;
}
