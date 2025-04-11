package ru.mts.loanservice.repository;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.mts.loanservice.model.LoanOrder;
import ru.mts.loanservice.model.Tariff;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class LoanOrderRepository  {
    private JdbcTemplate jdbcTemplate;
    public List<LoanOrder> findByUserId(Long userId){
        String sql = "SELECT loan_order.*, tariff.type, tariff.interest_rate " +
                "FROM loan_order " +
                "JOIN tariff ON loan_order.tariff_id = tariff.id " +
                "WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> new LoanOrder(
                rs.getLong("id"),
                rs.getString("order_id"),
                rs.getLong("user_id"),
                new Tariff(rs.getLong("tariff_id"), rs.getString("type"), rs.getString("interest_rate")),
                rs.getDouble("credit_rating"),
                rs.getString("status"),
                rs.getTimestamp("time_insert"),
                rs.getTimestamp("time_update")

        ));
    }
    public Optional<LoanOrder> findByUserIdAndOrderId(Long userId, String orderId) {
        String sql = "SELECT loan_order.*, tariff.type, tariff.interest_rate " +
                "FROM loan_order " +
                "JOIN tariff ON loan_order.tariff_id = tariff.id " +
                "WHERE user_id = ? AND order_id = ?";
        try {
        LoanOrder loanOrder = jdbcTemplate.queryForObject(sql, new Object[]{userId, orderId}, (rs, rowNum) -> new LoanOrder(
                rs.getLong("id"),
                rs.getString("order_id"),
                rs.getLong("user_id"),
                new Tariff(rs.getLong("tariff_id"), rs.getString("type"), rs.getString("interest_rate")),
                rs.getDouble("credit_rating"),
                rs.getString("status"),
                rs.getTimestamp("time_insert"),
                rs.getTimestamp("time_update")
        ));
            return Optional.of(loanOrder);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }
    public Optional<LoanOrder> findByOrderId(String orderId) {
        String sql = "SELECT loan_order.*, tariff.type, tariff.interest_rate " +
                "FROM loan_order " +
                "JOIN tariff ON loan_order.tariff_id = tariff.id " +
                "WHERE order_id = ?";
        try {
            LoanOrder loanOrder = jdbcTemplate.queryForObject(sql, new Object[]{orderId}, (rs, rowNum) -> new LoanOrder(
                    rs.getLong("id"),
                    rs.getString("order_id"),
                    rs.getLong("user_id"),
                    new Tariff(rs.getLong("tariff_id"), rs.getString("type"), rs.getString("interest_rate")),
                    rs.getDouble("credit_rating"),
                    rs.getString("status"),
                    rs.getTimestamp("time_insert"),
                    rs.getTimestamp("time_update")
            ));
            return Optional.of(loanOrder);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }
    public List<LoanOrder> findByStatus(String status) {
        String sql = "SELECT loan_order.*, tariff.type, tariff.interest_rate " +
                "FROM loan_order " +
                "JOIN tariff ON loan_order.tariff_id = tariff.id " +
                "WHERE status = ?";
        return jdbcTemplate.query(sql, new Object[]{status}, (rs, rowNum) -> new LoanOrder(
                rs.getLong("id"),
                rs.getString("order_id"),
                rs.getLong("user_id"),
                new Tariff(rs.getLong("tariff_id"), rs.getString("type"), rs.getString("interest_rate")),
                rs.getDouble("credit_rating"),
                rs.getString("status"),
                rs.getTimestamp("time_insert"),
                rs.getTimestamp("time_update")
        ));
    }

    public void save(LoanOrder loanOrder) {
        String sql = "INSERT INTO loan_order (order_id, user_id, tariff_id, credit_rating, status, time_insert, time_update) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, loanOrder.getOrderId(), loanOrder.getUserId(), loanOrder.getTariff().getId(),
                loanOrder.getCreditRating(), loanOrder.getStatus(), loanOrder.getTimeInsert(), loanOrder.getTimeUpdate());
    }

    public void delete(LoanOrder loanOrder) {
        String sql = "DELETE FROM loan_order WHERE id = ?";
        jdbcTemplate.update(sql, loanOrder.getId());
    }
    public void update(LoanOrder order) {
        String sql = "UPDATE loan_order SET status = ?, time_update = ? WHERE order_id = ?";
        jdbcTemplate.update(sql, order.getStatus(), order.getTimeUpdate(), order.getOrderId());
    }

}
