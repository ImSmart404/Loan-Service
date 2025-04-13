package ru.mtuci.loanservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.mtuci.loanservice.DTO.BaseDto;
import ru.mtuci.loanservice.DTO.GetTariffsDataResponseDTO;
import ru.mtuci.loanservice.model.Tariff;
import ru.mtuci.loanservice.repository.TariffRepository;
import ru.mtuci.loanservice.service.TariffService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TariffServiceImpl implements TariffService {

    private final TariffRepository tariffRepository;

    @Override
    public ResponseEntity<BaseDto> findAll() {
        List<Tariff> tariffs = tariffRepository.findAll();
        GetTariffsDataResponseDTO data = new GetTariffsDataResponseDTO(tariffs);
        return ResponseEntity.ok(data);
    }
}
