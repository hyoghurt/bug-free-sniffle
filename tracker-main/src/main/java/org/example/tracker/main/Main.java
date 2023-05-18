package org.example.tracker.main;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.enums.EmployeeStatus;
import org.example.tracker.dao.repository.impl.EmployeeRepositoryJdbc;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        EmployeeRepositoryJdbc repository = new EmployeeRepositoryJdbc();

        getAll(repository);

        createEmployee(repository, 1);
        createEmployee(repository, 2);
        createEmployee(repository, 3);

        getById(repository, 3);
        getAll(repository);

        delete(repository, 3);
        getAll(repository);

        update(repository, 2);
        getAll(repository);
    }

    private static void update(EmployeeRepositoryJdbc repository, Integer id) {
        System.out.println("--------------------------------");
        System.out.println("UPDATE ID " + id);
        EmployeeEntity update = createEntity("222");
        update.setId(id);
        update.setStatus(EmployeeStatus.DELETED);
        int result = repository.update(update);
        System.out.println("repository return: " + result);
        System.out.println("--------------------------------");
    }

    private static void getById(EmployeeRepositoryJdbc repository, Integer id) {
        System.out.println("--------------------------------");
        System.out.println("GET ID " + id);
        EmployeeEntity result = repository.getById(id);
        System.out.println("repository return: " + result);
        System.out.println("--------------------------------");
    }

    private static void delete(EmployeeRepositoryJdbc repository, Integer id) {
        System.out.println("--------------------------------");
        System.out.println("DELETE ID " + id);
        int result = repository.deleteById(id);
        System.out.println("repository return: " + result);
        System.out.println("--------------------------------");
    }

    private static void getAll(EmployeeRepositoryJdbc repository) {
        System.out.println("--------------------------------");
        System.out.println("GET ALL");
        List<EmployeeEntity> all0 = repository.getAll();
        System.out.println(all0);
        System.out.println("--------------------------------");
    }

    private static void createEmployee(EmployeeRepositoryJdbc repository, Integer prefix) {
        System.out.println("--------------------------------");
        System.out.println("CREATE " + prefix);
        int id = repository.create(createEntity(String.valueOf(prefix)));
        System.out.println("repository return: " + id);
        System.out.println("--------------------------------");
    }

    private static EmployeeEntity createEntity(String prefix) {
        EmployeeEntity entity = new EmployeeEntity();
        entity.setStatus(EmployeeStatus.ACTIVE);
        entity.setUpn(prefix + "upn@com.com");
        entity.setEmail(prefix + "hello@com.com");
        entity.setPosition(prefix + "position_test");
        entity.setFirstName(prefix + "first_name_test");
        entity.setLastName(prefix + "last_name_test");
        entity.setMiddleName(prefix + "middle_name_test");
        return entity;
    }
}
