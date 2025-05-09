package com.XAMMER.HRMS.service;

import com.XAMMER.HRMS.model.User;
import com.XAMMER.HRMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
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

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
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
    public List<String> getAllUsernames() {
        return userRepository.findAll()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<User> getUpcomingBirthdays() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        LocalDate todayPlusTwoWeeks = today.plus(5, ChronoUnit.WEEKS); // Example: Birthdays in the next two weeks

        return userRepository.findAll().stream()
                .filter(user -> {
                    if (user.getBirthDate() == null) {
                        return false;
                    }
                    LocalDate birthdayThisYear = user.getBirthDate().withYear(today.getYear());
                    if (birthdayThisYear.isBefore(today)) {
                        birthdayThisYear = birthdayThisYear.plusYears(1); // Consider next year's birthday if already passed
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

    @Override
    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null); // Or throw an exception if you prefer
    }
    
    // Add other methods as needed
}
