package com.springvuegradle.Hakinakina.controller;

import com.springvuegradle.Hakinakina.entity.Activity;
import com.springvuegradle.Hakinakina.entity.PassportCountryRepository;
import com.springvuegradle.Hakinakina.entity.SessionRepository;
import com.springvuegradle.Hakinakina.entity.UserRepository;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {

    public UserRepository userRepository;
    public PassportCountryRepository countryRepository;
    public SessionRepository sessionRepository;
    private ResponseHandler responseHandler = new ResponseHandler();

    public ActivityService(UserRepository userRepository, PassportCountryRepository countryRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.sessionRepository = sessionRepository;
    }

    /**
     * Adds an activity for the user*
     */
    public ResponseEntity addActivity() {
        return null;
    }

}
