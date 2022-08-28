package com.org.authservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class User {
    private String id;
    private String email;
    private String username;

    @JsonIgnore
    private String password;
}
