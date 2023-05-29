package org.example.tracker.dao.repository;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {

    @Query("""
            select e from EmployeeEntity e
            where (
            upper(e.firstName) like upper(concat('%', ?1, '%'))
            or upper(e.lastName) like upper(concat('%', ?1, '%'))
            or upper(e.middleName) like upper(concat('%', ?1, '%'))
            or upper(e.upn) like upper(concat('%', ?1, '%'))
            or upper(e.email) like upper(concat('%', ?1, '%'))
            )
            and e.status = 'ACTIVE'
            """)
    List<EmployeeEntity> findByNameEmailUpnActiveStatus(String query);


    EmployeeEntity findByUpnIgnoreCase(String upn);
}
