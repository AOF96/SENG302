package com.springvuegradle.hakinakina.controller;

import com.springvuegradle.hakinakina.dto.FeedPostDto;
import com.springvuegradle.hakinakina.entity.ActivityChange;
import com.springvuegradle.hakinakina.entity.HomeFeedEntry;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.SessionRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.service.HomeFeedService;
import com.springvuegradle.hakinakina.util.ErrorHandler;
import com.springvuegradle.hakinakina.util.ResponseHandler;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Rest controller class for controlling requests about a user's Home Feed
 */
@RestController
public class HomeFeedController {

    public UserRepository userRepository;
    public HomeFeedService homeFeedService;
    public ActivityRepository activityRepository;
    public SessionRepository sessionRepository;
    private final ResponseHandler responseHandler = new ResponseHandler();

    /**
     * Constructs an Activity Controller, passing in the repositories and service so that they can be accessed.
     *
     * @param userRepository     The repository containing Users
     * @param activityRepository The repository containing Activities
     * @param homeFeedService    The service for the Home Feed
     * @param sessionRepository  The repository containing sessions
     */
    public HomeFeedController(UserRepository userRepository,
                              SessionRepository sessionRepository,
                              ActivityRepository activityRepository,
                              HomeFeedService homeFeedService) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
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
                Page<HomeFeedEntry> feedEntries = homeFeedService.getHomeFeed(profileId, page, size);
                List<HomeFeedEntry> changesList = feedEntries.toList();
                List<FeedPostDto> posts = new ArrayList<>();
                for (HomeFeedEntry feedEntry : changesList) {
                    FeedPostDto newPost = new FeedPostDto();
                    newPost.activityId = feedEntry.getActivity().getId();
                    newPost.activityName = feedEntry.getActivity().getName();
                    newPost.authorName = feedEntry.getUser().getFirstName()+" "+feedEntry.getUser().getLastName();
                    newPost.dateTime = feedEntry.getDatetime();
                    newPost.postType = feedEntry.getType();
                    newPost.textContext = feedEntry.getContent();
                    posts.add(newPost);
                }

                result = new ResponseEntity(posts, HttpStatus.valueOf(200));

            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Cannot get feed");
            result = responseHandler.formatErrorResponseString(500, "An error occurred");
        }
        return result;
    }
}
