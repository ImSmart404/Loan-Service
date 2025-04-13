package ru.mts.loanservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mts.loanservice.model.Tariff;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTariffsDataResponseDTO implements BaseDto {
    List<Tariff> tariffs;
}
