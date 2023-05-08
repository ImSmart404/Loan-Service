package ru.mts.loanservice.service;

import ru.mts.loanservice.model.Tariff;

import java.util.List;

public interface TariffService {
    List<Tariff> findAll();
    Tariff findById(Long id);
}
