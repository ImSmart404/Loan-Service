package ru.mtuci.loanservice.exception;

public class TariffNotFound extends RuntimeException {

    public TariffNotFound(String message) {
        super(message);
    }
}
