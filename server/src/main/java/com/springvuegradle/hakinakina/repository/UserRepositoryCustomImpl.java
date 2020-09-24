package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This function takes a set of search terms and creates and executes a query that gets all
     * activities with at least one of the search terms in the name.
     * @param searchTerms A set of terms that the user wants to search for e.g. Fun and Exciting
     * @return a list of activities who's name contains at least one of the search terms.
     */
    @Override
    public List<User> findUserNamesOr(Set<String> searchTerms, String searchType) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> user = query.from(User.class);

        Path<String> namePath = null;

        if (searchType.equals("lastname")) {
             namePath = user.get("lastName");
        } else if (searchType.equals("email")) {
            namePath = user.get("primaryEmail");
        }

        List<Predicate> predicates = new ArrayList<>();
        for (String term : searchTerms) {
            predicates.add(cb.like(namePath, "%" + term + "%"));
        }
        query.select(user)
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
    public List<User> findUserNamesAnd(Set<String> searchTerms, String searchType) {
        if (searchTerms.size() == 0) {
            return new ArrayList<User>();
        } else {
            CriteriaBuilder  cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> user = query.from(User.class);

            Path<String> namePath = null;

            if (searchType.equals("lastname")) {
                namePath = user.get("lastName");
            } else if (searchType.equals("email")) {
                namePath = user.get("primaryEmail");
            }

            List<Predicate> predicates = new ArrayList<>();
            for (String term : searchTerms) {
                predicates.add(cb.like(namePath, "%" + term + "%"));
            }
            query.select(user)
                    .where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

            return entityManager.createQuery(query)
                    .getResultList();
        }
    }

}
