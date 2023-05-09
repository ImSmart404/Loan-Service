package ru.mts.loanservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.mts.loanservice.DTO.ErrorDTO;
import ru.mts.loanservice.DTO.ResponseErrorDTO;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ResponseErrorDTO> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {
        ErrorDTO errorDTO = new ErrorDTO("TARIFF_NOT_FOUND", "Тариф не найден");
        ResponseErrorDTO responseErrorDTO = new ResponseErrorDTO(errorDTO);
        log.warn("EmptyResultDataAccessException: {}", ex.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseErrorDTO);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {

        ErrorDTO errorDTO = new ErrorDTO(ex.getMessage(), "Произошла непридвиденная ошибка на сервере");
        ResponseErrorDTO refusedResponse = new ResponseErrorDTO(errorDTO);
        log.error("Exception: {}", ex.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(refusedResponse);
    }
}
