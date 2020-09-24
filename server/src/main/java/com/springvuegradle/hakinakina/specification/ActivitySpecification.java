package com.springvuegradle.hakinakina.specification;

import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.util.SearchUtil;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.SetJoin;

/**
 * Specification class for dynamic activity searching
 */
public class ActivitySpecification {
    /**
     * Search for an activity by the name and return appropriate criteria builder depending on the inputs
     * @param activityName activity's name
     * @return specification object with Activity search request (WHERE part of a query)
     */
    public static Specification<Activity> searchByActivityName(String activityName) {
        return (root, query, criteriaBuilder) -> {
            if (activityName == null) {
                return criteriaBuilder.and();
            }
            if (SearchUtil.isWithinQuotation(activityName)) {
                String activityNameWithoutQuotation = activityName.substring(1, activityName.length() - 1);

                return criteriaBuilder.equal(root.get("name"), activityNameWithoutQuotation);
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + activityName.toLowerCase() + "%");
        };
    }

    /**
     * Search for public activities and return appropriate criteria builder depending on the inputs.
     * @return specification object with Activity search request (WHERE part of a query)
     */
    public static Specification<Activity> searchPublicActivity() {
        return(root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("visibility"), 0);
        };
    }

    /**
     * Search activities which are created by the user searching activities, and return appropriate criteria builder
     * depending on the inputs.
     * @param  searchingUser, user object of the user who is doing the search
     * @return specification object with Activity search request (WHERE part of a query)
     */
    public static Specification<Activity> searchIsActivityAuthor(User searchingUser) {
        return(root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("author").get("userId"), searchingUser.getUserId());
        };
    }

    /**
     * Search activities which are shared to the user searching activities, and return appropriate criteria builder
     * depending on the inputs.
     * @param  searchingUser, user object of the user who is doing the search
     * @return specification object with Activity search request (WHERE part of a query)
     */
    public static Specification<Activity> searchIsActivityShared(User searchingUser) {
        return(root, query, criteriaBuilder) -> {
            return criteriaBuilder.isMember(searchingUser, root.get("usersShared"));
        };
    }
}
