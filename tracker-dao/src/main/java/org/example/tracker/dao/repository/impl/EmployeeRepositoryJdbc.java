package org.example.tracker.dao.repository.impl;

import org.example.tracker.dao.SearchFilter;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.dto.project.ProjectStatus;

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

    /**
     * изменяем статус на DELETED
     */
    public int deleteById(Integer id) {
        EmployeeEntity entity = getById(id);
        entity.setStatus(EmployeeStatus.DELETED);
        return update(entity);
    }

    public List<EmployeeEntity> getAllEmployeeByFilter(SearchFilter filter) {
        List<EmployeeEntity> list = new ArrayList<>();
        List<Object> objects = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT DISTINCT "
                + "employees.id, "
                + "employees.email, "
                + "employees.first_name, "
                + "employees.last_name, "
                + "employees.middle_name, "
                + "employees.position, "
                + "employees.upn, "
                + "employees.status "
                + "FROM employees "
                + "JOIN teams "
                + "ON employees.id = teams.employee_id "
                + "JOIN projects "
                + "ON projects.id = teams.project_id WHERE 1=1 ");

        // добавляем необходимые условия
        filter(filter.getFirstName(), "employees.first_name", stringBuilder, objects);
        filter(filter.getLastName(), "employees.last_name", stringBuilder, objects);
        filter(filter.getProjectName(), "projects.name", stringBuilder, objects);
        filter(filter.getProjectStatus(), "projects.status", stringBuilder, objects);

        try (Connection connection = getNewConnection();
             PreparedStatement statement = connection.prepareStatement(stringBuilder.toString())) {

            // вставляем значения условий
            for (int i = 0; i < objects.size(); i++) {
                int index = i + 1;
                Object obj = objects.get(i);
                if (obj instanceof ProjectStatus) {
                    statement.setObject(index, obj, Types.OTHER);
                } else {
                    statement.setObject(index, obj);
                }
            }

            // получаем результат
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

    private void filter(Object obj, String column, StringBuilder strBuild, List<Object> objects) {
        if (obj instanceof String str) {
            if (!str.isBlank()) {
                strBuild.append(String.format(" AND %s ILIKE ?", column));
                objects.add("%" + str + "%");
            }
        } else if (obj instanceof ProjectStatus status) {
            strBuild.append(String.format(" AND %s = ?", column));
            objects.add(status);
        }
    }

    private static EmployeeEntity toEmployeeEntity(ResultSet rs) throws SQLException {
        return EmployeeEntity.builder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .upn(rs.getString("upn"))
                .position(rs.getString("position"))
                .status(EmployeeStatus.valueOf(rs.getString("status")))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .middleName(rs.getString("middle_name"))
                .build();
    }
}
