package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.Activity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ActivityRepositoryCustomImpl implements ActivityRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Activity> findActivityNamesOr(Set<String> searchTerms) {
        CriteriaBuilder  cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Activity> query = cb.createQuery(Activity.class);
        Root<Activity> activity = query.from(Activity.class);

        Path<String> namePath = activity.get("name");

        List<Predicate> predicates = new ArrayList<>();
        for (String term : searchTerms) {
            predicates.add(cb.like(namePath, term));
        }
        query.select(activity)
                .where(cb.or(predicates.toArray(new Predicate[predicates.size()])));

        return entityManager.createQuery(query)
                .getResultList();
    }

    @Override
    public List<Activity> findActivityNamesAnd(Set<String> searchTerms) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT a FROM Activity a WHERE activity_name ");
        ArrayList<String> searchTermsList = new ArrayList<>(searchTerms);
        for (int i = 0; i < searchTermsList.size(); i++) {
            if (i != searchTermsList.size() - 1) {
                queryString.append("LIKE '%").append(searchTermsList.get(i)).append("%' AND activity_name ");
            } else {
                queryString.append("LIKE '%").append(searchTermsList.get(i)).append("%'");
            }
        }

        TypedQuery<Activity> query = entityManager.createQuery(queryString.toString(), Activity.class);

        return query.getResultList();
    }
}
