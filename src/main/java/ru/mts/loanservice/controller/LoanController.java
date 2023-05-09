package ru.mts.loanservice.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.mts.loanservice.DTO.ResponseDataDTO;
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
    public ResponseEntity<ResponseDataDTO> getTariffs() {
        return tariffService.findAll();
    }
    @GetMapping("/getStatusOrder")
    public ResponseEntity<Object> getStatusOrder(@RequestParam String orderId) {
        return  loanOrderService.findByOrderId(orderId);
    }
    @PostMapping("/order")
    public ResponseEntity<Object> postOrder(@RequestBody Map<String,Long> requestMap)  {
        Long tariffId = requestMap.get("tariffId");
        Long userId = requestMap.get("userId");
        return loanOrderService.findByUserId(userId,tariffId);

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
