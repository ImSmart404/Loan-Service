package ru.mts.loanservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.mts.loanservice.DTO.GetTariffsDataResponseDTO;
import ru.mts.loanservice.DTO.ResponseDataDTO;
import ru.mts.loanservice.model.Tariff;
import ru.mts.loanservice.repository.TariffRepository;
import ru.mts.loanservice.service.TariffService;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TariffServiceImpl implements TariffService {
    private final TariffRepository tariffRepository;
    @Override
    public ResponseEntity<ResponseDataDTO> findAll() {
        List<Tariff> tariffs = tariffRepository.findAll();
        GetTariffsDataResponseDTO data = new GetTariffsDataResponseDTO(tariffs);
        ResponseDataDTO response = new ResponseDataDTO(data);
        return  ResponseEntity.ok(response);
    }

    @Override
    public Tariff findById(Long id) {
        Tariff tariff = tariffRepository.findById(id);
        return tariff;
    }
}
