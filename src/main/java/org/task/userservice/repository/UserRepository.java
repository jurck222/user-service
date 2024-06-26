package org.task.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.task.userservice.entity.User;
import org.task.userservice.entity.UserNameSurname;
import org.task.userservice.enums.MedicalService;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query("SELECT new org.task.userservice.entity.UserNameSurname(u.id, u.firstName, u.lastName) FROM User u JOIN u.services s WHERE s = :service")
    Optional<List<UserNameSurname>> findUsersByService(@Param("service") MedicalService service);
}
