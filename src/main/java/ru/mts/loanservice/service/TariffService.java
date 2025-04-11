package ru.mts.loanservice.service;

import org.springframework.http.ResponseEntity;
import ru.mts.loanservice.DTO.BaseDto;

public interface TariffService {
    ResponseEntity<BaseDto> findAll();
}
