package ru.mts.loanservice.DTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {
    String code;
    String message;
}
