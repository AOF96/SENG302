package com.springvuegradle.hakinakina.specification;

import com.springvuegradle.hakinakina.entity.Email;
import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.util.SearchUtil;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.SetJoin;

public class UserSpecification {
    /**
     * Search users with their last name and return appropriate criteria builder depending on the inputs
     * @param lastName user's last name
     * @return specification object with User search request (WHERE part of a query)
     */
    public static Specification<User> searchByLastName(String lastName) {
        return (root, query, criteriaBuilder) -> {
            if (lastName == null) {
                return criteriaBuilder.and();
            }
            if (SearchUtil.isWithinQuotation(lastName)) {
                String lastNameWithoutQuotation = lastName.substring(1, lastName.length() - 1);

                return criteriaBuilder.equal(root.get("lastName"), lastNameWithoutQuotation);
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
        };
    }


    /**
     * Search users with their full name and return appropriate criteria builder depending on the inputs
     * @param fullName user's full name (can be with or without middle name)
     * @return specification object with User search request (WHERE part of a query)
     */
    public static Specification<User> searchByFullName(String fullName) {
        return (root, query, criteriaBuilder) -> {
            if (fullName == null) {
                return null;
            }
            Expression<String> fName = criteriaBuilder.concat(root.get("firstName"), " ");
            Expression<String> mName = criteriaBuilder.concat(root.get("middleName"), " ");
            Expression<String> lName = root.get("lastName");

            Expression<String> fullNameQuery = criteriaBuilder.concat(criteriaBuilder.concat(fName, mName), lName);
            Expression<String> fullWithoutMiddleNameQuery = criteriaBuilder.concat(fName, lName);

            if (SearchUtil.isWithinQuotation(fullName)) {
                String fullNameWithoutQuotation = fullName.substring(1, fullName.length() - 1);
                return criteriaBuilder.or(
                        criteriaBuilder.equal(fullNameQuery, fullNameWithoutQuotation),
                        criteriaBuilder.equal(fullWithoutMiddleNameQuery,fullNameWithoutQuotation)
                );
            }

            return criteriaBuilder.or(
                    criteriaBuilder.like(fullNameQuery, "%" + fullName + "%"),
                    criteriaBuilder.like(fullWithoutMiddleNameQuery, "%" + fullName + "%")
            );
        };
    }


    /**
     * Search users with email address and return appropriate criteria builder depending on the inputs
     * @param email user's email address
     * @return specification object with User search request (WHERE part of a query)
     */
    public static Specification<User> searchByEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null) {
                return null;
            }
            SetJoin<User, Email> userJoinEmail = root.joinSet("emails", JoinType.LEFT);
            if (SearchUtil.isWithinQuotation(email)) {
                String emailWithoutQuotation = email.substring(1, email.length() - 1);
                return criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("primaryEmail"), emailWithoutQuotation),
                        criteriaBuilder.equal(userJoinEmail.get("email"), emailWithoutQuotation)
                );
            }
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("primaryEmail"), "%" + email + "%"),
                    criteriaBuilder.like(userJoinEmail.get("email"), "%" + email + "%"));
        };
    }


    /**
     * Exclude Default admin from the search result by only getting users with permission level of less than 2
     * @return specification object with User search request (WHERE part of a query)
     */
    public static Specification<User> searchIsNotAdmin() {
        return(root, query, criteriaBuilder) -> {
            return criteriaBuilder.lessThan(root.get("permissionLevel"), 2);
        };
    }
}