package com.taskManagementSystem.exception;

import com.taskManagementSystem.enums.Status;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ExceptionResponse {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String message;
    private String path;

    private ExceptionResponse(Builder builder){
        this.timestamp = builder.timestamp;
        this.status = builder.status;
        this.message = builder.message;
        this.path = builder.path;
    }

    public static class Builder{
        private LocalDateTime timestamp;
        private HttpStatus status;
        private String message;
        private String path;

        public Builder timestamp(LocalDateTime timestamp){
            this.timestamp = timestamp;
            return this;
        }

        public Builder status(HttpStatus status){
            this.status = status;
            return this;
        }

        public Builder message(String message){
            this.message = message;
            return this;
        }

        public Builder path(String path){
            this.path = path;
            return this;
        }

        public ExceptionResponse build(){
            return new ExceptionResponse(this);
        }
    }
}
