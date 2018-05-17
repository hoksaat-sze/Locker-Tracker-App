package com.locker.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.locker.exception.LockerException;
import com.locker.model.Locker;
import com.locker.model.User;
import com.locker.repository.LockerRepository;

@Service
public class LockerService {

    private static final Logger log = LoggerFactory.getLogger(LockerService.class);

    private LockerRepository lockerRepo;

    private UserService userService;

    @Autowired
    public void setLockerRepo(LockerRepository lockerRepo) {
        this.lockerRepo = lockerRepo;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public List<Locker> getLockers() {
        return lockerRepo.findAll();
    }

    @Transactional
    public void addOwner(String email, String lockerId) throws LockerException {
        User loggedInUser = userService.getLoggedInUserByName(email);
        Locker newLocker = lockerRepo.findByIdAndOwnerId(lockerId, null);

        if (newLocker == null) {
            log.error("User tried to add locker which not his/her, "
                            + "already owned or not exists. lockerId=[{}]", lockerId);
            throw new LockerException("Locker owner is not null or the locker not exists. Cannot add to user!");
        }

        newLocker.setOwnerId(loggedInUser.getId());
        lockerRepo.save(newLocker);

        log.info("Change User's locker successfully done.");
    }

    @Transactional
    public void removeLocker(String lockerId, String email) throws LockerException {
        User loggedInUser = userService.getLoggedInUserByName(email);
        Locker newLocker = lockerRepo.findByIdAndOwnerId(lockerId, loggedInUser);

        if (newLocker == null) {
            log.error("User tried to remove locker which not his/her or not exists. lockerId=[{}]", lockerId);
            throw new LockerException("User has no such locker to remove!");
        }

        newLocker.setOwnerId(null);

        log.info("Remove logged in User's locker is succesfully done.");
    }

}
