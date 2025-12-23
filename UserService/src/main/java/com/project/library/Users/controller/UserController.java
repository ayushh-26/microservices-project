package com.project.library.Users.controller;

import com.project.library.Users.dto.UserDTO;
import com.project.library.Users.dto.UserResponseDTO;
import com.project.library.Users.exception.*;
import com.project.library.Users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ---------------- ADD USER ----------------
    @PostMapping("/add")
    public ResponseEntity<UserResponseDTO> addUser(@RequestBody UserDTO dto) {
        UserResponseDTO response = new UserResponseDTO();
        try {
            UserDTO created = userService.addUser(dto);
            response.setUserId(created.getUserId());
            response.set_status("Success");
            return ResponseEntity.ok(response);
        } catch (ValidationException ex) {
            response.set_status("Validation Failed: " + ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (UserAlreadyExistsException ex) {
            response.set_status("Conflict: " + ex.getMessage());
            return ResponseEntity.status(409).body(response);
        } catch (DatabaseException ex) {
            response.set_status("DB Error: " + ex.getMessage());
            return ResponseEntity.status(503).body(response);
        } catch (Exception ex) {
            response.set_status("Unknown Error: " + ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ---------------- GET USER BY ID ----------------
@GetMapping("/id/{userId}")
public ResponseEntity<UserResponseDTO> getById(@PathVariable String userId) {
    UserResponseDTO response = new UserResponseDTO();
    try {
        UserDTO u = userService.getUserById(userId);

        response.setUserId(u.getUserId());
        response.setFirstName(u.getFirstName());
        response.setLastName(u.getLastName());
        response.setEmail(u.getEmail());
        response.setPhoneNumber(u.getPhoneNumber());
        response.set_status("Success");

        return ResponseEntity.ok(response);

    } catch (UserNotFoundException ex) {
        response.set_status(ex.getMessage());
        return ResponseEntity.status(404).body(response);
    } catch (DatabaseException ex) {
        response.set_status("DB Error: " + ex.getMessage());
        return ResponseEntity.status(503).body(response);
    } catch (Exception ex) {
        response.set_status("Unknown Error: " + ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}

    // ---------------- GET ALL USERS ----------------
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserDTO> list = userService.getAllUsers();
            return ResponseEntity.ok(list);
        } catch (DatabaseException ex) {
            return ResponseEntity.status(503).body("DB Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Unknown Error: " + ex.getMessage());
        }
    }

    // ---------------- UPDATE USER ----------------
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable String userId, @RequestBody UserDTO dto) {
        UserResponseDTO response = new UserResponseDTO();
        try {
            userService.updateUser(userId, dto);
            response.setUserId(userId);
            response.set_status("Success");
            return ResponseEntity.ok(response);
        } catch (ValidationException ex) {
            response.set_status("Validation Failed: " + ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (UserNotFoundException ex) {
            response.set_status(ex.getMessage());
            return ResponseEntity.status(404).body(response);
        } catch (DatabaseException ex) {
            response.set_status("DB Error: " + ex.getMessage());
            return ResponseEntity.status(503).body(response);
        } catch (Exception ex) {
            response.set_status("Unknown Error: " + ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ---------------- DELETE USER ----------------
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<UserResponseDTO> deleteUser(@PathVariable String userId) {
        UserResponseDTO response = new UserResponseDTO();
        try {
            userService.deleteUserById(userId);
            response.setUserId(userId);
            response.set_status("Success");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException ex) {
            response.set_status(ex.getMessage());
            return ResponseEntity.status(404).body(response);
        } catch (DatabaseException ex) {
            response.set_status("DB Error: " + ex.getMessage());
            return ResponseEntity.status(503).body(response);
        } catch (Exception ex) {
            response.set_status("Unknown Error: " + ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ---------------- CHECK USER EXISTS ----------------
    @GetMapping("/exists/{userId}")
    public ResponseEntity<?> userExists(@PathVariable String userId) {
        try {
            boolean exists = userService.getUserById(userId) != null;
            return ResponseEntity.ok(exists);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.ok(false);
        } catch (DatabaseException ex) {
            return ResponseEntity.status(503).body("DB Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Unknown Error: " + ex.getMessage());
        }
    }

    // ---------------- PAGINATION ----------------
    @GetMapping("/nextpage")
    public ResponseEntity<?> getUsersPaginated(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            Map<String, Object> paginated = userService.getUsersPaginated(page, size);
            return ResponseEntity.ok(paginated);
        } catch (ValidationException ex) {
            return ResponseEntity.badRequest().body("Validation Failed: " + ex.getMessage());
        } catch (DatabaseException ex) {
            return ResponseEntity.status(503).body("DB Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Unknown Error: " + ex.getMessage());
        }
    }
}
