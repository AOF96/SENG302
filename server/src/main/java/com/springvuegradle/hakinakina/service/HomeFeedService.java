package com.springvuegradle.hakinakina.service;

import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service class containing logic related to Home Feed
 */
@Service
public class HomeFeedService {

    /**
     * Constructs a Home Feed Service, passing in the repositories so that they can be accessed.
     */
    public HomeFeedService() {
    }

    /**
     * Returns a list of entries for the users home feed
     * @param profileId id of user to get feed for
     * @return home feed results
     */
    public ResponseEntity<String> getHomeFeed(Long profileId) throws Exception{
        //TODO home feed from repository logic
        return null;
    }
}
