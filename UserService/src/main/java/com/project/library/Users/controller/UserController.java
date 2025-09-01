package com.project.library.Users.controller;

import com.project.library.Users.dto.UserDTO;
import com.project.library.Users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ------------------- CREATE -------------------
    /*
     * POST http://localhost:9001/users/add
     * Body (JSON):
     * {
     * "firstName":"Ayush",
     * "lastName":"Sehrawat",
     * "email":"ayush.new@example.com",
     * "phoneNumber":"9876543215"
     * }
     */
    @PostMapping("/add")
    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.addUser(dto));
    }

    // ------------------- READ -------------------
    // GET all users
    // GET http://localhost:9001/users/all
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET user by ID
    // GET http://localhost:9001/users/id/U001
    @GetMapping("/id/{userId}")
    public ResponseEntity<UserDTO> getById(@PathVariable String userId) {
        UserDTO u = userService.getUserById(userId);
        return u != null ? ResponseEntity.ok(u) : ResponseEntity.notFound().build();
    }

    // GET users by name
    // GET http://localhost:9001/users/name?firstName=Ayush&lastName=Sehrawat
    @GetMapping("/name")
    public ResponseEntity<List<UserDTO>> getByName(@RequestParam String firstName, @RequestParam String lastName) {
        return ResponseEntity.ok(userService.getUsersByName(firstName, lastName));
    }

    // ------------------- UPDATE -------------------
    /*
     * PUT http://localhost:9001/users/update/U001
     * Body (JSON):
     * {
     * "firstName":"AyushUpdated",
     * "lastName":"Sehrawat",
     * "email":"ayush.updated@example.com",
     * "phoneNumber":"9876543210"
     * }
     */
    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable String userId, @RequestBody UserDTO dto) {
        boolean updated = userService.updateUser(userId, dto);
        return updated ? ResponseEntity.ok("User updated successfully.") : ResponseEntity.notFound().build();
    }

    // ------------------- DELETE -------------------
    // DELETE http://localhost:9001/users/delete/U001
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        boolean deleted = userService.deleteUserById(userId);
        return deleted ? ResponseEntity.ok("User deleted successfully.") : ResponseEntity.notFound().build();
    }

    // ------------------- CHECK IF USER EXISTS (FOR ISSUE SERVICE)
    // -------------------
    /*
     * GET http://localhost:9001/users/exists/U001
     * Response: true / false
     */
    @GetMapping("/exists/{userId}")
    public ResponseEntity<Boolean> userExists(@PathVariable String userId) {
        boolean exists = userService.getUserById(userId) != null;
        return ResponseEntity.ok(exists);
    }
}
