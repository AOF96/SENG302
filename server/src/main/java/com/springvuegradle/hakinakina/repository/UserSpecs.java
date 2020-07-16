package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.ActivityType;
import com.springvuegradle.hakinakina.entity.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Set;

public class UserSpecs {

    /**
     *
     * @param activityTypes set of activity types selected by the user for searching other users with same activity types
     *
     */
    public static Specification<User> hasActivityTypeOfIDs(Set<ActivityType> activityTypes) {
        return (Specification<User>) (root, query, criteriaBuilder) -> {
            query.distinct(true);
            ListJoin<User, ActivityType> userJoinActivityType = root.joinList("user_id");
            System.out.println(userJoinActivityType);
            return userJoinActivityType.get("type_id").in(activityTypes);
        };
    }

    /**
     *
     * @param lastname entered by the logged in user to look for other users with the specific last name
     *
     */

    public static  Specification<User> hasUserofLastname(String lastname) {
        return (root, query, criteriaBuilder) -> {
            Predicate equalPredicate = criteriaBuilder.equal(root.get("last_name"), lastname);
            System.out.println(equalPredicate);
            return equalPredicate;
        };
    }

        }


