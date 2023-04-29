package ru.mtc.loanservice;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.mtc.loanservice.model.LoanOrder;
import ru.mtc.loanservice.repository.LoanOrderRepository;

@Component
public class LoanOrderScheduler {

    @Autowired
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
            loanOrderRepository.save(order);
        }
    }
}