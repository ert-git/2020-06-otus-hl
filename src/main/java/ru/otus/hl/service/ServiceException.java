package ru.otus.hl.service;

public class ServiceException extends RuntimeException {
    public ServiceException(Exception e) {
        super(e);
    }
}
