package ru.mts.loanservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class GetTariffsDataResponse {
    List<Tariff> tariffs;
}
