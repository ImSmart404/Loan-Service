package ru.mtc.loanservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
