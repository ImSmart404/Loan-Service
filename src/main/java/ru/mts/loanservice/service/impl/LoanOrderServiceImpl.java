package ru.mts.loanservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.mts.loanservice.DTO.*;
import ru.mts.loanservice.DTO.ErrorDTO;
import ru.mts.loanservice.model.*;
import ru.mts.loanservice.repository.LoanOrderRepository;
import ru.mts.loanservice.repository.TariffRepository;
import ru.mts.loanservice.service.LoanOrderService;
import ru.mts.loanservice.service.TariffService;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class LoanOrderServiceImpl implements LoanOrderService {

    private final LoanOrderRepository loanOrderRepository;
    private final TariffRepository tariffRepository;
    private final TariffService tariffService;

    @Override
    public ResponseEntity<Object> findByUserId(Long userId, Long tariffId) {
        tariffService.findById(tariffId);                                                 //если тариф не найден возвращает ошибку EmptyResultDataAccessException
        List<LoanOrder> loanOrders = loanOrderRepository.findByUserId(userId);
        for (LoanOrder order : loanOrders) {
            if (order.getTariff().getId() == tariffId) {
                switch (order.getStatus()) {
                    case "IN_PROGRESS":
                        ErrorDTO inProgressError = new ErrorDTO("LOAN_CONSIDERATION", "Тариф в процессе рассмотрения");
                        ResponseErrorDTO inProgressResponse = new ResponseErrorDTO(inProgressError);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(inProgressResponse);
                    case "APPROVED":
                        ErrorDTO approvedErrorDTO = new ErrorDTO("LOAN_ALREADY_APPROVED", "Тариф уже одобрен");
                        ResponseErrorDTO approvedResponse = new ResponseErrorDTO(approvedErrorDTO);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(approvedResponse);
                    case "REFUSED": {
                        if (Duration.between(order.getTimeUpdate().toLocalDateTime(), LocalDateTime.now()).toMinutes() < 2) {
                            ErrorDTO refusedError = new ErrorDTO("TRY_LATER", "Попробуйте оставить заявку позже");
                            ResponseErrorDTO refusedResponse = new ResponseErrorDTO(refusedError);
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(refusedResponse);
                        }
                    }
                }
            }
        }
        PostOrderIdDataResponseDTO data = new PostOrderIdDataResponseDTO(save(userId,tariffId).getOrderId());
        ResponseDataDTO response = new ResponseDataDTO(data);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> findByUserIdAndOrderId(Long userId, String orderId) {
        Optional<LoanOrder> loanOrder = loanOrderRepository.findByUserIdAndOrderId(userId,orderId);
        ErrorDTO error = new ErrorDTO();
        ResponseErrorDTO responseError = new ResponseErrorDTO();
        if (loanOrder.isEmpty()) {
            error.setCode("ORDER_NOT_FOUND");
            error.setMessage("Заявка не найдена");
        } else {
            LoanOrder order = loanOrder.get();
            if (order.getStatus() == "IN_PROGRESS") {
                delete(order);
                return  ResponseEntity.status(HttpStatus.OK).body(null);
            } else {
                error.setCode("ORDER_IMPOSSIBLE_TO_DELETE");
                error.setMessage("Невозможно удалить заявку");
            }
        }
        responseError.setError(error);
        return ResponseEntity.badRequest().body(responseError);
    }

    @Override
    public ResponseEntity<Object> findByOrderId(String orderId) {
        Optional<LoanOrder> loanOrder = loanOrderRepository.findByOrderId(orderId);
        if (loanOrder.isEmpty()){
            ErrorDTO error = new ErrorDTO("ORDER_NOT_FOUND", "Заявка не найдена");
            ResponseErrorDTO response = new ResponseErrorDTO(error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            GetStatusOrderDataResponseDTO data = new GetStatusOrderDataResponseDTO(loanOrder.get().getStatus());
            ResponseDataDTO response = new ResponseDataDTO(data);
            return ResponseEntity.ok(response);
        }
    }

    @Override
    public List<LoanOrder> findByStatus(String status) {
        List<LoanOrder> loanOrders = loanOrderRepository.findByStatus(status);

        return loanOrders;
    }

    @Override
    public void delete(LoanOrder order) {
        loanOrderRepository.delete(order);
    }

    @Override
    public LoanOrder save(Long userId, Long tariffId) {
        LoanOrder newOrder = new LoanOrder();
        newOrder.setOrderId(String.valueOf(UUID.randomUUID()));
        newOrder.setUserId(userId);
        newOrder.setTariff(tariffRepository.findById(tariffId));
        newOrder.setCreditRating(Math.round((Math.random() * 0.8 + 0.1) * 100.0) / 100.0);
        newOrder.setStatus("IN_PROGRESS");
        newOrder.setTimeUpdate(Timestamp.valueOf(LocalDateTime.now()));
        newOrder.setTimeInsert(Timestamp.valueOf(LocalDateTime.now()));
        loanOrderRepository.save(newOrder);
        return newOrder;
    }
}
