package ru.mts.loanservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostOrderIdDataResponse {
    String orderId;

    public PostOrderIdDataResponse(LoanOrder order){
        orderId = order.getOrderId();
    }
}
