package ru.mtc.loanservice.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtc.loanservice.model.LoanOrder;
import ru.mtc.loanservice.model.Tariff;
import ru.mtc.loanservice.repository.LoanOrderRepository;
import ru.mtc.loanservice.repository.TariffRepository;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequestMapping("/loan-service")
public class TariffController {
    private final TariffRepository tariffRepository;
    private final LoanOrderRepository loanOrderRepository;

    public TariffController(TariffRepository tariffRepository, LoanOrderRepository loanOrderRepository) {
        this.tariffRepository = tariffRepository;
        this.loanOrderRepository = loanOrderRepository;
    }

    @GetMapping("/getTariffs")
    public ResponseEntity<HashMap<String, Object>> getTariffs() {
        List<Tariff> tariffs = tariffRepository.findAll();
        HashMap<String, Object> response = new HashMap<>();
        response.put("data", Collections.singletonMap("tariffs", tariffs));
        return ResponseEntity.ok(response);
    }
    @GetMapping("/getStatusOrder")
    public ResponseEntity<HashMap<String, Object>> getTariffs(@RequestParam String orderId) {
        Optional<LoanOrder> loanOrder = loanOrderRepository.findByOrderId(orderId);
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
        try{
            Tariff tariff = tariffRepository.findById(tariffId);
        } catch (EmptyResultDataAccessException ex){
            dataMap.put("code", "TARIFF_NOT_FOUND");
            dataMap.put("message", "Тариф не найден");
            response.put("data", dataMap);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        List<LoanOrder> loanOrders = loanOrderRepository.findByUserId(userId);
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
        dataMap.put("orderId", createNewLoanOrder(userId,tariffId));
        response.put("data", dataMap);
        return  ResponseEntity.ok(response);
    }
//58ee13fa-6d52-4322-8416-582ecdd947b0
    @DeleteMapping("/deleteOrder")
    public ResponseEntity<HashMap<String,Object>> deleteOrder(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        String orderId = request.get("orderId").toString();
        System.out.println(userId);
        System.out.println(orderId);
        Optional<LoanOrder> loanOrder = loanOrderRepository.findByUserIdAndOrderId((userId), orderId);
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
                loanOrderRepository.delete(order);
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
    private String createNewLoanOrder(Long userId, Long tariffId) {
        LoanOrder newOrder = new LoanOrder();
        newOrder.setOrderId(String.valueOf(UUID.randomUUID()));
        newOrder.setUserId(userId);
        newOrder.setTariff(tariffRepository.findById(tariffId));
        newOrder.setCreditRating(Math.round((Math.random() * 0.8 + 0.1) * 100.0) / 100.0);
        newOrder.setStatus("IN_PROGRESS");
        newOrder.setTimeUpdate(Timestamp.valueOf(LocalDateTime.now()));
        newOrder.setTimeInsert(Timestamp.valueOf(LocalDateTime.now()));
        loanOrderRepository.save(newOrder);
        return newOrder.getOrderId();
    }
}
