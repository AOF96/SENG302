package com.springvuegradle.hakinakina.service_tests;

import com.springvuegradle.hakinakina.entity.Gender;
import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.repository.SearchRepository;
import com.springvuegradle.hakinakina.service.SearchService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchServiceTest {
    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private SearchService service;

    @BeforeAll
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getIntersectionOfListOfSetsOfUsersTest() {
        User user1 = new User("John", "Doe", "john@mail.com", "", Gender.MALE, 1, "coolPassword1");
        User user2 = new User("Jane", "Doe", "jane@mail.com", "", Gender.FEMALE, 2, "coolPassword2");
        User user3 = new User("Dane", "Joe", "dane@mail.com", "", Gender.NON_BINARY, 3, "coolPassword3");

        List<Set<User>> list = new ArrayList<>();
        Set<User> set1 = new HashSet<>();
        set1.add(user1);
        set1.add(user2);

        Set<User> set2 = new HashSet<>();
        set2.add(user2);
        set2.add(user3);

        Set<User> set3 = new HashSet<>();
        set3.add(user1);
        set3.add(user2);
        set3.add(user3);

        list.add(set1);
        list.add(set2);
        list.add(set3);

        List<User> expectedResult = new ArrayList<>();
        expectedResult.add(user2);
        assertEquals(expectedResult, service.getIntersectionOfListOfSetsOfUsers(list));
    }

    @Test
    public void getIntersectionOfListOfSetsOfUsersOneSetTest() {
        User user1 = new User("John", "Doe", "john@mail.com", "", Gender.MALE, 1, "coolPassword1");
        User user2 = new User("Jane", "Doe", "jane@mail.com", "", Gender.FEMALE, 2, "coolPassword2");
        user1.setUserId(2222L);
        user2.setUserId(2323L);
        List<Set<User>> list = new ArrayList<>();
        Set<User> set1 = new HashSet<>();
        set1.add(user1);
        set1.add(user2);
        list.add(set1);

        //Order of result is random, thus use several asserts instead of comparing two lists
        List<User> result = service.getIntersectionOfListOfSetsOfUsers(list);
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
    }

    @Test
    public void getIntersectionOfListOfSetsOfUsersEmptyListTest() {
        List<Set<User>> list = new ArrayList<>();
        assertEquals(0, service.getIntersectionOfListOfSetsOfUsers(list).size());
    }
}
