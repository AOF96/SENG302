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
     * @return Page object with a list of SearchActivity Dtos that will display generic information about the activity
     */
    @Transactional
    public Page<SearchActivityDto> findActivityPaginated(String activitySearchTerm, int page, int size, User searchingUser) {
        Page<Activity> activityPage = activityRepository.findAll(generateActivitySpecification(activitySearchTerm, searchingUser), PageRequest.of(page, size));
        List<SearchActivityDto> searchActivityDtoList = new ArrayList<SearchActivityDto>();
        for (Activity activity: activityPage) {
            SearchActivityDto searchActivityDto = new SearchActivityDto();
            searchActivityDto.setId(activity.getId());
            searchActivityDto.setName(activity.getName());
            searchActivityDto.setVisibility(activity.getVisibility());
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

    /**
     * Uses dynamic query builders to get a list of activities with either all of the supplied searchterms
     * or at least one of the supplied searchterms depending on the value of the method param. After the
     * list of activities has been retreived it is turned into a Page<SearchActivityDto> and returned.
     * @param searchTerms A set of terms that the user wants to search for
     * @param method The method of multiple term search. And or Or, contains all terms or at least one term
     * @param page The current page to retrieve
     * @param size The amount of activities to display on this page
     * @return Returns a page of activities that match the criteria
     */
    @Transactional
    public Page<SearchActivityDto> findActivityPaginatedByQuery(Set<String> searchTerms, String method, int page, int size) {
        List<SearchActivityDto> searchActivityDtoList = new ArrayList<SearchActivityDto>();
        List<Activity> activityList = new ArrayList<>();
        if (method.equals("or")) {
            activityList = activityRepository.findActivityNamesOr(searchTerms);
        } else if (method.equals("and")){
            activityList = activityRepository.findActivityNamesAnd(searchTerms);
        }
            int start = (page - 1) * 10;
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
    public Page<SearchUserDto> findPaginatedByQuery(int page, int size, String email, String fullname, String lastname,
                                                    Set<ActivityType> activityTypes, Set<String> searchTerms,
                                                    String searchTypes, String searchTermsMethod, String method) {
        if (searchTermsMethod.equals("single")) {
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
        } else {
            Page<User> userPageFinal = null;
            userPageFinal = getUsersByActivityFilter(page, size, email, fullname, lastname, activityTypes, searchTerms,
                    searchTypes, searchTermsMethod, method);

            if (userPageFinal == null) {
                return responseHandler.userPageToSearchResponsePage(new PageImpl<>(new ArrayList<>()));
            } else {
                return responseHandler.userPageToSearchResponsePage(userPageFinal);
            }
        }
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
     * Specification for searching an activity by name which are either public, shared or user is the creator of the activity
     * @param activityName search term of an activity name
     * @param searchingUser user object of the user who is doing the search
     * @return Specification object with Activity search request (WHERE part of the query)
     */
    private Specification<Activity> generateActivitySpecification(String activityName, User searchingUser) {
        Specification<Activity> activitySpecification;
        activitySpecification = Specification.where(ActivitySpecification.searchByActivityName(activityName));

        // if you are not admin, your search is limited
        if (searchingUser.getPermissionLevel() == 0) {
            Specification<Activity> additionalSpecification = Specification.where(
                    ActivitySpecification.searchPublicActivity()
            ).or(
                    ActivitySpecification.searchIsActivityAuthor(searchingUser)
            ).or(
                    ActivitySpecification.searchIsActivityShared(searchingUser)
            );
            activitySpecification = activitySpecification.and(additionalSpecification);
        }

        return activitySpecification;
    }

    /**
     * Checks the list of users returned by the keyword filter and the activity type filter and returns users that are
     * in both
     * @param start where the list search should start. Will depend on current page
     * @param list1End the amount of entries to check from the first list
     * @param list2End the amount of entries to check from the second list
     * @param userList1 list of users that were found by the activity filter search
     * @param userList2 list of users that were found by the keyword filter search
     * @return a list of users that were in both of the lists
     */
    private List<User> getCommonItemsBetweenLists(List<User> userList1, List<User> userList2, int start,
                                                  int list1End, int list2End) {
        List<User> commonItemsList = new ArrayList<>();

        for (int i = start; i < list1End; i++) {
            for (int j = start; j < list2End; j++) {
                if (userList1.get(i).getUserId().equals(userList2.get(j).getUserId())) {
                    commonItemsList.add(userList1.get(i));
                    break;
                }
            }
        }

        return commonItemsList;
    }

    /**
     * Helper function that calls other helper functions in order to get the final page of users to return.
     * @param page the current page number
     * @param size the amount of results to return
     * @param email email of the current user being searched
     * @param fullname fullname of the current user being searched
     * @param lastname lastname of the current user being searched
     * @param activityTypes activity types that users are being checked whether they have or not
     * @param searchTerms the keywords that are being searched for
     * @param searchTypes the type of the user search
     * @return a page of users that were in both activity and keyword filter searches
     */
    private Page<User> getUsersByActivityFilter(int page, int size, String email, String fullname, String lastname,
                                                Set<ActivityType> activityTypes, Set<String> searchTerms,
                                                String searchTypes, String searchTermsMethod, String method) {

        List<User> usersFoundByKeywordFilterList = new ArrayList<>();
        Page<User> usersFoundByActivityFilterPage;
        List<User> usersFoundByActivityFilterList = new ArrayList<>();
        Page<User> userPageFinal = null;


        if (searchTermsMethod.equals("and")) {
            usersFoundByKeywordFilterList = userRepository.findUserNamesAnd(searchTerms, searchTypes);
        } else {
            usersFoundByKeywordFilterList = userRepository.findUserNamesOr(searchTerms, searchTypes);
        }

        if (activityTypes != null) {
            if (method.equals("or")) {
                usersFoundByActivityFilterPage = filterUsersActivityTypesOr(page, size, email, fullname, lastname,
                        activityTypes, searchTerms, searchTypes);
            } else {
                usersFoundByActivityFilterPage = filterUsersActivityTypesAnd(page, size, email, fullname, lastname,
                        activityTypes, searchTerms, searchTypes);
            }

            usersFoundByActivityFilterList = usersFoundByActivityFilterPage.getContent();

            userPageFinal = getCommonItemsBetweenLists(page, size, usersFoundByActivityFilterList,
                    usersFoundByKeywordFilterList);
        } else {
            if (usersFoundByKeywordFilterList.size() > page * 10) {
                usersFoundByKeywordFilterList = usersFoundByKeywordFilterList.subList(page * 10,
                        usersFoundByKeywordFilterList.size());
                userPageFinal = new PageImpl<>(usersFoundByKeywordFilterList);
            } else {
                userPageFinal = new PageImpl<>(new ArrayList<>());
            }
        }
        return userPageFinal;
    }

    /**
     * Checks to see if users that were returned by the keyword search have at least one of the activity types that are
     * being filtered by and returns them.
     * @param page the current page number
     * @param size the amount of results to return
     * @param email email of the current user being searched
     * @param fullname fullname of the current user being searched
     * @param lastname lastname of the current user being searched
     * @param activityTypes activity types that users are being checked whether they have or not
     * @param searchTerms the keywords that are being searched for
     * @param searchTypes the type of the user search
     * @return a page of all users with that have at least one of these associated activity types
     */
    private Page<User> filterUsersActivityTypesOr(int page, int size, String email, String fullname, String lastname,
                                                  Set<ActivityType> activityTypes, Set<String> searchTerms,
                                                  String searchTypes) {
        Page<User> usersFoundByActivityFilterPage;
        List<User> usersFoundByActivityFilterList = new ArrayList<>();
        ArrayList<String> searchTermsList = new ArrayList<>(searchTerms);

        if (searchTypes.equals("email")) {
            for (int i =0; i<searchTerms.size(); i++) {
                usersFoundByActivityFilterPage = userRepository.findAllByActivityTypesOR(
                        PageRequest.of(page, size), searchTermsList.get(i), fullname, lastname, activityTypes);
                usersFoundByActivityFilterList.addAll(usersFoundByActivityFilterPage.getContent());
            }
            usersFoundByActivityFilterPage = new PageImpl<>(usersFoundByActivityFilterList);
        } else if (searchTypes.equals("lastname")) {
            for (int i =0; i<searchTerms.size(); i++) {
                usersFoundByActivityFilterPage = userRepository.findAllByActivityTypesOR(
                        PageRequest.of(page, size), email, fullname, searchTermsList.get(i), activityTypes);
                usersFoundByActivityFilterList.addAll(usersFoundByActivityFilterPage.getContent());
            }
            usersFoundByActivityFilterPage = new PageImpl<>(usersFoundByActivityFilterList);
        } else {
            usersFoundByActivityFilterPage = userRepository.findAllByActivityTypesOR(
                    PageRequest.of(page, size), email, fullname, lastname, activityTypes);
        }
        return usersFoundByActivityFilterPage;
    }

    /**
     * Checks to see if users that were returned by the keyword search have all of the activity types that are
     * being filtered by and returns them.
     * @param page the current page number
     * @param size the amount of results to return
     * @param email email of the current user being searched
     * @param fullname fullname of the current user being searched
     * @param lastname lastname of the current user being searched
     * @param activityTypes activity types that users are being checked whether they have or not
     * @param searchTerms the keywords that are being searched for
     * @param searchTypes the type of the user search
     * @return a page of all users with that have all of these associated activity types
     */
    private Page<User> filterUsersActivityTypesAnd(int page, int size, String email, String fullname, String lastname,
                                                  Set<ActivityType> activityTypes, Set<String> searchTerms,
                                                  String searchTypes) {
        Page<User> usersFoundByActivityFilterPage;
        List<User> usersFoundByActivityFilterList = new ArrayList<>();
        ArrayList<String> searchTermsList = new ArrayList<>(searchTerms);

        if (searchTypes.equals("email")) {
            for (int i =0; i<searchTerms.size(); i++) {
                usersFoundByActivityFilterPage = userRepository.getUsersWithActivityTypeAnd(
                        PageRequest.of(page, size), searchTermsList.get(i), fullname, lastname, activityTypes);
                usersFoundByActivityFilterList.addAll(usersFoundByActivityFilterPage.getContent());
            }
            usersFoundByActivityFilterPage = new PageImpl<>(usersFoundByActivityFilterList);
        } else if (searchTypes.equals("lastname")) {
            for (int i =0; i<searchTerms.size(); i++) {
                usersFoundByActivityFilterPage = userRepository.getUsersWithActivityTypeAnd(
                        PageRequest.of(page, size), email, fullname, searchTermsList.get(i), activityTypes);
                usersFoundByActivityFilterList.addAll(usersFoundByActivityFilterPage.getContent());
            }
            usersFoundByActivityFilterPage = new PageImpl<>(usersFoundByActivityFilterList);
        } else {
            usersFoundByActivityFilterPage = userRepository.getUsersWithActivityTypeAnd(
                    PageRequest.of(page, size), email, fullname, lastname, activityTypes);
        }
        return usersFoundByActivityFilterPage;
    }

    /**
     * Checks the list of users returned by the keyword filter and the activity type filter and returns users that are
     * in both
     * @param page current page number
     * @param size number of results to display on page
     * @param usersFoundByActivityFilterList list of users that were found by the activity filter search
     * @param usersFoundByKeywordFilterList list of users that were found by the keyword filter search
     * @return a list of users that were in both of the lists
     */
    private Page<User> getCommonItemsBetweenLists(int page, int size, List<User> usersFoundByActivityFilterList, List<User> usersFoundByKeywordFilterList) {
        int start = page * 10;
        int end = page + size;
        int keywordFilterListEnd = 0;
        int list2End = 0;
        List<User> userListFinal = new ArrayList<>();
        Page<User> userPageFinal = null;

        if (usersFoundByActivityFilterList.size() != 0 && usersFoundByKeywordFilterList.size() != 0) {
            keywordFilterListEnd = Math.min(usersFoundByActivityFilterList.size(), end);
            list2End = Math.min(usersFoundByKeywordFilterList.size(), end);
            if (usersFoundByActivityFilterList.size() > start && usersFoundByKeywordFilterList.size() > start) {
                userListFinal = getCommonItemsBetweenLists(usersFoundByActivityFilterList,
                        usersFoundByActivityFilterList, start, keywordFilterListEnd, list2End);
            }
            userPageFinal = new PageImpl<>(userListFinal);
        } else if (usersFoundByActivityFilterList.size() != 0 && usersFoundByActivityFilterList.size() >
                start && usersFoundByKeywordFilterList.size() == 0) {
            userPageFinal = new PageImpl<>(usersFoundByKeywordFilterList);
        } else if (usersFoundByActivityFilterList.size() == 0 && usersFoundByKeywordFilterList.size() !=
                0 && usersFoundByKeywordFilterList.size() > start) {
            userPageFinal = new PageImpl<>(usersFoundByActivityFilterList);
        }
        return userPageFinal;
    }

}
