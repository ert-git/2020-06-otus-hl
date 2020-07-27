package ru.otus.hl.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuth implements Serializable {

    protected long id;
    private String login;
    private String password;
}