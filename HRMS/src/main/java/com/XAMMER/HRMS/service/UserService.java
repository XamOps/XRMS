package com.XAMMER.HRMS.service;


import java.util.Set;


import com.XAMMER.HRMS.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    User findById(Long id);
    List<User> findAll();
    void save(User user);
    void delete(Long id);
    List<String> getAllUsernames();

    List<User> getUpcomingBirthdays(); // New method declaration
    User getUserByUsername(String username);
}