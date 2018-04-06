package com.locker.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.locker.exception.LockerException;
import com.locker.form.UserRegisterForm;
import com.locker.model.User;
import com.locker.repository.UserRepository;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepo;

    private EmailService emailService;

    @Autowired
    public void setUserRepo(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public List<User> getUsers() {
        return userRepo.findAll();
    }

    public User getLoggedInUserByName(String email) {
        return userRepo.findByEmail(email.toLowerCase());
    }

    @Transactional
    public void signUpUser(UserRegisterForm userForm) throws LockerException, UnsupportedEncodingException {
        String userFormEmail = userForm.getEmail().toLowerCase();
        User user = userRepo.findByEmail(userFormEmail);

        if (user != null) {
            String errorMessage = "This user already exists!";
            log.error("LockerException thrown: " + errorMessage);
            throw new LockerException(errorMessage);
        }

        user = new User();
        user.setEmail(userFormEmail);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userForm.getPassword());
        String activationCode = UUID.randomUUID().toString();

        user.setPassword(hashedPassword);
        user.setEnabled(false);
        user.setActivation(activationCode);
        userRepo.save(user);

        emailService.sendActivationLink(userFormEmail, activationCode);

        log.info(
            String.format("UserService: user saved in the database successfully. [user.email = %s]",
                    userFormEmail));
    }

    @Transactional
    public void activateUser(String code) throws LockerException {
        User user = userRepo.findByActivation(code);

        if (user == null) {
            log.info("User not found with this activation code");
            throw new LockerException("LockerException: User not found with this activation code");
        }

        user.setEnabled(true);
        user.setActivation("");

        log.info("User activated its account");
    }

}
