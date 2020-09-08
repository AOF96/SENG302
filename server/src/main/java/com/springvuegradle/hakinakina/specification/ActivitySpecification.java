package com.springvuegradle.hakinakina.specification;

import com.springvuegradle.hakinakina.entity.Activity;
import org.springframework.data.jpa.domain.Specification;

public class ActivitySpecification {
    public static Specification<Activity> searchByActivityName(String activityName) {
        return (root, query, criteriaBuilder) -> {
            if (activityName == null) {
                return criteriaBuilder.and();
            }
            if (isWithinQuotation(activityName)) {
                String activityNameWithoutQuotation = activityName.substring(1, activityName.length() - 1);

                return criteriaBuilder.equal(root.get("name"), activityNameWithoutQuotation);
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + activityName.toLowerCase() + "%");
        };
    }

    /**
     * Helper function used in searchByActivityName
     * checks whether the strings given has quotation marks (" or ') around the search term string.
     * @param searchText text(string) you are using to search for an activity
     * @return true if quotation wraps the search term, false otherwise
     */
    private static boolean isWithinQuotation(String searchText) {
        if (searchText != null && searchText.length() > 1) {
            return searchText.startsWith("\"") && searchText.endsWith("\"") || searchText.startsWith("'") && searchText.endsWith("'");
        }
        return false;
    }
}
