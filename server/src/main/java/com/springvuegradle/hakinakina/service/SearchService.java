package com.springvuegradle.hakinakina.service;

import com.springvuegradle.hakinakina.dto.SearchActivityDto;
import com.springvuegradle.hakinakina.dto.SearchActivityLocationDto;
import com.springvuegradle.hakinakina.dto.SearchUserDto;
import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.entity.ActivityType;
import com.springvuegradle.hakinakina.entity.Location;
import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.LocationRepository;
import com.springvuegradle.hakinakina.repository.SearchRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.specification.ActivitySpecification;
import com.springvuegradle.hakinakina.specification.UserSpecification;
import com.springvuegradle.hakinakina.util.ResponseHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.*;


/**
 * Class that handles all the search functionality logic.
 */
@Service
public class SearchService {

    private UserRepository userRepository;
    private SearchRepository searchRepository;
    private ActivityRepository activityRepository;
    private LocationRepository locationRepository;
    private ResponseHandler responseHandler = new ResponseHandler();

    public SearchService(UserRepository userRepository,
                         SearchRepository searchRepository,
                         ActivityRepository activityRepository,
                         LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.searchRepository = searchRepository;
        this.activityRepository = activityRepository;
        this.locationRepository = locationRepository;
    }

    /**
     * Returns a list of activities that match the searched term. The returned results are paginated.
     *
     * @param activitySearchTerm the search term of the activity that the user is trying to find
     * @param page the page number the user wants to be at
     * @param size the number of activities that are returned per page
     * @return Page object with a list of SearchActivityDtos that will display generic information about the activity
     */
    @Transactional
    public Page<SearchActivityDto> findActivityPaginated(String activitySearchTerm, int page, int size) {
        Page<Activity> activityPage = activityRepository.findAll(generateActivitySpecification(activitySearchTerm), PageRequest.of(page, size));
        List<SearchActivityDto> searchActivityDtoList = new ArrayList<SearchActivityDto>();
        for (Activity activity: activityPage) {
            SearchActivityDto searchActivityDto = new SearchActivityDto();
            searchActivityDto.setId(activity.getId());
            searchActivityDto.setName(activity.getName());
            searchActivityDto.setContinuous(activity.isContinuous());
            searchActivityDto.setStartTime(activity.getStartTime());
            searchActivityDto.setEndTime(activity.getEndTime());

            if (activity.getLocation() != null) {
                if (locationRepository.findById(activity.getLocation().getId()).isPresent()) {
                    Location activityLocation = locationRepository.getOne(activity.getLocation().getId());
                    searchActivityDto.setSearchActivityLocationDto(setLocationForSearchActivityDto(activityLocation));
                }
            }

            searchActivityDtoList.add(searchActivityDto);
        }
        return new PageImpl<>(searchActivityDtoList);
    }

    @Transactional
    public Page<SearchActivityDto> findActivityPaginatedByQuery(Set<String> searchTerms, String method, int page, int size) {
        List<SearchActivityDto> searchActivityDtoList = new ArrayList<SearchActivityDto>();
        List<Activity> activityList = new ArrayList<>();
        if (method.equals("or")) {
            activityList = activityRepository.findActivityNamesOr(searchTerms);
        } else if (method.equals("and")){
            activityList = activityRepository.findActivityNamesAnd(searchTerms);
        }
            int start = page * 10;
            int end = start + size;
            if (activityList.size() < end) {
                end = activityList.size();
            }
            for (int i = start; i<end; i++) {
                SearchActivityDto searchActivityDto = new SearchActivityDto();
                searchActivityDto.setId(activityList.get(i).getId());
                searchActivityDto.setName(activityList.get(i).getName());
                searchActivityDto.setContinuous(activityList.get(i).isContinuous());
                searchActivityDto.setStartTime(activityList.get(i).getStartTime());
                searchActivityDto.setEndTime(activityList.get(i).getEndTime());
                if (activityList.get(i).getLocation() != null) {
                    if (locationRepository.findById(activityList.get(i).getLocation().getId()).isPresent()) {
                        Location activityLocation = locationRepository.getOne(activityList.get(i).getLocation().getId());
                        searchActivityDto.setSearchActivityLocationDto(setLocationForSearchActivityDto(activityLocation));
                    }
                }
                searchActivityDtoList.add(searchActivityDto);
            }
        return new PageImpl<>(searchActivityDtoList);
    }

    /**
     * Determines if there is a location set for the activity. If there is one, it sets a generic address.
     *
     * @param activityLocation The location of the activity that we are setting the DTO for
     * @return SearchActivityLocationDto that will return a generic address for the activity search results
     */
    public SearchActivityLocationDto setLocationForSearchActivityDto(Location activityLocation) {
        SearchActivityLocationDto searchActivityLocationDto = new SearchActivityLocationDto();
        searchActivityLocationDto.setStreetAddress(activityLocation.getStreetAddress());
        searchActivityLocationDto.setCity(activityLocation.getCity());
        searchActivityLocationDto.setCountry(activityLocation.getCountry());
        return searchActivityLocationDto;
    }

    /**
     * Deals with pagination with no conditions like email, surname, full name etc
     *
     * @param page number of a page you want to be at
     * @param size how many results you want on a page
     * @return Page object with list SearchUserResponse object with user's email, full name, nickname
     */
    public Page<SearchUserDto> findPaginated(int page, int size) {
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));
        return responseHandler.userPageToSearchResponsePage(userPage);
    }

    /**
     * Deals with pagination where you can search users with email, full name and last name
     *
     * @param page     number of a page you want to be at
     * @param size     how many results you want on a page
     * @param email    email of the user you want to search
     * @param fullname full name of the user you want to search
     * @param lastname last name of the user you want to search
     * @param activityTypes activityTypes of the user you want to search
     * @param method
     * @return Page object with list SearchUserResponse object with user's email, full name, nickname
     */
    public Page<SearchUserDto> findPaginatedByQuery(int page, int size, String email, String fullname, String lastname, Set<ActivityType> activityTypes, String method) {
        Page<User> userPage;
        if (activityTypes != null) {
            if (method.equals("or")) {
                userPage = userRepository.findAllByActivityTypesOR(PageRequest.of(page, size), email, fullname, lastname, activityTypes);
            } else {
                userPage = userRepository.getUsersWithActivityTypeAnd(PageRequest.of(page, size), email, fullname, lastname, activityTypes);
            }
        } else {
            userPage = userRepository.findAll(generateSpecification(lastname, fullname, email), PageRequest.of(page, size));
        }
        return responseHandler.userPageToSearchResponsePage(userPage);
    }

    /**
     * Finds the intersection of a List of Sets of Users. Much of this code was adapted from
     * https://stackoverflow.com/questions/37749559/conversion-of-list-to-page-in-spring
     * @param listOfUserSets A List of Sets of User objects
     * @return The intersection of these Sets as a List
     */
    public List<User> getIntersectionOfListOfSetsOfUsers(List<Set<User>> listOfUserSets) {
        List<User> result = new ArrayList<>();
        if (!listOfUserSets.isEmpty()) {
            Set<User> userCross = listOfUserSets.get(0);
            for (int i = 1; i < listOfUserSets.size(); i++) {
                userCross.retainAll(listOfUserSets.get(i));
            }
            for (User user : userCross) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * Gives a normal user admin rights if the requesting user is authenticated and is an admin.
     * @param lastName last name of the user you are searching
     * @param fullName full name of the user you are searching
     * @param email email of the user you are searching
     * @return specification object with User search request (WHERE part of a query)
     */
    private Specification<User> generateSpecification(String lastName, String fullName, String email) {
        return Specification.where(UserSpecification.searchByLastName(lastName))
                .and(
                        UserSpecification.searchByFullName(fullName))
                .and(
                        UserSpecification.searchByEmail(email))
                .and(
                        UserSpecification.searchIsNotAdmin()
                );
    }

    /**
     * Specification for searching an activity by name
     * @param activityName search term of an activity name
     * @return Specification object with Activity search request (WHERE part of the query)
     */
    private Specification<Activity> generateActivitySpecification(String activityName) {
        return Specification.where(ActivitySpecification.searchByActivityName(activityName));
    }
}
