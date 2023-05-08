package ru.mts.loanservice.service;

import org.springframework.http.ResponseEntity;
import ru.mts.loanservice.model.ResponseData;
import ru.mts.loanservice.model.Tariff;

public interface TariffService {
    ResponseEntity<ResponseData> findAll();
    Tariff findById(Long id);
}
