package com.XAMMER.HRMS.repository;

import com.XAMMER.HRMS.model.Roles;
import com.XAMMER.HRMS.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import javax.management.relation.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // List<User> findByUsername(String username);
     Optional<User> findByUsername(String username);
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String firstName, String lastName, String username, String email);
    List<User> findByRoles(Roles roleAdmin);
    
    List<User> findByManagerId(Long managerId);
   
    
}