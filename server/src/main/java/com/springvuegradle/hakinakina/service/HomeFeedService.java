package com.springvuegradle.hakinakina.service;

import com.springvuegradle.hakinakina.entity.ActivityChange;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.repository.ActivityChangeRepository;
import com.springvuegradle.hakinakina.util.ErrorHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service class containing logic related to Home Feed
 */
@Service
public class HomeFeedService {

    public ActivityChangeRepository activityChangeRepository;

    /**
     * Constructs a Home Feed Service, passing in the repositories so that they can be accessed.
     */
    public HomeFeedService(ActivityChangeRepository activityChangeRepository) {
        this.activityChangeRepository = activityChangeRepository;
    }

    /**
     * Returns a list of entries for the users home feed
     * @param profileId id of user to get feed for
     * @return home feed results
     */
    public Page<ActivityChange> getHomeFeed(Long profileId, int page, int size) {

        Page<ActivityChange> result;
        result = activityChangeRepository.getUserHomeFeedById(PageRequest.of(page, size), profileId);

        return result;
    }
}