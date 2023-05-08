package ru.mts.loanservice.model;

@lombok.Data
public class ResponseData {
    Object data;
    public ResponseData(Object data){
        this.data = data;
    }
}
