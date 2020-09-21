package com.springvuegradle.hakinakina.controller;

import com.springvuegradle.hakinakina.dto.SearchActivityDto;
import com.springvuegradle.hakinakina.dto.SearchUserDto;
import com.springvuegradle.hakinakina.entity.ActivityType;
import com.springvuegradle.hakinakina.repository.ActivityTypeRepository;
import com.springvuegradle.hakinakina.repository.EmailRepository;
import com.springvuegradle.hakinakina.repository.SessionRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.service.SearchService;
import com.springvuegradle.hakinakina.util.ErrorHandler;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Rest controller class for controlling requests related to searching
 */
@RestController
public class SearchController {
    public UserRepository userRepository;
    public EmailRepository emailRepository;
    public SessionRepository sessionRepository;
    public ActivityTypeRepository activityTypeRepository;
    public SearchService searchService;

    /**
     * Constructs a Search Controller, passing in the repositories and service so that they can be accessed.
     *
     * @param userRepository         The repository containing Users
     * @param emailRepository        The repository containing Emails
     * @param sessionRepository      The repository containing Sessions
     * @param activityTypeRepository The repository containing Activities
     * @param searchService          The service for Searching
     */
    public SearchController(UserRepository userRepository,
                            EmailRepository emailRepository,
                            SessionRepository sessionRepository,
                            ActivityTypeRepository activityTypeRepository,
                            SearchService searchService) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.sessionRepository = sessionRepository;
        this.activityTypeRepository = activityTypeRepository;
        this.searchService = searchService;
    }

    /**
     * Takes a string of activity types from the URL and matches each to an Activity Type object, which is added to a
     * set and returned.
     * @param activity The string of activities from the url
     * @return A set of ActivityType objects matching those in the URL
     */
    public Set<ActivityType> getActivityTypesSet(String activity) {
        Set<ActivityType> activityTypes = new HashSet<>();
        if(activity != null) {
            String[] arrOfActivities = activity.split(" ");
            for (String activityType : arrOfActivities) {
                ActivityType retrievedType = activityTypeRepository.findActivityTypeByName(activityType);
                if (retrievedType != null) {
                    activityTypes.add(retrievedType);
                }
            }
        }
        return activityTypes;
    }

    /**
     * Retrieves activities that match the activity name
     * @param activitySearchTerm The activity name input in the search
     * @param page the page number the user wants
     * @param size the amount of activities returned that match the search term
     * @param sessionToken the session of the user
     * @return Response entity that returns the activities that match the search term if there are any
     */
    @GetMapping("/activities")
    public ResponseEntity findActivityPaginated(
            @RequestParam(required = false) String activitySearchTerm,
            @RequestParam(required = false) String activitySearchTerms,
            @RequestParam("method") String method,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @CookieValue(value = "s_id") String sessionToken) {
        try {
            if (sessionRepository.findUserIdByToken(sessionToken) == null) {
                return new ResponseEntity("Session invalid", HttpStatus.UNAUTHORIZED);
            }
            if (method.equals("single")) {
                Page<SearchActivityDto> results = searchService.findActivityPaginated(activitySearchTerm, page, size);
                return new ResponseEntity(results, HttpStatus.OK);
            } else {
                System.out.println(activitySearchTerms);
                String[] str = activitySearchTerms.split(" ");
                List<String> activitySearchTermsList = new ArrayList<String>();
                activitySearchTermsList = Arrays.asList(str);
                Set<String> activitySearchTermsSet = new HashSet<String>(activitySearchTermsList);

                Page<SearchActivityDto> results = searchService.findActivityPaginatedByQuery(activitySearchTermsSet,
                        method, page, size);
                return new ResponseEntity(results, HttpStatus.OK);
            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "could not search for activity");
            return new ResponseEntity("An error occurred", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Handle request for retrieving users with email or full name or surname
     *
     * @param email    searching for a user with the given email
     * @param fullname searching for a user with some name that matches a users full name (first, middle, last)
     * @param lastname searching for a user with the given nickname
     * @param activity searching for users with given activity types
     * @param page     current page number that the user is viewing
     * @param size     how many results we want to return
     * @return response entity containing a list of profiles
     */
    @GetMapping("/profiles")
    public ResponseEntity findPaginated(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String fullname,
            @RequestParam(required = false) String lastname,
            @RequestParam(required = false) String activity,
            @RequestParam("method") String method,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        if (!method.equals("or") && !method.equals("and")) {
            return new ResponseEntity("Method must either be 'or' or 'and'", HttpStatus.valueOf(400));
        }

        if(page < 0 || size < 1) {
            return new ResponseEntity("Invalid page or size value", HttpStatus.valueOf(400));
        }
        Set<ActivityType> activityTypes = getActivityTypesSet(activity);
        Page<SearchUserDto> resultPage;
        if(activityTypes.size() == 0){
            activityTypes = null;
        }
        if (email != null || fullname != null || lastname != null || activityTypes != null) {
            resultPage = searchService.findPaginatedByQuery(page, size, email, fullname, lastname, activityTypes, method);
        } else {
            resultPage = searchService.findPaginated(page, size);
        }

        return new ResponseEntity(resultPage, HttpStatus.valueOf(200));
    }
}
