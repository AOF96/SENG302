package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.Activity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ActivityRepositoryCustomImpl implements ActivityRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This function takes a set of search terms and creates and executes a query that gets all
     * activities with at least one of the search terms in the name.
     * @param searchTerms A set of terms that the user wants to search for e.g. Fun and Exciting
     * @return a list of activities who's name contains at least one of the search terms.
     */
    @Override
    public List<Activity> findActivityNamesOr(Set<String> searchTerms) {
        CriteriaBuilder  cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Activity> query = cb.createQuery(Activity.class);
        Root<Activity> activity = query.from(Activity.class);

        Path<String> namePath = activity.get("name");

        List<Predicate> predicates = new ArrayList<>();
        for (String term : searchTerms) {
            predicates.add(cb.like(namePath, "%" + term + "%"));
        }
        query.select(activity)
                .where(cb.or(predicates.toArray(new Predicate[predicates.size()])));

        return entityManager.createQuery(query)
                .getResultList();
    }

    /**
     * This function takes a set of search terms and creates and executes a query that gets all
     * activities with all of the search terms in the name.
     * @param searchTerms A set of terms that the user wants to search for e.g. Fun and Exciting
     * @return a list of activities who's name contains all of the search terms.
     */
    @Override
    public List<Activity> findActivityNamesAnd(Set<String> searchTerms) {
        if (searchTerms.size() == 0) {
            return new ArrayList<Activity>();
        } else {
            CriteriaBuilder  cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Activity> query = cb.createQuery(Activity.class);
            Root<Activity> activity = query.from(Activity.class);

            Path<String> namePath = activity.get("name");

            List<Predicate> predicates = new ArrayList<>();
            for (String term : searchTerms) {
                predicates.add(cb.like(namePath, "%" + term + "%"));
            }
            query.select(activity)
                    .where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

            return entityManager.createQuery(query)
                    .getResultList();
        }
    }
}
