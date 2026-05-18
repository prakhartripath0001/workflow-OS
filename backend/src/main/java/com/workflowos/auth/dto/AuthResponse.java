package com.workflowos.auth.dto;

import java.util.UUID;

public class AuthResponse {
    private UUID   id;
    private String name;
    private String email;
    private String token;
    private String message;

    public AuthResponse() {}

    public AuthResponse(UUID id, String name, String email, String token, String message) {
        this.id      = id;
        this.name    = name;
        this.email   = email;
        this.token   = token;
        this.message = message;
    }

    public UUID   getId()      { return id; }
    public String getName()    { return name; }
    public String getEmail()   { return email; }
    public String getToken()   { return token; }
    public String getMessage() { return message; }
}

