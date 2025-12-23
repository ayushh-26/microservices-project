package com.project.library.Users.service;

import com.project.library.Users.dto.UserDTO;
import com.project.library.Users.exception.*;
import com.project.library.Users.irepository.IUserRepository;
import com.project.library.Users.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final IUserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(IUserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    // ---------------- CREATE ----------------
    public UserDTO addUser(UserDTO dto) {
        if(dto.getEmail() == null || dto.getEmail().trim().isEmpty())
            throw new ValidationException("Email cannot be empty");
        if(dto.getFirstName() == null || dto.getFirstName().trim().isEmpty())
            throw new ValidationException("First Name cannot be empty");

        List<User> existingUsers = userRepository.getUsersByName(dto.getFirstName(), dto.getLastName());
        if(existingUsers.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(dto.getEmail()))) {
            throw new UserAlreadyExistsException("Email " + dto.getEmail() + " already exists");
        }

        try {
            User user = modelMapper.map(dto, User.class);
            User created = userRepository.addUser(user);
            if (created == null) throw new DatabaseException("Unable to create user in database");
            return modelMapper.map(created, UserDTO.class);
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Database error while adding user: " + e.getMessage());
        }
    }

    // ---------------- READ ALL ----------------
    public List<UserDTO> getAllUsers() {
        try {
            return userRepository.getAllUsers()
                    .stream()
                    .map(u -> modelMapper.map(u, UserDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DatabaseException("Error fetching users: " + e.getMessage());
        }
    }

    // ---------------- READ BY ID ----------------
    public UserDTO getUserById(String userId) {
        User user = userRepository.getUserById(userId);
        if (user == null)
            throw new UserNotFoundException("User not found with ID: " + userId);
        return modelMapper.map(user, UserDTO.class);
    }

    // ---------------- UPDATE ----------------
    public void updateUser(String userId, UserDTO dto) {
        User existing = userRepository.getUserById(userId);
        if (existing == null)
            throw new UserNotFoundException("User not found with ID: " + userId);

        if(dto.getEmail() == null || dto.getEmail().trim().isEmpty())
            throw new ValidationException("Email cannot be empty");

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setPhoneNumber(dto.getPhoneNumber());

        try {
            if (!userRepository.updateUser(existing))
                throw new DatabaseException("Failed to update user in database");
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Database error while updating user: " + e.getMessage());
        }
    }

    // ---------------- DELETE ----------------
    public void deleteUserById(String userId) {
        Boolean deleted = userRepository.deleteUserById(userId);
        if (!deleted) throw new UserNotFoundException("User not found with ID: " + userId);
    }

    // ---------------- PAGINATION ----------------
    public Map<String, Object> getUsersPaginated(int page, int size) {
        if(page < 0 || size <= 0)
            throw new ValidationException("Invalid pagination parameters");

        try {
            long totalElements = userRepository.countUsers();
            int totalPages = (int) Math.ceil((double) totalElements / size);

            List<UserDTO> users = userRepository.getUsersPaginated(page, size)
                    .stream()
                    .map(u -> modelMapper.map(u, UserDTO.class))
                    .collect(Collectors.toList());

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("page", page);
            result.put("size", size);
            result.put("totalPages", totalPages);
            result.put("totalElements", totalElements);
            result.put("users", users);

            return result;
        } catch (Exception e) {
            throw new DatabaseException("Pagination DB error: " + e.getMessage());
        }
    }
}
