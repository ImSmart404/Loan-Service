package ru.mts.loanservice.service;

import org.springframework.http.ResponseEntity;
import ru.mts.loanservice.DTO.BaseDto;
import ru.mts.loanservice.model.LoanOrder;


public interface LoanOrderService {

    ResponseEntity<BaseDto> findByUserId(Long userId, Long tariffId);
    ResponseEntity<BaseDto> findByUserIdAndOrderId(Long userId, String orderId);
    ResponseEntity<BaseDto> findByOrderId(String orderId);

    LoanOrder save(Long userId, Long tariffId);
}
