package com.project.library.apigateway.repository;

import com.project.library.apigateway.model.ServiceCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ServiceCredentialsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final class ServiceCredentialsRowMapper implements RowMapper<ServiceCredentials> {
        @Override
        public ServiceCredentials mapRow(ResultSet rs, int rowNum) throws SQLException {
            ServiceCredentials creds = new ServiceCredentials();
            creds.setServiceName(rs.getString("service_name"));
            creds.setUsername(rs.getString("username"));
            creds.setPassword(rs.getString("password"));
            creds.setSharedSecret(rs.getString("shared_secret"));
            return creds;
        }
    }

    public ServiceCredentials getByServiceName(String serviceName) {
        String sql = "SELECT * FROM service_credentials WHERE service_name = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{serviceName}, new ServiceCredentialsRowMapper());
    }
}
