package ru.mtuci.loanservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetStatusOrderDataResponseDTO implements BaseDto {
    String statusOrder;
}
