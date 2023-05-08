package ru.mts.loanservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseError {

        Error error;
        public ResponseError(Error error) {
            this.error= error ;
        }

}
