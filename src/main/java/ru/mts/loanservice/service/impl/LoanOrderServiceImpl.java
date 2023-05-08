package ru.mts.loanservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mts.loanservice.model.LoanOrder;
import ru.mts.loanservice.repository.LoanOrderRepository;
import ru.mts.loanservice.repository.TariffRepository;
import ru.mts.loanservice.service.LoanOrderService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class LoanOrderServiceImpl implements LoanOrderService {

    private final LoanOrderRepository loanOrderRepository;
    private final TariffRepository tariffRepository;

    @Override
    public List<LoanOrder> findByUserId(Long userId) {
        List<LoanOrder> loanOrders = loanOrderRepository.findByUserId(userId);
        return loanOrders;
    }

    @Override
    public Optional<LoanOrder> findByUserIdAndOrderId(Long userId, String orderId) {
        Optional<LoanOrder> loanOrder = loanOrderRepository.findByUserIdAndOrderId(userId,orderId);
        return loanOrder;
    }

    @Override
    public Optional<LoanOrder> findByOrderId(String orderId) {
        Optional<LoanOrder> loanOrder = loanOrderRepository.findByOrderId(orderId);
        return loanOrder;
    }

    @Override
    public List<LoanOrder> findByStatus(String status) {
        List<LoanOrder> loanOrders = loanOrderRepository.findByStatus(status);

        return loanOrders;
    }

    @Override
    public void delete(LoanOrder order) {
        loanOrderRepository.delete(order);
    }

    @Override
    public LoanOrder save(Long userId, Long tariffId) {
        LoanOrder newOrder = new LoanOrder();
        newOrder.setOrderId(String.valueOf(UUID.randomUUID()));
        newOrder.setUserId(userId);
        newOrder.setTariff(tariffRepository.findById(tariffId));
        newOrder.setCreditRating(Math.round((Math.random() * 0.8 + 0.1) * 100.0) / 100.0);
        newOrder.setStatus("IN_PROGRESS");
        newOrder.setTimeUpdate(Timestamp.valueOf(LocalDateTime.now()));
        newOrder.setTimeInsert(Timestamp.valueOf(LocalDateTime.now()));
        loanOrderRepository.save(newOrder);
        return newOrder;
    }
}
