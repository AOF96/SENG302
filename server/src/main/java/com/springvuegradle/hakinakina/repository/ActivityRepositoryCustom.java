package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.Activity;

import java.util.List;
import java.util.Set;

public interface ActivityRepositoryCustom {
    List<Activity> findActivityNamesOr(Set<String> searchTerms);

    List<Activity> findActivityNamesAnd(Set<String> searchTerms);
}
