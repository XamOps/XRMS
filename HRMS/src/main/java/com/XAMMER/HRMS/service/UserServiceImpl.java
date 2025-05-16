package com.XAMMER.HRMS.service;

import com.XAMMER.HRMS.model.User;
import com.XAMMER.HRMS.repository.UserRepository;
import com.XAMMER.HRMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // @Override
    // public Optional<User> findByUsername(String username) {
    //     return userRepository.findByUsername(username);
    // }
@Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id); // Returns Optional<User>
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getUpcomingBirthdays() {
        LocalDate today = LocalDate.now();
        LocalDate todayPlusTwoWeeks = today.plus(12, ChronoUnit.MONTHS);

        return userRepository.findAll().stream()
                .filter(user -> {
                    if (user.getBirthDate() == null) {
                        return false;
                    }
                    LocalDate birthdayThisYear = user.getBirthDate().withYear(today.getYear());
                    if (birthdayThisYear.isBefore(today)) {
                        birthdayThisYear = birthdayThisYear.plusYears(1);
                    }
                    return !birthdayThisYear.isBefore(today) && !birthdayThisYear.isAfter(todayPlusTwoWeeks);
                })
                .sorted((u1, u2) -> {
                    LocalDate b1 = u1.getBirthDate().withYear(today.getYear());
                    if (b1.isBefore(today)) b1 = b1.plusYears(1);
                    LocalDate b2 = u2.getBirthDate().withYear(today.getYear());
                    if (b2.isBefore(today)) b2 = b2.plusYears(1);
                    return b1.compareTo(b2);
                })
                .collect(Collectors.toList());
    }

    // @Override
    // public List<String> getAllUsernames() {
    //     return userRepository.findAll().stream()
    //             .map(User::getUsername) // Assuming your User entity has a getUsername() method
    //             .collect(Collectors.toList());
    // }

    @Override
    public User getUserByUsername(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserByUsername'");
    }

    @Override
    public List<User> getAllUsers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllUsers'");
    }

    @Override
    public void saveUser(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveUser'");
    }
    @Override
public List<String> searchUsernames(String query) {
    return userRepository.findByUsernameContainingIgnoreCase(query)
                         .stream()
                         .map(User::getUsername)
                         .collect(Collectors.toList());
}

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<String> getAllUsernames() {
        return userRepository.findAll().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findUsernamesContaining(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query).stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }

}