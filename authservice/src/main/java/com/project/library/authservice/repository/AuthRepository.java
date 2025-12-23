package com.project.library.authservice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.project.library.authservice.dto.SignupDto;
import com.project.library.authservice.exception.UserAlreadyExistsException;
import com.project.library.authservice.exception.UserNotFoundException;
import com.project.library.authservice.exception.DatabaseException;

@Repository
public class AuthRepository {

    private final JdbcTemplate _JdbcTemplate;

    @Autowired
    public AuthRepository(JdbcTemplate jdbcTemplate) {
        this._JdbcTemplate = jdbcTemplate;
    }

    // ------------------- SIGNUP -------------------
    public void SignUp(SignupDto cred) {
        try {
            String query = "INSERT INTO users (u_name, u_email, u_password) VALUES (?, ?, ?)";
            _JdbcTemplate.update(query, cred.get_Name(), cred.get_Email(), cred.get_Password());
        } catch (DataAccessException ex) {
            // Assuming unique constraint on email
            if (ex.getMessage().toLowerCase().contains("duplicate") || ex.getMessage().toLowerCase().contains("unique")) {
                throw new UserAlreadyExistsException("User with email " + cred.get_Email() + " already exists");
            }
            throw new DatabaseException("Failed to signup user: " + ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException("Unexpected error during signup: " + ex.getMessage());
        }
    }

   public String getPasswordByEmail(String email) {
    try {
        String query = "SELECT u_password FROM users WHERE u_email = ?";
        // This will throw IncorrectResultSizeDataAccessException if no row
        String password = _JdbcTemplate.queryForObject(query, String.class, email);

        if (password == null || password.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        return password;

    } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
        // No user found for this email
        throw new UserNotFoundException("User not found with email: " + email);

    } catch (DataAccessException ex) {
        // Other DB issues
        throw new DatabaseException("Database error while fetching user password: " + ex.getMessage());
    } catch (Exception ex) {
        throw new DatabaseException("Unexpected error while fetching user password: " + ex.getMessage());
    }
}

}
