package ru.mts.loanservice.controller;

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
import ru.mts.loanservice.DTO.BaseDto;
import ru.mts.loanservice.service.LoanOrderService;
import ru.mts.loanservice.service.TariffService;

import java.util.Map;


@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/loan-service", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class LoanController {

    private final TariffService tariffService;
    private final LoanOrderService loanOrderService;

    @GetMapping("/getTariffs")
    public ResponseEntity<BaseDto> getTariffs() {
        return tariffService.findAll();
    }

    @GetMapping("/getStatusOrder")
    public ResponseEntity<BaseDto> getStatusOrder(@RequestParam String orderId) {
        return loanOrderService.findByOrderId(orderId);
    }
    @PostMapping("/order")
    public ResponseEntity<BaseDto> postOrder(@RequestBody Map<String,Long> requestMap)  {
        Long tariffId = requestMap.get("tariffId");
        Long userId = requestMap.get("userId");
        return loanOrderService.findByUserId(userId,tariffId);

    }
    @DeleteMapping("/deleteOrder")
    public ResponseEntity<BaseDto> deleteOrder(@RequestBody Map<String, Object> request) {
        Long userId = Long.parseLong( request.get("userId").toString());
        String orderId = request.get("orderId").toString();
        return loanOrderService.findByUserIdAndOrderId(userId,orderId);
    }
    @GetMapping
    public String getPage(){
        return "LoanService";
    }
}
