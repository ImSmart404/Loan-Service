package ru.mts.loanservice;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.mts.loanservice.model.LoanOrder;
import ru.mts.loanservice.repository.LoanOrderRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@AllArgsConstructor
public class LoanOrderScheduler {

    private LoanOrderRepository loanOrderRepository;

    @Scheduled(fixedDelay = 120000)
    public void updateInProgressOrders() {
        List<LoanOrder> inProgressOrders = loanOrderRepository.findByStatus("IN_PROGRESS");
        Random random = new Random();

        for (LoanOrder order : inProgressOrders) {
            if (random.nextBoolean()) {
                order.setStatus("APPROVED");
            } else {
                order.setStatus("REFUSED");
            }
            order.setTimeUpdate(Timestamp.valueOf(LocalDateTime.now()));
            loanOrderRepository.update(order);
        }
    }
}