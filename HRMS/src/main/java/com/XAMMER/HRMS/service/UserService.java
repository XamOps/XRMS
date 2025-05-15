package com.XAMMER.HRMS.service;

// import com.XAMMER.HRMS.model.User;
// import com.XAMMER.HRMS.repository.UserRepository;

// import jakarta.transaction.Transactional;
// import lombok.extern.slf4j.Slf4j;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// import java.util.Collections;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Optional;
// @Slf4j
// @Service
// public class UserService {
import java.util.Set;

//     private final UserRepository userRepository;
//     private final BCryptPasswordEncoder passwordEncoder;

//     @Autowired
//     public UserService(UserRepository userRepository) {
//         this.userRepository = userRepository;
//         this.passwordEncoder = new BCryptPasswordEncoder(); // or inject via bean
//     }

//     public List<User> getAllUsers() {
//         log.info("userRepository->"+userRepository.findAll());
//         return userRepository.findAll();
//     }

//     public Optional<User> getUserByUsername(String username) {
//         log.info("getUserByUsername->"+userRepository.findByUsername(username));
//         return userRepository.findByUsername(username);
//     }

//     public User createUser(User user) {
//         user.setPassword(passwordEncoder.encode(user.getPassword()));
//         return userRepository.save(user);
//     }

//     public boolean existsByUsername(String username) {
//         return userRepository.existsByUsername(username);
//     }
// }

//package com.XAMMER.HRMS.service;



// 

import com.XAMMER.HRMS.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    
    List<User> findAll();
    void save(User user);
    List<String> getAllUsernames();

    List<User> getUpcomingBirthdays(); // New method declaration
    User getUserByUsername(String username);
    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);
    List<User> getAllUsers();
    void saveUser(User user);
      void delete(Long id);
      List<String> searchUsernames(String query);
      List<String> findUsernamesContaining(String query);

}