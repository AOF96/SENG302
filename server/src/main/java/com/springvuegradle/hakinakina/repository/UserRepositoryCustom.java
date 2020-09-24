package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.User;

import java.util.List;
import java.util.Set;

public interface UserRepositoryCustom {
    List<User> findUserNamesOr(Set<String> searchTerms, String searchType);

    List<User> findUserNamesAnd(Set<String> searchTerms, String searchType);

}
