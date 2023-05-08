package ru.mts.loanservice.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.mts.loanservice.model.Tariff;
import java.util.List;


@Repository
@AllArgsConstructor
public class TariffRepository {


    private JdbcTemplate jdbcTemplate;
    public List<Tariff> findAll() {
        String sql = "SELECT * FROM tariff";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Tariff(
                rs.getLong("id"),
                rs.getString("type"),
                rs.getString("interest_rate")
        ));
    }
    public Tariff findById(Long id){
        String sql = "SELECT * FROM tariff WHERE id = ?";
            return  (jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> new Tariff(
                    rs.getLong("id"),
                    rs.getString("type"),
                    rs.getString("interest_rate")
            )));
    }
}
