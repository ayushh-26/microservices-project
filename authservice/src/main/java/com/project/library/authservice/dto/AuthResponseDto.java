package com.project.library.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponseDto {

    @JsonProperty("Email")
    private String _Email;

    @JsonProperty("Status")
    private String _Status;

    public AuthResponseDto() {}

    public AuthResponseDto(String email, String status) {
        this._Email = email;
        this._Status = status;
    }

    public String get_Email() {
        return _Email;
    }

    public void set_Email(String email) {
        this._Email = email;
    }

    public String get_Status() {
        return _Status;
    }

    public void set_Status(String status) {
        this._Status = status;
    }
}
