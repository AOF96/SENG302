package com.springvuegradle.Hakinakina.controller;

import com.springvuegradle.Hakinakina.entity.Email;
import com.springvuegradle.Hakinakina.entity.EmailRepository;
import com.springvuegradle.Hakinakina.entity.User;
import com.springvuegradle.Hakinakina.entity.UserRepository;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;


/**
 * Rest controller class for controlling requests about Users
 */
@RestController
public class EmailController {

    public EmailRepository emailRepository;
    public UserRepository userRepository;
    private ResponseHandler responseHandler = new ResponseHandler();


    /**
     * Contructs a emailsController and parses in the Repository
     *
     * @param emailRepository
     */
    public EmailController(EmailRepository emailRepository, UserRepository userRepository){
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
    }
    
}
