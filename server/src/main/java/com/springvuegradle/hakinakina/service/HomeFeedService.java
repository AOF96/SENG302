package com.springvuegradle.hakinakina.service;

import com.springvuegradle.hakinakina.entity.ActivityChange;
import com.springvuegradle.hakinakina.entity.HomeFeedEntry;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.HomeFeedRepository;
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

    public HomeFeedRepository homeFeedRepository;

    /**
     * Constructs a Home Feed Service, passing in the repositories so that they can be accessed.
     */
    public HomeFeedService(HomeFeedRepository homeFeedRepository) {
        this.homeFeedRepository = homeFeedRepository;
    }

    /**
     * Returns a list of entries for the users home feed
     * @param profileId id of user to get feed for
     * @return home feed results
     */
    public Page<HomeFeedEntry> getHomeFeed(Long profileId, int page, int size) {

        Page<HomeFeedEntry> result;
        result = homeFeedRepository.getUserHomeFeedById(PageRequest.of(page, size), profileId);

        return result;
    }
}