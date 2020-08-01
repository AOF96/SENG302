package com.springvuegradle.hakinakina.controller;

import com.springvuegradle.hakinakina.entity.ActivityChange;
import com.springvuegradle.hakinakina.repository.SessionRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.service.HomeFeedService;
import com.springvuegradle.hakinakina.util.ErrorHandler;
import com.springvuegradle.hakinakina.util.ResponseHandler;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Rest controller class for controlling requests about a user's Home Feed
 */
@RestController
public class HomeFeedController {

    public UserRepository userRepository;
    public HomeFeedService homeFeedService;
    public SessionRepository sessionRepository;
    private final ResponseHandler responseHandler = new ResponseHandler();

    /**
     * Constructs an Activity Controller, passing in the repositories and service so that they can be accessed.
     *
     * @param userRepository     The repository containing Users
     * @param homeFeedService    The service for the Home Feed
     * @param sessionRepository  The repository containing sessions
     */
    public HomeFeedController(UserRepository userRepository,
                              HomeFeedService homeFeedService,
                              SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.homeFeedService = homeFeedService;
        this.sessionRepository = sessionRepository;
    }

    /**
     * Returns a list of entries for the users home feed
     * @param profileId id of user to get feed for
     * @param sessionToken session of requesting user
     * @return home feed results
     */
    @GetMapping("/profiles/{profileId}/feed")
    public ResponseEntity getHomeFeed(@PathVariable Long profileId,
                                      @CookieValue(value = "s_id") String sessionToken,
                                      @RequestParam("page") int page,
                                      @RequestParam("size") int size) {
        ResponseEntity result;
        try{
            if (sessionToken == null) {
                result = responseHandler.formatErrorResponseString(401, "Invalid Session");
            } else if (profileId == null || userRepository.getUserById(profileId).isEmpty()) {
                result = responseHandler.formatErrorResponseString(404, "User not found");
            } else if (!profileId.equals(sessionRepository.findUserIdByToken(sessionToken).getUser().getUserId())) {
                result = responseHandler.formatErrorResponseString(403, "Invalid user");
            } else {
                Page<ActivityChange> activityChanges = homeFeedService.getHomeFeed(profileId, page, size);
                List<ActivityChange> test = activityChanges.toList();
                StringBuilder outcome = new StringBuilder("{\"");
                for (ActivityChange activityChange : test) {
                    outcome.append(activityChange.getDescription());
                    System.out.println(activityChange.getDescription());
                }
                outcome.append("\"}");
                System.out.println(outcome);
                result = new ResponseEntity(outcome, HttpStatus.valueOf(200));

            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Cannot get feed");
            result = responseHandler.formatErrorResponseString(500, "An error occurred");
        }
        return result;
    }
}
