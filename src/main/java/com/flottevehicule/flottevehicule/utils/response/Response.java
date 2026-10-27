package com.flottevehicule.flottevehicule.utils.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private Status status;
    private String message;
    private T data;

    public Response() {}

    public Response(Status status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(Status.SUCCESS, null, data);
    }

    public static <T> Response<T> success(String message, T data) {
        return new Response<>(Status.SUCCESS, message, data);
    }

    public static <T> Response<T> error(String message) {
        return new Response<>(Status.ERROR, message, null);
    }

    public Status getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}