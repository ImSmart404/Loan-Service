package ru.mts.loanservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.mts.loanservice.model.*;
import ru.mts.loanservice.model.Error;
import ru.mts.loanservice.repository.LoanOrderRepository;
import ru.mts.loanservice.repository.TariffRepository;
import ru.mts.loanservice.service.LoanOrderService;
import ru.mts.loanservice.service.TariffService;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class LoanOrderServiceImpl implements LoanOrderService {

    private final LoanOrderRepository loanOrderRepository;
    private final TariffRepository tariffRepository;
    private final TariffService tariffService;

    @Override
    public ResponseEntity<Object> findByUserId(Long userId, Long tariffId) {

        try {
            tariffService.findById(tariffId);
        }
        catch (EmptyResultDataAccessException ex){
            Error error = new Error("TARIFF_NOT_FOUND", "Тариф не найден");
            ResponseError refusedResponse = new ResponseError(error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(refusedResponse);
        }
        List<LoanOrder> loanOrders = loanOrderRepository.findByUserId(userId);
        for (LoanOrder order : loanOrders) {
            if (order.getTariff().getId() == tariffId) {
                switch (order.getStatus()) {
                    case "IN_PROGRESS":
                        Error inProgressError = new Error("LOAN_CONSIDERATION", "Тариф в процессе рассмотрения");
                        ResponseError inProgressResponse = new ResponseError(inProgressError);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(inProgressResponse);
                    case "APPROVED":
                        Error approvedError = new Error("LOAN_ALREADY_APPROVED", "Тариф уже одобрен");
                        ResponseError approvedResponse = new ResponseError(approvedError);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(approvedResponse);
                    case "REFUSED": {
                        if (Duration.between(order.getTimeUpdate().toLocalDateTime(), LocalDateTime.now()).toMinutes() < 2) {
                            Error refusedError = new Error("TRY_LATER", "Попробуйте оставить заявку позже");
                            ResponseError refusedResponse = new ResponseError(refusedError);
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(refusedResponse);
                        }
                    }
                }
            }
        }
        PostOrderIdDataResponse data = new PostOrderIdDataResponse(save(userId,tariffId));
        ResponseData response = new ResponseData(data);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> findByUserIdAndOrderId(Long userId, String orderId) {
        Optional<LoanOrder> loanOrder = loanOrderRepository.findByUserIdAndOrderId(userId,orderId);
        Error error = new Error();
        ResponseError responseError = new ResponseError();
        if (loanOrder.isEmpty()) {
            error.setCode("ORDER_NOT_FOUND");
            error.setMessage("Заявка не найдена");
        } else {
            LoanOrder order = loanOrder.get();
            if (order.getStatus() == "IN_PROGRESS") {
                delete(order);
                return  ResponseEntity.status(HttpStatus.OK).body(null);
            } else {
                error.setCode("ORDER_IMPOSSIBLE_TO_DELETE");
                error.setMessage("Невозможно удалить заявку");
            }
        }
        responseError.setError(error);
        return ResponseEntity.badRequest().body(responseError);
    }

    @Override
    public ResponseEntity<Object> findByOrderId(String orderId) {
        Optional<LoanOrder> loanOrder = loanOrderRepository.findByOrderId(orderId);
        if (loanOrder.isEmpty()){
            Error error = new Error("ORDER_NOT_FOUND", "Заявка не найдена");
            ResponseError response = new ResponseError(error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            GetStatusOrderDataResponse data = new GetStatusOrderDataResponse(loanOrder.get());
            ResponseData response = new ResponseData(data);
            return ResponseEntity.ok(response);
        }
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
