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
    // List<User> findByRoles(Roles roleAdmin);
        // List<User> findByRole(String role); // New method to find by role
            List<User> findByRoles(Roles role); // CORRECT METHOD DEFINITION


    List<User> findByManagerId(Long managerId);
        List<User> findByUsernameContainingIgnoreCase(String query);
        List<User> findByFirstNameContainingIgnoreCaseAndDepartmentContainingIgnoreCase(String query,
                String department);
        List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                String query, String query2, String query3);
        List<User> findByDepartmentContainingIgnoreCase(String department);
        Optional<User> findByEmail(String email);
        List<User> findByManager(User userToDelete);
   
    
}