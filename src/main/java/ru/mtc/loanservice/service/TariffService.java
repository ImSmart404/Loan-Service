package ru.mtc.loanservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtc.loanservice.model.Tariff;

import java.util.List;

@Service
public interface TariffService {
    List<Tariff> findAll();
    Tariff findById(Long id);
}
