package ru.mts.loanservice.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mts.loanservice.model.LoanOrder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetStatusOrderDataResponseDTO {
    String statusOrder;
}
