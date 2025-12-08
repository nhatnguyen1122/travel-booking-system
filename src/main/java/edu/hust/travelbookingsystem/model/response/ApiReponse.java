package edu.hust.travelbookingsystem.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiReponse<T> {
    private int code = 1000 ;
    private String message ;
    private T data ;

    public ApiReponse() {}

    public ApiReponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiReponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
