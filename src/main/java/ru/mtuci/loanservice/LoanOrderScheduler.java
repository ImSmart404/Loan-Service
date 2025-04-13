package ru.mtuci.loanservice;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.mtuci.loanservice.model.LoanOrder;
import ru.mtuci.loanservice.repository.LoanOrderRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@AllArgsConstructor
public class LoanOrderScheduler {

    private LoanOrderRepository loanOrderRepository;

    @Scheduled(fixedDelay = 120000)
    @Transactional
    public void updateInProgressOrders() {
        List<LoanOrder> inProgressOrders = loanOrderRepository.findByStatus("IN_PROGRESS");
        Random random = new Random();

        inProgressOrders.forEach(order -> {
            String status;
            if (random.nextBoolean()) {
                status = "APPROVED";
            } else {
                status = "REFUSED";
            }
            loanOrderRepository.updateTimeAndStatus(order.getId(), Timestamp.valueOf(LocalDateTime.now()), status);
        });
    }
}