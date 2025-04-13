package ru.mtuci.loanservice.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mtuci.loanservice.DTO.BaseDto;
import ru.mtuci.loanservice.service.LoanOrderService;
import ru.mtuci.loanservice.service.TariffService;

import java.util.Map;


@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/loan-service")
public class LoanController {

    private final TariffService tariffService;
    private final LoanOrderService loanOrderService;

    @GetMapping(path = "/getTariffs", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @CircuitBreaker(name = "unstableApiBreaker")
    public ResponseEntity<BaseDto> getTariffs() {
        return tariffService.findAll();
    }

    @GetMapping(path = "/getStatusOrder", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @CircuitBreaker(name = "unstableApiBreaker")
    public ResponseEntity<BaseDto> getStatusOrder(@RequestParam String orderId) {
        return loanOrderService.findByOrderId(orderId);
    }

    @PostMapping(path = "/order", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @CircuitBreaker(name = "unstableApiBreaker")
    public ResponseEntity<BaseDto> postOrder(@RequestBody Map<String, Long> requestMap) {
        Long tariffId = requestMap.get("tariffId");
        Long userId = requestMap.get("userId");
        return loanOrderService.findByUserId(userId, tariffId);
    }

    @DeleteMapping(path = "/deleteOrder", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @CircuitBreaker(name = "unstableApiBreaker")
    public ResponseEntity<BaseDto> deleteOrder(@RequestBody Map<String, String> request) {
        Long userId = Long.parseLong(request.get("userId"));
        String orderId = request.get("orderId");
        return loanOrderService.findByUserIdAndOrderId(userId, orderId);
    }

    @GetMapping
    public String getPage(){
        return "LoanService";
    }
}
