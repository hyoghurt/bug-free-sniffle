package org.example.tracker.main;

import org.example.tracker.dao.SearchFilter;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.repository.impl.EmployeeRepositoryJdbc;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.dto.project.ProjectStatus;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        EmployeeRepositoryJdbc repository = new EmployeeRepositoryJdbc();
        getAll(repository);

        // TEST CRUD
        int id = createEmployee(repository, 1);
        getById(repository, id);
        getAll(repository);

        update(repository, id);
        getAll(repository);

        delete(repository, id);
        getAll(repository);

        // TEST FILTER
        SearchFilter filter = SearchFilter.builder()
                .firstName("tes")
                .lastName("3")
                .projectName("NAME")
                .build();
        getByFilter(repository, filter);

        filter = SearchFilter.builder()
                .build();
        getByFilter(repository, filter);

        filter = SearchFilter.builder()
                .firstName("name")
                .build();
        getByFilter(repository, filter);

        filter = SearchFilter.builder()
                .firstName("first")
                .build();
        getByFilter(repository, filter);

        filter = SearchFilter.builder()
                .lastName("2")
                .projectName("2")
                .build();
        getByFilter(repository, filter);

        filter = SearchFilter.builder()
                .lastName("2")
                .projectStatus(ProjectStatus.DRAFT)
                .build();
        getByFilter(repository, filter);
    }

    private static void getByFilter(EmployeeRepositoryJdbc repository, SearchFilter filter) {
        System.out.println("--------------------------------");
        System.out.println("FILTER " + filter);
        List<EmployeeEntity> result = repository.getAllEmployeeByFilter(filter);
        System.out.println("repository return: " + result);
        System.out.println("repository return rows: " + result.size());
        System.out.println("--------------------------------");
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

    private static int createEmployee(EmployeeRepositoryJdbc repository, Integer prefix) {
        System.out.println("--------------------------------");
        System.out.println("CREATE " + prefix);
        int id = repository.create(createEntity(String.valueOf(prefix)));
        System.out.println("repository return: " + id);
        System.out.println("--------------------------------");
        return id;
    }

    private static EmployeeEntity createEntity(String prefix) {
        return EmployeeEntity.builder()
                .status(EmployeeStatus.ACTIVE)
                .upn(prefix + "upn@com.com")
                .email(prefix + "hello@com.com")
                .position(prefix + "position_test")
                .firstName(prefix + "first_name_test")
                .lastName(prefix + "last_name_test")
                .middleName(prefix + "middle_name_test")
                .build();
    }
}
