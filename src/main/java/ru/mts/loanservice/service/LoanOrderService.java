package ru.mts.loanservice.service;

import ru.mts.loanservice.model.LoanOrder;

import java.util.List;
import java.util.Optional;


public interface LoanOrderService {

    List<LoanOrder> findByUserId(Long userId);
    Optional<LoanOrder> findByUserIdAndOrderId(Long userId, String orderId);
    Optional<LoanOrder>  findByOrderId(String orderId);

    List<LoanOrder> findByStatus(String status);

    void delete(LoanOrder order);

    LoanOrder save(Long userId, Long tariffId);
}