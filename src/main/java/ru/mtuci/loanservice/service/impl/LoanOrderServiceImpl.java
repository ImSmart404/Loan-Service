package ru.mtuci.loanservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.mtuci.loanservice.DTO.BaseDto;
import ru.mtuci.loanservice.DTO.DeleteOrderDTO;
import ru.mtuci.loanservice.DTO.ErrorDTO;
import ru.mtuci.loanservice.DTO.GetStatusOrderDataResponseDTO;
import ru.mtuci.loanservice.DTO.PostOrderIdDataResponseDTO;
import ru.mtuci.loanservice.exception.TariffNotFound;
import ru.mtuci.loanservice.model.LoanOrder;
import ru.mtuci.loanservice.model.Tariff;
import ru.mtuci.loanservice.repository.LoanOrderRepository;
import ru.mtuci.loanservice.repository.TariffRepository;
import ru.mtuci.loanservice.service.LoanOrderService;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class LoanOrderServiceImpl implements LoanOrderService {

    private final LoanOrderRepository loanOrderRepository;
    private final TariffRepository tariffRepository;

    @Override
    public ResponseEntity<BaseDto> findByUserId(Long userId, Long tariffId) {
        if (!tariffRepository.isTariffExistsById(tariffId)) {
            //если тариф не найден возвращает ошибку TariffNotFound
            throw new TariffNotFound("Unlikely situation: tariff not found by id");
        }
        List<LoanOrder> loanOrders = loanOrderRepository.findByUserId(userId);
        for (LoanOrder order : loanOrders) {
            if (order.getTariff().getId().equals(tariffId)) {
                switch (order.getStatus()) {
                    case "IN_PROGRESS":
                        ErrorDTO inProgressError = new ErrorDTO(HttpStatus.BAD_REQUEST.value(), "Тариф в процессе рассмотрения");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(inProgressError);
                    case "APPROVED":
                        ErrorDTO approvedErrorDTO = new ErrorDTO(HttpStatus.BAD_REQUEST.value(), "Тариф уже одобрен");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(approvedErrorDTO);
                    case "REFUSED": {
                        if (Duration.between(order.getUpdateTime().toLocalDateTime(), LocalDateTime.now()).toMinutes() < 2) {
                            ErrorDTO refusedError = new ErrorDTO(HttpStatus.BAD_REQUEST.value(), "Попробуйте оставить заявку позже");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(refusedError);
                        }
                    }
                }
            }
        }
        PostOrderIdDataResponseDTO data = new PostOrderIdDataResponseDTO(save(userId, tariffId).getOrderId());
        return ResponseEntity.ok(data);
    }

    @Override
    public ResponseEntity<BaseDto> findByUserIdAndOrderId(Long userId, String orderId) {
        Optional<LoanOrder> loanOrder = loanOrderRepository.findByUserIdAndOrderId(userId, orderId);
        ErrorDTO error = new ErrorDTO();
        if (loanOrder.isEmpty()) {
            error.setCode(HttpStatus.BAD_REQUEST.value());
            error.setMessage("Заявка не найдена");
        } else {
            LoanOrder order = loanOrder.get();
            if (order.getStatus().equals("IN_PROGRESS")) {
                loanOrderRepository.delete(order);
                return ResponseEntity.status(HttpStatus.OK).body(new DeleteOrderDTO("Заявка успешно удалена"));
            } else {
                error.setCode(HttpStatus.BAD_REQUEST.value());
                error.setMessage("Невозможно удалить заявку");
            }
        }

        return ResponseEntity.badRequest().body(error);
    }

    @Override
    public ResponseEntity<BaseDto> findByOrderId(String orderId) {
        Optional<LoanOrder> loanOrder = loanOrderRepository.findByOrderId(orderId);
        if (loanOrder.isEmpty()) {
            ErrorDTO error = new ErrorDTO(HttpStatus.BAD_REQUEST.value(), "Заявка не найдена");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } else {
            GetStatusOrderDataResponseDTO data = new GetStatusOrderDataResponseDTO(loanOrder.get().getStatus());
            return ResponseEntity.ok(data);
        }
    }

    @Override
    public LoanOrder save(Long userId, Long tariffId) {
        Optional<Tariff> tariff = tariffRepository.findById(tariffId);

        LoanOrder newOrder = new LoanOrder();
        newOrder.setOrderId(String.valueOf(UUID.randomUUID()));
        newOrder.setUserId(userId);
        newOrder.setCreditRating(Math.round((Math.random() * 0.8 + 0.1) * 100.0) / 100.0);
        newOrder.setStatus("IN_PROGRESS");
        newOrder.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
        newOrder.setInsertTime(Timestamp.valueOf(LocalDateTime.now()));
        if (tariff.isPresent()) {
            newOrder.setTariff(tariff.get());
        } else {
            log.error("Unlikely situation: tariff not found by id");
            throw new TariffNotFound("Unlikely situation: tariff not found by id");
        }
        loanOrderRepository.save(newOrder);
        return newOrder;
    }
}
