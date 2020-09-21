package com.springvuegradle.hakinakina.specification;

import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.util.SearchUtil;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.List;

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
}
