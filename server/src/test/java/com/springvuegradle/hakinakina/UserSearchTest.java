package com.springvuegradle.hakinakina;

import com.springvuegradle.hakinakina.dto.SearchUserDto;
import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserSearchTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService service;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll(); //this deletes admin from this test file

        User mayuko = new User();
        setupUser(mayuko, "Mayuko", null, "Williams", "mayuko@acnh.com", 0);
        User walter = new User();
        setupUser(walter, "Walter", "Compiler", "Guttman", "walter@acnh.com", 0);
        User fabian = new User();
        setupUser(fabian, "Fabian", "Scrum", "Gilson", "fabian@acnh.com", 0);
        User marina = new User(); // Default Admin
        setupUser(marina, "Marina", "Ski", "Filipovic", "marina@acnh.com", 2);
    }

    void setupUser(User user, String fName, String mName, String lName, String email, int pLevel) {
        user.setFirstName(fName);
        user.setMiddleName(mName);
        user.setLastName(lName);
        user.setPrimaryEmail(email);
        user.setPermissionLevel(pLevel);
        userRepository.save(user);
    }


    @Test
    public void testUserSearchWithExistingEmailShouldReturnUser() {
        Page<SearchUserDto> page = service.findPaginatedByQuery(
                0,
                10,
                "fabian@acnh.com",
                null,
                null,
                null,
                "and"
        );
        List<SearchUserDto> content = page.getContent();
        assertEquals("Fabian", content.get(0).getFirstname());
    }


    @Test
    public void testUserSearchWithExistingLastNameShouldReturnUser() {
        Page<SearchUserDto> page = service.findPaginatedByQuery(
                0,
                10,
                null,
                null,
                "Gilson",
                null,
                "and"
        );
        List<SearchUserDto> content = page.getContent();
        assertEquals("Fabian", content.get(0).getFirstname());
    }


    @Test
    public void testUserSearchWithExistingFullNameShouldReturnUser() {
        Page<SearchUserDto> page = service.findPaginatedByQuery(
                0,
                10,
                null,
                "Mayuko Williams",
                null,
                null,
                "and"
        );

        long resultCount = page.getTotalElements();
        assertEquals(1, resultCount);
    }


    @Test
    public void testSearchUserWithInvalidPageShouldReturnNoUser() {
        try {
            service.findPaginatedByQuery(
                    -1,
                    10,
                    null,
                    null,
                    "Lambkins",
                    null,
                    null
            );
            fail("Should not reach this, exception should be thrown");
        } catch (IllegalArgumentException e) {
        }
    }


    @Test
    public void testSearchUserWithInvalidSizeShouldReturnNoUser() {
        try {
            service.findPaginatedByQuery(
                    1,
                    -5,
                    null,
                    null,
                    "Lambkins",
                    null,
                    null
            );
            fail("Should not reach this, exception should be thrown");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testSearchDefaultAdminShouldReturnNoUser() {
        Page<SearchUserDto> page = service.findPaginatedByQuery(
                0,
                10,
                null,
                "Marina Ski Filipovic",
                null,
                null,
                "and"
        );

        long resultCount = page.getTotalElements();
        assertEquals(0, resultCount);
    }


}
