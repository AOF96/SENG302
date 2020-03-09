package com.springvuegradle.Hakinakina;

import com.springvuegradle.Hakinakina.entity.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmailTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailRepository emailRepository;

    @Test
    public void testInsertingSecondaryEmail() {
        Email email = new Email("johnsmith@gmail.com");
        emailRepository.save(email);

        long millis=System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        User user = new User("John", "Smith", "email@email.com", "1985-12-20", Gender.MALE, 3,
                "JohnS");
        userRepository.save(user);

        user.addEmail(email);
        userRepository.save(user);

        assertEquals("[johnsmith@gmail.com]", user.getEmails().toString());
    }

}
