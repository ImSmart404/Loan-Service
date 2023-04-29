package ru.mtc.loanservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtc.loanservice.model.LoanOrder;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanOrderRepository extends JpaRepository<LoanOrder, Long> {
    List<LoanOrder> findByUserId(Long userId);
    Optional<LoanOrder> findByUserIdAndOrderId(Long userId, String orderId);
    Optional<LoanOrder>  findByOrderId(String orderId);

    List<LoanOrder> findByStatus(String status);
}
