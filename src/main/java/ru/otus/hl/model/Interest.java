package ru.otus.hl.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Interest {

    private long id;
    private String name;
    private List<User> users;

    public Interest(String name) {
        this.name = name;
    }

    public Interest(long id, String name) {
        this.id = id;
        this.name = name;
    }
}