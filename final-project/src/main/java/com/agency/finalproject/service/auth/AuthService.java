package com.agency.finalproject.service.auth;

import com.agency.finalproject.entity.Manager;
import com.agency.finalproject.entity.Master;
import com.agency.finalproject.entity.Role;
import com.agency.finalproject.entity.User;
import com.agency.finalproject.exception.WrongPasswordOnLoginException;
import com.agency.finalproject.repository.manager.ManagerRepository;
import com.agency.finalproject.repository.master.MasterRepository;
import com.agency.finalproject.repository.user.UserRepository;
import com.agency.finalproject.service.BaseService;
import com.agency.finalproject.service.session.CurrentSession;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO encrypt password
 */
@Service
public class AuthService implements BaseService {
    private static final Logger logger = LogManager.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final MasterRepository masterRepository;
    private final ManagerRepository managerRepository;

    @Autowired
    public AuthService(UserRepository userRepository, MasterRepository masterRepository, ManagerRepository managerRepository) throws ClassNotFoundException {
        this.userRepository = userRepository;
        this.masterRepository = masterRepository;
        this.managerRepository = managerRepository;
    }

    public void register(String email, String password, Role role) {
        switch (role) {
            case MASTER:
                if (this.masterRepository.existsByEmail(email)) {
                    logger.error(String.format("Master with email=[%s] already exists.", email));
                    return;
                }
                Master master = Master.builder()
                        .email(email)
                        .password(password)
                        .build();
                this.masterRepository.save(master);
                break;
            case MANAGER:
                if (this.managerRepository.existsByEmail(email)) {
                    logger.error(String.format("Manager with email=[%s] already exists.", email));
                    return;
                }
                Manager manager = Manager.builder()
                        .email(email)
                        .password(password)
                        .build();
                this.managerRepository.save(manager);
                break;
            case USER:
                if (this.userRepository.existsByEmail(email)) {
                    logger.error(String.format("User with email=[%s] already exists.", email));
                    return;
                }
                User user = User.builder()
                        .email(email)
                        .password(password)
                        .build();
                this.userRepository.save(user);
                break;
            default:
                logger.warn("Registration was not successful, because specified role is not correct.");
                return;
        }
        logger.info("Registration was successful.");
    }

    public void login(String email, String password, Role role) throws EntityNotFoundException, WrongPasswordOnLoginException {

    }

    public void logout() {
        CurrentSession.clear();
        logger.info("Logout was successfully performed.");
    }

}