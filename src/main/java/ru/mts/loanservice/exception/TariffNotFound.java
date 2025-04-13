package ru.mts.loanservice.exception;

public class TariffNotFound extends RuntimeException {

    public TariffNotFound(String message) {
        super(message);
    }
}
