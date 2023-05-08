package ru.mts.loanservice.model;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetStatusOrderDataResponse {
    String statusOrder;

    public GetStatusOrderDataResponse(LoanOrder order){
        statusOrder = order.getStatus();
    }
}
