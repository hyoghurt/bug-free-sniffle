package org.example.tracker.repository;

import org.example.tracker.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer>,
        JpaSpecificationExecutor<EmployeeEntity> {

    @Transactional
    @Modifying
    @Query("update EmployeeEntity e set e.status = 'DELETED' where e.id = ?1")
    int updateSetStatusDeletedById(Integer id);

    Optional<EmployeeEntity> findByUpn(String upn);
}
