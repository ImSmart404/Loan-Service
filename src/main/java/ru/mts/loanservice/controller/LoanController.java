package ru.mts.loanservice.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.mts.loanservice.model.*;
import ru.mts.loanservice.service.LoanOrderService;
import ru.mts.loanservice.service.TariffService;
import java.util.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/loan-service")
public class LoanController {

    private final TariffService tariffService;

    private final LoanOrderService loanOrderService;


    @GetMapping("/getTariffs")
    public ResponseEntity<ResponseData> getTariffs() {
        return tariffService.findAll();
    }
    @GetMapping("/getStatusOrder")
    public ResponseEntity<Object> getTariffs(@RequestParam String orderId) {
        return  loanOrderService.findByOrderId(orderId);
    }
    @PostMapping("/order")
    public ResponseEntity<Object> postOrder(@RequestBody Map<String,Long> requestMap)  {
        Long tariffId = requestMap.get("tariffId");
        Long userId = requestMap.get("userId");
        return loanOrderService.findByUserId(userId,tariffId);

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
        return loanOrderService.findByUserIdAndOrderId(userId,orderId);
    }

    @GetMapping
    public String getPage(){
        return "LoanService";
    }
}
