package org.example.tracker.main;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.repository.EmployeeRepository;
import org.example.tracker.dao.repository.impl.EmployeeRepositoryImpl;

import java.util.List;

public class Main {

  public static void main(String[] args) {
    EmployeeRepository repository = new EmployeeRepositoryImpl();

    System.out.println("--------------------------------");
    System.out.println("GET ALL");
    List<EmployeeEntity> all0 = repository.getAll();
    System.out.println(all0);
    System.out.println("--------------------------------");

    repository.create(createEntity("1"));
    repository.create(createEntity("2"));
    repository.create(createEntity("3"));

    System.out.println("--------------------------------");
    System.out.println("GET BY ID 2");
    EmployeeEntity byId = repository.getById(2);
    System.out.println(byId);
    System.out.println("--------------------------------");

    System.out.println("--------------------------------");
    System.out.println("GET ALL");
    List<EmployeeEntity> all = repository.getAll();
    System.out.println(all);
    System.out.println("--------------------------------");

    System.out.println("--------------------------------");
    System.out.println("DELETE ID 3 AND GET ALL");
    repository.deleteById(3);
    List<EmployeeEntity> all3 = repository.getAll();
    System.out.println(all3);
    System.out.println("--------------------------------");

    System.out.println("--------------------------------");
    System.out.println("UPDATE ID 2 AND GET ALL");
    EmployeeEntity update = createEntity("222");
    update.setId(2);
    repository.update(update);
    List<EmployeeEntity> all2 = repository.getAll();
    System.out.println(all2);
    System.out.println("--------------------------------");

  }

  private static EmployeeEntity createEntity(String prefix) {
    EmployeeEntity entity = new EmployeeEntity();
    entity.setId(Integer.parseInt(prefix));
    entity.setEmail(prefix+ "hello@com.com");
    entity.setPosition(prefix + "position_test");
    entity.setFirstName(prefix + "first_name_test");
    entity.setLastName(prefix+ "last_name_test");
    entity.setMiddleName(prefix+ "middle_name_test");
    return entity;
  }
}
