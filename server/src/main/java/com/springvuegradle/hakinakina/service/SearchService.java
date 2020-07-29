package com.springvuegradle.hakinakina.service;

import com.springvuegradle.hakinakina.dto.SearchUserDto;
import com.springvuegradle.hakinakina.entity.ActivityType;
import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.repository.SearchRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Class that handles all the search functionality logic.
 */
@Service
public class SearchService {

    private UserRepository userRepository;
    private SearchRepository searchRepository;

    public SearchService(UserRepository userRepository,
                         SearchRepository searchRepository) {
        this.userRepository = userRepository;
        this.searchRepository = searchRepository;
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
        return userPageToSearchResponsePage(userPage);
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
        return userPageToSearchResponsePage(userPage);
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
     * Helper function used in findPaginated and findPaginatedByQuery,
     * creates SearchUserResponse object which has user email, full name and nickname details
     * from the list of users in page object returned by the query
     *
     * @param users Page object that contains list of users found by the query
     * @return Page object with list of SearchUserResponse object
     */
    private Page<SearchUserDto> userPageToSearchResponsePage(Page<User> users) {
        List<SearchUserDto> userResponses = new ArrayList<>();
        for (User user : users) {
            SearchUserDto searchUserDto = new SearchUserDto();
            searchUserDto.setEmail(user.getPrimaryEmail());
            searchUserDto.setFirstname(user.getFirstName());
            searchUserDto.setLastname(user.getLastName());
            searchUserDto.setMiddlename(user.getMiddleName());
            searchUserDto.setNickname(user.getNickName());
            searchUserDto.setActivityTypes(user.getActivityTypes());
            userResponses.add(searchUserDto);
        }
        return new PageImpl<>(userResponses);
    }

    /***
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
}
