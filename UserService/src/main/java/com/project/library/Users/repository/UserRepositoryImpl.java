package com.project.library.Users.repository;

import com.project.library.Users.irepository.IUserRepository;
import com.project.library.Users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UserRepositoryImpl implements IUserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Auto-generate userId like U001, U002, ...
    private String generateUserId() {
        String sql = "SELECT user_id FROM users ORDER BY user_id DESC LIMIT 1";
        List<String> ids = jdbcTemplate.queryForList(sql, String.class);
        String lastId = ids.isEmpty() ? "U000" : ids.get(0);
        int num = Integer.parseInt(lastId.substring(1)) + 1;
        return String.format("U%03d", num);
    }

    @Override
    public User addUser(User user) {
        String userId = generateUserId();
        user.setUserId(userId);
        String sql = "INSERT INTO users (user_id, first_name, last_name, email, phone_number) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUserId(), user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getPhoneNumber());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    @Override
    public User getUserById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, new Object[] { userId }, new UserRowMapper());
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public List<User> getUsersByName(String firstName, String lastName) {
        String sql = "SELECT * FROM users WHERE first_name = ? AND last_name = ?";
        return jdbcTemplate.query(sql, new Object[] { firstName, lastName }, new UserRowMapper());
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone_number = ? WHERE user_id = ?";
        int updated = jdbcTemplate.update(sql, user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getPhoneNumber(), user.getUserId());
        return updated > 0;
    }

    @Override
    public boolean deleteUserById(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        int deleted = jdbcTemplate.update(sql, userId);
        return deleted > 0;
    }

    @Override
    public List<User> getUsersPaginated(int page, int size) {
        int offset = page * size;
        String sql = "SELECT * FROM users LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new Object[] { size, offset }, new UserRowMapper());
    }

    @Override
    public long countUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

}
