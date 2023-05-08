package ru.mts.loanservice.model;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
public class ResponseData {
    Object data;
    public ResponseData(Object data){
        this.data = data;
    }
}
