package com.project.library.Users.service;

import com.project.library.Users.dto.UserDTO;
import com.project.library.Users.model.User;
import com.project.library.Users.irepository.IUserRepository;
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

    // CREATE
    public UserDTO addUser(UserDTO dto) {
        User user = modelMapper.map(dto, User.class);
        User created = userRepository.addUser(user);
        return modelMapper.map(created, UserDTO.class);
    }

    public Map<String, Object> getUsersPaginated(int page, int size) {
        long totalElements = userRepository.countUsers();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<UserDTO> users = userRepository.getUsersPaginated(page, size).stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", totalPages);
        response.put("totalElements", totalElements);
        response.put("users", users);
        return response;
    }

    // READ
    public List<UserDTO> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(String userId) {
        User u = userRepository.getUserById(userId);
        return u != null ? modelMapper.map(u, UserDTO.class) : null;
    }

    public List<UserDTO> getUsersByName(String firstName, String lastName) {
        return userRepository.getUsersByName(firstName, lastName).stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .collect(Collectors.toList());
    }

    // UPDATE
    public boolean updateUser(String userId, UserDTO dto) {
        User existing = userRepository.getUserById(userId);
        if (existing == null)
            return false;

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setPhoneNumber(dto.getPhoneNumber());

        return userRepository.updateUser(existing);
    }

    // DELETE
    public boolean deleteUserById(String userId) {
        return userRepository.deleteUserById(userId);
    }
}
