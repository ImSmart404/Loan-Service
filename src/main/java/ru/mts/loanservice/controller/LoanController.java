package ru.mts.loanservice.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import ru.mts.loanservice.model.*;
import ru.mts.loanservice.model.Error;
import ru.mts.loanservice.service.LoanOrderService;
import ru.mts.loanservice.service.TariffService;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    public ResponseEntity<ResponseData> getTariffs() {
        List<Tariff> tariffs = tariffService.findAll();
        GetTariffsDataResponse data = new GetTariffsDataResponse(tariffs);
        ResponseData response = new ResponseData(data);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/getStatusOrder")
    public ResponseEntity<Object> getTariffs(@RequestParam String orderId) {
        Optional<LoanOrder> loanOrder = loanOrderService.findByOrderId(orderId);
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
    @PostMapping("/order")
    public ResponseEntity<Object> postOrder(@RequestBody Map<String,Long> requestMap)  {
        Long tariffId = requestMap.get("tariffId");
        Long userId = requestMap.get("userId");
        tariffService.findById(tariffId);
        List<LoanOrder> loanOrders = loanOrderService.findByUserId(userId);
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
                            if (Duration.between(order.getTimeUpdate().toLocalDateTime(), LocalDateTime.now()).toMinutes() < 2){
                                Error refusedError = new Error("TRY_LATER", "Попробуйте оставить заявку позже");
                                ResponseError refusedResponse = new ResponseError(refusedError);
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(refusedResponse);
                            }
                        }
                    }
                }
            }
            PostOrderIdDataResponse data = new PostOrderIdDataResponse(loanOrderService.save(userId,tariffId));
            ResponseData response = new ResponseData(data);
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
    public ResponseEntity<Object> deleteOrder(@RequestBody Map<String, Object> request) {
        Long userId = Long.parseLong( request.get("userId").toString());
        String orderId = request.get("orderId").toString();
        Error error = new Error();
        ResponseError responseError = new ResponseError();
        Optional<LoanOrder> loanOrder = loanOrderService.findByUserIdAndOrderId((userId), orderId);
        if (loanOrder.isEmpty()) {
            error.setCode("ORDER_NOT_FOUND");
            error.setMessage("Заявка не найдена");
        } else {
            LoanOrder order = loanOrder.get();
            if (order.getStatus() == "IN_PROGRESS") {
                loanOrderService.delete(order);
                return  ResponseEntity.status(HttpStatus.OK).body(null);
            } else {
                error.setCode("ORDER_IMPOSSIBLE_TO_DELETE");
                error.setMessage("Невозможно удалить заявку");
            }
        }
        responseError.setError(error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    @GetMapping
    public String getPage(){
        return "LoanService";
    }
}
