package com.project.library.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.library.authservice.dto.SignupDto;
import com.project.library.authservice.dto.AuthDto;
import com.project.library.authservice.dto.AuthResponseDto;
import com.project.library.authservice.service.AuthService;
import com.project.library.authservice.exception.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService _AuthService;

    @Autowired
    public AuthController(AuthService authService) {
        this._AuthService = authService;
    }

    // ------------------- SIGNUP -------------------
    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> SignUp(@RequestBody SignupDto cred) {
        AuthResponseDto response = new AuthResponseDto();

        // Input validation
        if (cred.get_Email() == null || cred.get_Email().isEmpty()) {
            response.set_Email(null);
            response.set_Status("Email is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (cred.get_Password() == null || cred.get_Password().isEmpty()) {
            response.set_Email(cred.get_Email());
            response.set_Status("Password is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (cred.get_Name() == null || cred.get_Name().isEmpty()) {
            response.set_Email(cred.get_Email());
            response.set_Status("Name is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            boolean isSuccess = _AuthService.SignUp(cred, response);
            return ResponseEntity.ok(response);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        } catch (Exception e) {
            response.set_Status("Signup failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ------------------- AUTHENTICATE -------------------
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDto> Authenticate(@RequestBody AuthDto cred) {
        AuthResponseDto response = new AuthResponseDto();

        // Input validation
        if (cred.get_Email() == null || cred.get_Email().isEmpty()) {
            response.set_Email(null);
            response.set_Status("Email is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (cred.get_Password() == null || cred.get_Password().isEmpty()) {
            response.set_Email(cred.get_Email());
            response.set_Status("Password is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            boolean isAuthenticated = _AuthService.Authenticate(cred);
            response.set_Email(cred.get_Email());
            response.set_Status("Success");
            return ResponseEntity.ok(response);

        } catch (UserNotFoundException e) {
            response.set_Email(cred.get_Email());
            response.set_Status("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (InvalidCredentialsException e) {
            response.set_Email(cred.get_Email());
            response.set_Status("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (DatabaseException e) {
            response.set_Email(cred.get_Email());
            response.set_Status("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);

        } catch (Exception e) {
            response.set_Email(cred.get_Email());
            response.set_Status("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
