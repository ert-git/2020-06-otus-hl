package ru.otus.hl.repository;

public class RepositoryException extends RuntimeException {
    public RepositoryException(Exception ex) {
        super(ex);
    }
}
