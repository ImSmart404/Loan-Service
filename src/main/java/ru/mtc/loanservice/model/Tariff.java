package ru.mtc.loanservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ConditionalOnReactiveDiscoveryEnabled;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "tariff")
public class Tariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String type;

    @Column(name = "interest_rate",nullable = false)
    private String interestRate;

}
