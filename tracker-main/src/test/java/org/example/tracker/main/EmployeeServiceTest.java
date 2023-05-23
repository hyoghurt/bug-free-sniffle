package org.example.tracker.main;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.repository.EmployeeRepository;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.service.EmployeeService;
import org.example.tracker.service.exception.DuplicateUniqueFieldException;
import org.example.tracker.service.exception.EmployeeAlreadyDeletedException;
import org.example.tracker.service.exception.RequiredFieldException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = "delete from employees")
class EmployeeServiceTest extends BaseTest{
    @Autowired
    EmployeeService service;
    @Autowired
    EmployeeRepository repository;

    @Test
    void create() {
        EmployeeReq request = genEmployeeReq("upn", "first", "last");
        EmployeeResp resp = service.create(request);
        assertEquals(resp.getStatus(), EmployeeStatus.ACTIVE);
    }

    @Test
    void delete() {
        EmployeeReq request = genEmployeeReq("upn", "first", "last");
        EmployeeResp resp = service.create(request);
        service.delete(resp.getId());
        EmployeeResp actual = service.getById(resp.getId());
        assertEquals(actual.getStatus(), EmployeeStatus.DELETED);
    }

    @Test
    void update() {
        EmployeeReq request = genEmployeeReq("upn", "first", "last");
        EmployeeResp resp = service.create(request);

        EmployeeReq update = genEmployeeReq("upnUpdate", "firstUpdate", "lastUpdate");
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
    void createDuplicateUpnException() {
        EmployeeReq request = genEmployeeReq("upnDuplicate", "firstDuplicate", "lastDuplicate");
        service.create(request);

        EmployeeReq request2 = genEmployeeReq("upnDuplicate", "firstDuplicate", "lastDuplicate");
        assertThrows(DuplicateUniqueFieldException.class, () -> service.create(request2));
    }

    @Test
    void updateDuplicateUpnException() {
        EmployeeReq request = genEmployeeReq("upnDuplicate", "firstDuplicate", "lastDuplicate");
        service.create(request);

        EmployeeReq request2 = genEmployeeReq("upn2Duplicate", "first2Duplicate", "last2Duplicate");
        EmployeeResp save = service.create(request2);

        EmployeeReq update = genEmployeeReq("upnDuplicate", "first2Duplicate", "last2Duplicate");

        assertThrows(DuplicateUniqueFieldException.class, () -> service.update(save.getId(), update));
    }

    @Test
    void requiredFieldException() {
        EmployeeReq request = EmployeeReq.builder()
                .upn("upnDuplicate")
                .build();

        assertThrows(RequiredFieldException.class, () -> service.create(request));
    }

    @Test
    void alreadyDeletedException() {
        EmployeeEntity entity = EmployeeEntity.builder()
                .upn("deleted")
                .firstName("deletedName")
                .lastName("deletedName")
                .status(EmployeeStatus.DELETED)
                .build();

        EmployeeEntity save = repository.save(entity);
        EmployeeReq request = EmployeeReq.builder()
                .upn("deletedUpdate")
                .firstName("deletedNameUpdate")
                .lastName("deletedNameUpdate")
                .build();

        assertThrows(EmployeeAlreadyDeletedException.class, () -> service.update(save.getId(), request));
    }

    @Test
    void find() {
        List<EmployeeEntity> entities = new ArrayList<>();

        System.out.println("CREATE Employee ENTITIES--------------------------");
        entities.add(genEmployeeEntity("testName", "last", null, null, null, null, EmployeeStatus.ACTIVE));
        entities.add(genEmployeeEntity("name", "lastTest", null, null, null, null, EmployeeStatus.ACTIVE));
        entities.add(genEmployeeEntity("name", "last", "midtestname", null, null, null, EmployeeStatus.ACTIVE));
        entities.add(genEmployeeEntity("name", "last", null, "testmail", null, null, EmployeeStatus.ACTIVE));
        entities.add(genEmployeeEntity("name", "last", null, null, "testupn", null, EmployeeStatus.ACTIVE));
        entities.add(genEmployeeEntity("name", "last", null, null, null, "postest", EmployeeStatus.ACTIVE));
        entities.add(genEmployeeEntity("testFirst", "testLast", "testMiddle", "testEmail", "testUpn", "testPosition", EmployeeStatus.DELETED));

        entities.forEach(e -> {
            EmployeeEntity save = repository.save(e);
            e.setId(save.getId());
        });

        String search = "tEst";
        List<EmployeeResp> actual = service.find(search);
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
}