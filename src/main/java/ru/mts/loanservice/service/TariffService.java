package ru.mts.loanservice.service;

import org.springframework.http.ResponseEntity;
import ru.mts.loanservice.DTO.ResponseDataDTO;
import ru.mts.loanservice.model.Tariff;

public interface TariffService {
    ResponseEntity<ResponseDataDTO> findAll();
    Tariff findById(Long id);
}
