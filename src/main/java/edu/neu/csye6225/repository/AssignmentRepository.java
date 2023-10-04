package edu.neu.csye6225.repository;

import edu.neu.csye6225.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {

    Assignment getAssignmentById(UUID id);
}
