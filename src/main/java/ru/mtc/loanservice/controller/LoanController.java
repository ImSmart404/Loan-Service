package ru.mtc.loanservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.mtc.loanservice.model.LoanOrder;
import ru.mtc.loanservice.model.Tariff;
import ru.mtc.loanservice.service.LoanOrderService;
import ru.mtc.loanservice.service.TariffService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/loan-service")
public class LoanController {

    private final TariffService tariffService;

    private final LoanOrderService loanOrderService;


    @GetMapping("/getTariffs")
    public ResponseEntity<HashMap<String, Object>> getTariffs() {
        List<Tariff> tariffs = tariffService.findAll();
        HashMap<String, Object> response = new HashMap<>();
        response.put("data", Collections.singletonMap("tariffs", tariffs));
        return ResponseEntity.ok(response);
    }
    @GetMapping("/getStatusOrder")
    public ResponseEntity<HashMap<String, Object>> getTariffs(@RequestParam String orderId) {
        Optional<LoanOrder> loanOrder = loanOrderService.findByOrderId(orderId);
        HashMap<String, Object> response = new HashMap<>();
        if (loanOrder.isEmpty()){
            HashMap<String, Object> errorMap = new HashMap<>();
            errorMap.put("code", "ORDER_NOT_FOUND");
            errorMap.put("message", "Заявка не найдена");
            response.put("error", errorMap);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            LoanOrder order = loanOrder.get();
            response.put("data", Collections.singletonMap("orderStatus", order.getStatus()));
            return ResponseEntity.ok(response);
        }
    }
    @PostMapping("/order")
    public ResponseEntity<HashMap<String, Object>> postOrder(@RequestBody Map<String,Long> requestMap){
        Long tariffId = requestMap.get("tariffId");
        Long userId = requestMap.get("userId");
        HashMap<String, Object> response = new HashMap<>();
        HashMap<String, String> dataMap = new HashMap<>();
        tariffService.findById(tariffId);
        List<LoanOrder> loanOrders = loanOrderService.findByUserId(userId);
            for (LoanOrder order : loanOrders) {
                if (order.getTariff().getId() == tariffId) {
                    switch (order.getStatus()) {
                        case "IN_PROGRESS":
                            dataMap.put("code", "LOAN_CONSIDERATION");
                            dataMap.put("message", "Тариф в процессе рассмотрения");
                            response.put("data", dataMap);
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                        case "APPROVED":
                            dataMap.put("code", "LOAN_ALREADY_APPROVED");
                            dataMap.put("message", "Тариф уже одобрен");
                            response.put("data", dataMap);
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                        case "REFUSED": {
                            if (Duration.between(order.getTimeUpdate().toLocalDateTime(), LocalDateTime.now()).toMinutes() < 2){
                                dataMap.put("code", "TRY_LATER");
                                dataMap.put("message", "Попробуйте оставить заявку позже");
                                response.put("data", dataMap);
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                            }
                        }
                    }
                }
            }
            dataMap.put("orderId", loanOrderService.save(userId, tariffId));
            response.put("data", dataMap);
            return ResponseEntity.ok(response);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {
        Map<String, Object> errorMap = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        errorMap.put("code", "TARIFF_NOT_FOUND");
        errorMap.put("message", "Тариф не найден");
        response.put("data", errorMap);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        Map<String, Object> errorMap = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        errorMap.put("code", "SERVER_ERROR");
        errorMap.put("message", "Произошла непредвиденная ошибка на сервере");
        errorMap.put("error", ex.getMessage());
        response.put("data", errorMap);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @DeleteMapping("/deleteOrder")
    public ResponseEntity<HashMap<String,Object>> deleteOrder(@RequestBody Map<String, Object> request) {
        Long userId = Long.parseLong( request.get("userId").toString());
        String orderId = request.get("orderId").toString();
        Optional<LoanOrder> loanOrder = loanOrderService.findByUserIdAndOrderId((userId), orderId);
        HashMap<String, Object> response = new HashMap<>();
        if (loanOrder.isEmpty()) {
            HashMap<String, Object> errorMap = new HashMap<>();
            errorMap.put("code", "ORDER_NOT_FOUND");
            errorMap.put("message", "Заявка не найдена");
            response.put("error", errorMap);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            LoanOrder order = loanOrder.get();
            if (order.getStatus() == "IN_PROGRESS") {
                loanOrderService.delete(order);
                return  ResponseEntity.status(HttpStatus.OK).body(null);
            } else {
                HashMap<String, Object> errorMap = new HashMap<>();
                errorMap.put("code", "ORDER_IMPOSSIBLE_TO_DELETE");
                errorMap.put("message", "Невозможно удалить заявку");
                response.put("error", errorMap);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }
    }

    @GetMapping
    public String getPage(){
        return "LoanService";
    }
}
