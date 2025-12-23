package com.project.library.Users.dto;

public class UserResponseDTO {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private String _status;

    public UserResponseDTO() {}

    public String getUserId() { 
        return userId; 
    }
    public void setUserId(String userId) { 
        this.userId = userId; 
    }

    public String getFirstName() { 
        return firstName; 
    }
    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }

    public String getLastName() { 
        return lastName; 
    }
    public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }

    public String getEmail() { 
        return email; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }

    public String getPhoneNumber() { 
        return phoneNumber; 
    }
    public void setPhoneNumber(String phoneNumber) { 
        this.phoneNumber = phoneNumber; 
    }

    public String get_status() { 
        return _status; 
    }
    public void set_status(String _status) { 
        this._status = _status; 
    }
}
