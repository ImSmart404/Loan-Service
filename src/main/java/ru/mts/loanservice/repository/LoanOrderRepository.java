package ru.mts.loanservice.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mts.loanservice.model.LoanOrder;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface LoanOrderRepository extends JpaRepository<LoanOrder, Long> {

    @Query("SELECT l FROM LoanOrder l join l.tariff t WHERE l.userId = :userId")
    List<LoanOrder> findByUserId(@Param("userId") Long userId);

    @Query("SELECT l FROM LoanOrder l join l.tariff t WHERE l.userId = :userId AND l.orderId = :orderId")
    Optional<LoanOrder> findByUserIdAndOrderId(@Param("userId") Long userId, @Param("orderId") String orderId);

    @Query("SELECT l FROM LoanOrder l join l.tariff t WHERE l.orderId = :orderId")
    Optional<LoanOrder> findByOrderId(@Param("orderId") String orderId);

    @Query("SELECT l FROM LoanOrder l join l.tariff t WHERE l.status = :status")
    List<LoanOrder> findByStatus(@Param("status") String status);

    @Modifying
    @Transactional
    @Query("UPDATE LoanOrder l SET l.updateTime = :updateTime, l.status = :status WHERE l.id = :id")
    void updateTimeAndStatus(@Param("id") Long id, @Param("updateTime") Timestamp updateTime, @Param("status") String status);
}
