package ru.mtuci.loanservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostOrderIdDataResponseDTO implements BaseDto {
    String orderId;
}
