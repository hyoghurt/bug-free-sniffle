package org.example.tracker.dao.repository.impl;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.enums.EmployeeStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepositoryJdbc {

    private Connection getNewConnection() throws SQLException {
        String URL = "jdbc:postgresql://localhost:5432/tracker";
        String USERNAME = "user";
        String PASSWORD = "example";

        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public int create(EmployeeEntity entity) {
        String query = "INSERT INTO employees("
                + "email,"
                + "upn,"
                + "position,"
                + "status,"
                + "first_name,"
                + "last_name,"
                + "middle_name"
                + ") VALUES(?,?,?,?,?,?,?)";

        try (Connection connection = getNewConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            int i = 1;
            statement.setString(i++, entity.getEmail());
            statement.setString(i++, entity.getUpn());
            statement.setString(i++, entity.getPosition());
            statement.setObject(i++, entity.getStatus(), Types.OTHER);
            statement.setString(i++, entity.getFirstName());
            statement.setString(i++, entity.getLastName());
            statement.setString(i, entity.getMiddleName());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            throw new RuntimeException("database id no generated");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(EmployeeEntity entity) {
        String query = "UPDATE employees "
                + "SET email = ?, "
                + "upn = ?, "
                + "position = ?, "
                + "status = ?, "
                + "first_name = ?, "
                + "last_name = ?, "
                + "middle_name = ? "
                + "WHERE id = ?";

        try (Connection connection = getNewConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            int i = 1;
            statement.setString(i++, entity.getEmail());
            statement.setString(i++, entity.getUpn());
            statement.setString(i++, entity.getPosition());
            statement.setObject(i++, entity.getStatus(), Types.OTHER);
            statement.setString(i++, entity.getFirstName());
            statement.setString(i++, entity.getLastName());
            statement.setString(i++, entity.getMiddleName());
            statement.setInt(i, entity.getId());
            return statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public EmployeeEntity getById(Integer id) {
        String query = "SELECT * FROM employees WHERE id = ?";

        try (Connection connection = getNewConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return (rs.next()) ? toEmployeeEntity(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<EmployeeEntity> getAll() {
        String query = "SELECT * FROM employees";
        List<EmployeeEntity> list = new ArrayList<>();

        try (Connection connection = getNewConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(toEmployeeEntity(rs));
                }
                return list;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteById(Integer id) {
        String query = "DELETE FROM employees WHERE id = ?";

        try (Connection connection = getNewConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            return statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static EmployeeEntity toEmployeeEntity(ResultSet rs) throws SQLException {
        EmployeeEntity entity = new EmployeeEntity();
        entity.setId(rs.getInt("id"));
        entity.setEmail(rs.getString("email"));
        entity.setUpn(rs.getString("upn"));
        entity.setPosition(rs.getString("position"));
        entity.setStatus(EmployeeStatus.valueOf(rs.getString("status")));
        entity.setFirstName(rs.getString("first_name"));
        entity.setLastName(rs.getString("last_name"));
        entity.setMiddleName(rs.getString("middle_name"));
        return entity;
    }
}
