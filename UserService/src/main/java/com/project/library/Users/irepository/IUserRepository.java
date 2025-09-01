package com.project.library.Users.irepository;

import com.project.library.Users.model.User;
import java.util.List;

public interface IUserRepository {

    User addUser(User user); // Create

    List<User> getAllUsers(); // Read all

    User getUserById(String userId); // Read by ID

    List<User> getUsersByName(String firstName, String lastName); // Read by Name

    boolean updateUser(User user); // Update

    boolean deleteUserById(String userId); // Delete
}
