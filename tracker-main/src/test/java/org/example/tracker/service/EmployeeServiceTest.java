package org.example.tracker.service;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.repository.EmployeeRepository;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.service.exception.DuplicateUniqueFieldException;
import org.example.tracker.service.exception.EmployeeAlreadyDeletedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = "delete from employees")
class EmployeeServiceTest extends BaseTest {
    @Autowired
    EmployeeService service;
    @Autowired
    EmployeeRepository repository;

    @Test
    void create() {
        EmployeeResp resp = createRandomEmployee();
        EmployeeResp actual = service.getById(resp.getId());
        assertEquals(actual.getStatus(), EmployeeStatus.ACTIVE);
    }

    @Test
    void delete() {
        EmployeeResp resp = createRandomEmployee();
        service.delete(resp.getId());
        EmployeeResp actual = service.getById(resp.getId());
        assertEquals(actual.getStatus(), EmployeeStatus.DELETED);
    }

    @Test
    void update() {
        EmployeeResp resp = createRandomEmployee();

        EmployeeReq update = genEmployeeReq(UUID.randomUUID().toString(), "firstUpdate", "lastUpdate");
        service.update(resp.getId(), update);

        EmployeeResp actual = service.getById(resp.getId());
        EmployeeResp expected = EmployeeResp.builder()
                .id(resp.getId())
                .status(EmployeeStatus.ACTIVE)
                .upn(update.getUpn())
                .firstName(update.getFirstName())
                .lastName(update.getLastName())
                .build();

        assertEquals(actual, expected);
    }

    @Test
    void create_duplicateUpnException() {
        EmployeeResp resp = createRandomEmployee();
        EmployeeReq request = genEmployeeReq(resp.getUpn(), "firstDuplicate", "lastDuplicate");
        assertThrows(DuplicateUniqueFieldException.class, () -> service.create(request));
    }

    @Test
    void update_duplicateUpnException() {
        EmployeeResp resp = createRandomEmployee();
        EmployeeResp resp2 = createRandomEmployee();
        EmployeeReq update = genEmployeeReq(resp.getUpn(), resp2.getFirstName(), resp2.getLastName());
        assertThrows(DuplicateUniqueFieldException.class, () -> service.update(resp2.getId(), update));
    }

    @Test
    void update_alreadyDeletedException() {
        EmployeeResp resp = createRandomEmployee();
        service.delete(resp.getId());
        EmployeeReq request = genEmployeeReq(resp.getUpn(), "first", "last");
        assertThrows(EmployeeAlreadyDeletedException.class, () -> service.update(resp.getId(), request));
    }

    @Test
    void find() {
        List<EmployeeEntity> entities = new ArrayList<>();

        System.out.println("CREATE Employee ENTITIES--------------------------");
        entities.add(repository.save(genEmployeeEntity("testName", "last", null, null, null, null, EmployeeStatus.ACTIVE)));
        entities.add(repository.save(genEmployeeEntity("name", "lastTest", null, null, null, null, EmployeeStatus.ACTIVE)));
        entities.add(repository.save(genEmployeeEntity("name", "last", "midtestname", null, null, null, EmployeeStatus.ACTIVE)));
        entities.add(repository.save(genEmployeeEntity("name", "last", null, "testmail", null, null, EmployeeStatus.ACTIVE)));
        entities.add(repository.save(genEmployeeEntity("name", "last", null, null, "testupn", null, EmployeeStatus.ACTIVE)));
        entities.add(repository.save(genEmployeeEntity("name", "last", null, null, null, "postest", EmployeeStatus.ACTIVE)));
        entities.add(repository.save(genEmployeeEntity("testFirst", "testLast", "testMiddle", "testEmail", "testUpn", "testPosition", EmployeeStatus.DELETED)));

        String search = "tEst";
        List<EmployeeResp> actual = service.getAllByQuery(search);
        List<EmployeeResp> expected = entities.stream()
                .filter(e -> (
                                e.getFirstName().toUpperCase().contains(search.toUpperCase())
                                        || e.getLastName().toUpperCase().contains(search.toUpperCase())
                                        || (e.getMiddleName() != null && e.getMiddleName().toUpperCase().contains(search.toUpperCase()))
                                        || (e.getEmail() != null && e.getEmail().toUpperCase().contains(search.toUpperCase()))
                                        || (e.getUpn() != null && e.getUpn().toUpperCase().contains(search.toUpperCase()))
                        ) && (
                                e.getStatus().equals(EmployeeStatus.ACTIVE)
                        )
                ).map(modelMapper::toEmployeeResp).toList();
        assertEquals(expected, actual);
    }

    EmployeeResp createRandomEmployee() {
        EmployeeReq request = genEmployeeReq(UUID.randomUUID().toString(), "first", "last");
        return service.create(request);
    }
}