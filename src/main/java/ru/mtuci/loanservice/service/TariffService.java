package ru.mtuci.loanservice.service;

import org.springframework.http.ResponseEntity;
import ru.mtuci.loanservice.DTO.BaseDto;

public interface TariffService {
    ResponseEntity<BaseDto> findAll();
}
