package ru.mts.loanservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.mts.loanservice.model.Tariff;

import java.util.List;

@Data
@AllArgsConstructor
public class GetTariffsDataResponseDTO {
    List<Tariff> tariffs;
}
