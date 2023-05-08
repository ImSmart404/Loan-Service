package ru.mts.loanservice.model;
import lombok.Data;
@Data
public class GetStatusOrderDataResponse {
    String statusOrder;

    public GetStatusOrderDataResponse(LoanOrder order){
        statusOrder = order.getStatus();
    }
}
