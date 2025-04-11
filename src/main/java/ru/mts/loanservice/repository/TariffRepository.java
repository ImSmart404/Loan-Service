package ru.mts.loanservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mts.loanservice.model.Tariff;

public interface TariffRepository extends JpaRepository<Tariff, Long> {

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM Tariff t WHERE t.id = id")
    Boolean isTariffExistsById(Long id);
}
