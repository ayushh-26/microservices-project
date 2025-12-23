package com.project.library.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.library.authservice.dto.SignupDto;
import com.project.library.authservice.dto.AuthDto;
import com.project.library.authservice.dto.AuthResponseDto;
import com.project.library.authservice.repository.AuthRepository;
import com.project.library.authservice.exception.*;

@Service
public class AuthService {

    private final AuthRepository _AuthRepository;
    private final PasswordEncoder _PasswordEncoder;

    @Autowired
    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this._AuthRepository = authRepository;
        this._PasswordEncoder = passwordEncoder;
    }

    // ------------------- SIGNUP -------------------
    public boolean SignUp(SignupDto cred, AuthResponseDto response) {
        try {
            // Hash password
            cred.set_Password(_PasswordEncoder.encode(cred.get_Password()));

            // Repository call (throws UserAlreadyExistsException if duplicate)
            _AuthRepository.SignUp(cred);

            response.set_Email(cred.get_Email());
            response.set_Status("Success");
            return true;

        } catch (UserAlreadyExistsException e) {
            response.set_Email(cred.get_Email());
            response.set_Status("Signup failed: " + e.getMessage());
            throw e;

        } catch (DatabaseException e) {
            response.set_Email(cred.get_Email());
            response.set_Status("Signup failed: " + e.getMessage());
            throw e;

        } catch (Exception e) {
            response.set_Email(cred.get_Email());
            response.set_Status("Signup failed: " + e.getMessage());
            throw new DatabaseException("Failed to signup user: " + e.getMessage());
        }
    }

    // ------------------- AUTHENTICATE -------------------
    public boolean Authenticate(AuthDto cred) {
        try {
            String passwordFromDB = _AuthRepository.getPasswordByEmail(cred.get_Email());

            // If password does not match
            if (!_PasswordEncoder.matches(cred.get_Password(), passwordFromDB)) {
                throw new InvalidCredentialsException("Incorrect password");
            }

            return true;

        } catch (UserNotFoundException e) {
            throw e;

        } catch (InvalidCredentialsException e) {
            throw e;

        } catch (Exception e) {
            throw new DatabaseException("Failed to authenticate user: " + e.getMessage());
        }
    }
}
