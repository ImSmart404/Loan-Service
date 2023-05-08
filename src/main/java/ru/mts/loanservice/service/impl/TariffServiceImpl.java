package ru.mts.loanservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mts.loanservice.model.Tariff;
import ru.mts.loanservice.repository.TariffRepository;
import ru.mts.loanservice.service.TariffService;

import java.util.List;
@RequiredArgsConstructor
@Service
public class TariffServiceImpl implements TariffService {
    private final TariffRepository tariffRepository;
    @Override
    public List<Tariff> findAll() {
        List<Tariff> tariffs = tariffRepository.findAll();
        return tariffs;
    }

    @Override
    public Tariff findById(Long id) {
        Tariff tariff = tariffRepository.findById(id);
        return tariff;
    }
}
