package ru.mts.loanservice.service;

import org.springframework.http.ResponseEntity;
import ru.mts.loanservice.model.LoanOrder;

import java.util.List;


public interface LoanOrderService {

    ResponseEntity<Object> findByUserId(Long userId, Long tariffId);
    ResponseEntity<Object> findByUserIdAndOrderId(Long userId, String orderId);
    ResponseEntity<Object> findByOrderId(String orderId);

    List<LoanOrder> findByStatus(String status);

    void delete(LoanOrder order);

    LoanOrder save(Long userId, Long tariffId);
}
