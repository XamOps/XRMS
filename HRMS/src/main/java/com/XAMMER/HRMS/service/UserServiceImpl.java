package com.XAMMER.HRMS.service;

import com.XAMMER.HRMS.dto.PersonalDetailsUpdateDTO;
import com.XAMMER.HRMS.dto.UserDTO;
import com.XAMMER.HRMS.model.Roles;
import com.XAMMER.HRMS.model.User;
import com.XAMMER.HRMS.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final DateTimeFormatter inputDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE; // Parses "yyyy-MM-dd"
    @Autowired
    public UserServiceImpl(UserRepository userRepository,EmailService emailService , PasswordEncoder passwordencoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
         this.passwordEncoder = passwordencoder;

    }
    
   
    // @Override
    // public Optional<User> findById(Long id) {
    //     return userRepository.findById(id);
    // }

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

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public List<String> searchUsernames(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query).stream()
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

    @Override
    public Optional getUserById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserById'");
    }

    @Override
    public List<User> findAllEmployees() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllEmployees'");
    }
    // @Override
    // @Transactional
    // public User addUser(User user) {
    //     // **CRITICAL: Encode the password before saving**
    //     // if (user.getPassword() == null || user.getPassword().isEmpty()) {
    //     //     throw new IllegalArgumentException("Password cannot be empty or null for new user.");
    //     // }
    //     // user.setPassword(passwordEncoder.encode(user.getPassword())); // <--- THIS IS THE FIX

    //     // // Optional: Set default role if not already provided (good practice)
    //     // if (user.getRoles() == null) {
    //     //     user.setRoles(Roles.User); // Or your default role
    //     // }

    //     // Optional: Set username from email if applicable (good practice)
    //     if (user.getEmail() != null && !user.getEmail().isEmpty() && (user.getUsername() == null || user.getUsername().isEmpty())) {
    //         user.setUsername(user.getEmail().split("@")[0]);
    //     }

    //     // Optional: Set joining date if not provided
    //     if (user.getJoiningDate() == null) {
    //         user.setJoiningDate(LocalDate.now());
    //     }

    //     return userRepository.save(user); // Save the user
    // }

 @Override
@Transactional
public User addUser(User user) {
    // Generate username if not provided
    if (user.getUsername() == null || user.getUsername().isEmpty()) {
        user.setUsername(user.getEmail().split("@")[0]);
    }

    // Validate and encode password
    if (user.getPassword() == null || user.getPassword().isEmpty()) {
        throw new IllegalArgumentException("Password cannot be empty");
    }
    String rawPassword = user.getPassword(); // Store before encoding

    // **** IMPORTANT: UNCOMMENT THIS LINE TO ENCODE PASSWORD ****
    user.setPassword(passwordEncoder.encode(rawPassword));

    // Set default role if not provided (re-enabled as it's good practice)
    // if (user.getRoles() == null) {
    //     user.setRoles(Roles.EMPLOYEE); // Assuming Roles.EMPLOYEE is a valid enum value
    // }

    // Set joining date if not provided
    if (user.getJoiningDate() == null) {
        user.setJoiningDate(LocalDate.now());
    }

    User savedUser = userRepository.save(user);

    // Send welcome email with the raw password and a login URL
    // **** IMPORTANT: ADD THE loginUrl ARGUMENT ****
    String loginUrl = "https://xrms.xammer.in/login"; // Replace with your actual login URL or get it from properties
    emailService.sendWelcomeEmail(
        savedUser.getEmail(),
        savedUser.getFirstName(),
        savedUser.getUsername(),
        rawPassword, // Send the raw password for the email
        loginUrl     // Provide the login URL
    );

    return savedUser;
}

    @Override
    public List<User> searchAndFilterUsers(String query, String department) {
        // Example implementation - you might need more complex queries in your UserRepository
        if (query != null && !query.isEmpty() && department != null && !department.isEmpty()) {
            return userRepository.findByFirstNameContainingIgnoreCaseAndDepartmentContainingIgnoreCase(query, department);
        } else if (query != null && !query.isEmpty()) {
            return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, query);
        } else if (department != null && !department.isEmpty()) {
            return userRepository.findByDepartmentContainingIgnoreCase(department);
        } else {
            return userRepository.findAll();
        }
    }

       @Override
    @Transactional // Add @Transactional for methods that modify data
    public User updateUser(Long id, User userDetails) {
        Optional<User> existingUserOptional = userRepository.findById(id);

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();

            // Update all the fields that can be edited from the frontend
            existingUser.setFirstName(userDetails.getFirstName());
            existingUser.setLastName(userDetails.getLastName());
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setDepartment(userDetails.getDepartment());
            existingUser.setJobTitle(userDetails.getJobTitle());
            existingUser.setPhone(userDetails.getPhone());
            existingUser.setBirthDate(userDetails.getBirthDate()); 
            // Assuming you have this field and want to update
            existingUser.setJoiningDate(userDetails.getJoiningDate()); // Uncomment if updatable
            // existingUser.setPassword(userDetails.getPassword()); 
            existingUser.setRoles(userDetails.getRoles()); 
            


            // Be careful with password, roles, etc. - usually not updated via employee edit
            // If username is derived from email, update it
            existingUser.setUsername(userDetails.getEmail().split("@")[0]);

            // Save the updated entity
            return userRepository.save(existingUser);
        } else {
            // Throw an exception that your controller can catch and convert to a 404
            throw new RuntimeException("User not found with ID: " + id);
        }
    }
   @Override
    @Transactional // Essential for this method to ensure atomicity
    public void deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            // If the user doesn't exist, throw an exception that the controller
            // can catch to return a 404 (Not Found).
            // Example: throw new EmptyResultDataAccessException("User not found with ID: " + id, 1);
            // Or a custom exception
            System.out.println("User with ID " + id + " not found for deletion.");
            return; // Or throw a specific exception if you want your controller to handle it
        }

        User userToDelete = userOptional.get();

        // **IMPORTANT: Handle subordinates first**
        // Disassociate any subordinates who report to this manager
        List<User> subordinates = userRepository.findByManager(userToDelete);
        for (User subordinate : subordinates) {
            subordinate.setManager(null); // Set their manager to null
            userRepository.save(subordinate); // Save the updated subordinate
        }
        // Note: With CascadeType.ALL on attendances and leaveRequests in User entity,
        // you no longer need to explicitly delete them here.
        // JPA will handle it when userToDelete is deleted.

        userRepository.delete(userToDelete); // Delete the user (JPA will cascade to attendance/leaveRequests)
        System.out.println("User with ID " + id + " and associated records deleted successfully from service.");
    }
        @Override
    // @Transactional(readOnly = true) // Ensure the session is open for mapping
    public List<UserDTO> findAllUsersDTO() {
        // Fetch all User entities from the database
        // Hibernate's default findAll() will load the main User entity and its direct fields.
        // The manager relationship (if FetchType.LAZY) will be a proxy.
        List<User> users = userRepository.findAll();

        // Map each User entity to a UserDTO within the active transaction
        return users.stream()
                .map(UserDTO::new) // Call the DTO constructor
                .collect(Collectors.toList());
    }

        public List<String> findUsernamesByQuery(String query) {
        // Implement logic to find users by username (or parts of name/email)
        // Example:
        return userRepository.findByUsernameContainingIgnoreCase(query)
                             .stream()
                             .map(User::getUsername) // Assuming User has getUsername()
                             .collect(Collectors.toList());
        // You might need to add findByUsernameContainingIgnoreCase to your UserRepository
    }
   @Override
    public Optional<User> findById(Long id) {
        if (id == null) {
            // You can either return Optional.empty() or throw an IllegalArgumentException
            // JpaRepository's findById itself usually throws IllegalArgumentException if id is null.
            // So, this explicit check might be redundant depending on desired behavior,
            // but it's clear.
            // For consistency with how JpaRepository.findById(null) behaves,
            // it's often okay to just pass it through:
            // return userRepository.findById(id);
            // Or, if you want to return empty for null IDs without an exception from repo:
            return Optional.empty();
        }
        return userRepository.findById(id);
    }



 @Override
@Transactional // Ensures the operation is atomic
public User updatePersonalDetails(Long id, PersonalDetailsUpdateDTO PersonalDetailsUpdateDTO) { // Parameter name is PersonalDetailsUpdateDTO
    // Step 1: Find the user.
    User userToUpdate = findById(id) // Uses your updated findById
            .orElseThrow(() -> new RuntimeException("User not found with employee ID: " + id + " for update.")); // Message says "employee ID" but 'id' is primary key

    // Step 2: Update fields from the DTO
    // Update Date of Birth
    if (PersonalDetailsUpdateDTO.getDateOfBirth() != null) {
        if (PersonalDetailsUpdateDTO.getDateOfBirth().isEmpty()) {
            userToUpdate.setBirthDate(null);
        } else {
            try {
                // Assumes inputDateFormatter is defined in this class or accessible
                userToUpdate.setBirthDate(LocalDate.parse(PersonalDetailsUpdateDTO.getDateOfBirth(), inputDateFormatter));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format for birth date. Expected yyyy-MM-dd.", e);
            }
        }
    }

    // Update Gender
    if (PersonalDetailsUpdateDTO.getGender() != null) {
        userToUpdate.setGender(PersonalDetailsUpdateDTO.getGender().isEmpty() ? null : PersonalDetailsUpdateDTO.getGender());
    }

    // Update Email
    if (PersonalDetailsUpdateDTO.getEmail() != null) {
        userToUpdate.setEmail(PersonalDetailsUpdateDTO.getEmail());
    }

    // Update Phone
    if (PersonalDetailsUpdateDTO.getPhone() != null) {
        userToUpdate.setPhone(PersonalDetailsUpdateDTO.getPhone().isEmpty() ? null : PersonalDetailsUpdateDTO.getPhone());
    }

    // Update Address
    if (PersonalDetailsUpdateDTO.getAddress() != null) {
        userToUpdate.setAddress(PersonalDetailsUpdateDTO.getAddress().isEmpty() ? null : PersonalDetailsUpdateDTO.getAddress());
    }

    // Step 3: Save the updated user
    return userRepository.save(userToUpdate);
}

        // @Override
        // public void sendWelcomeEmail(String to, String firstName, String username, String password) {
        //     // TODO Auto-generated method stub
        //     throw new UnsupportedOperationException("Unimplemented method 'sendWelcomeEmail'");
        // }

        // @Override
        // public UserDTO addUser(UserDTO userDTO) {
        //     // TODO Auto-generated method stub
        //     throw new UnsupportedOperationException("Unimplemented method 'addUser'");
        // }

        // @Override
        // public UserDTO updateUser(Long id, UserDTO userDTO) {
        //     // TODO Auto-generated method stub
        //     throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
        // }
}