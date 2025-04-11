package ru.mts.loanservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostOrderIdDataResponseDTO implements BaseDto {
    String orderId;
}
