package com.springvuegradle.hakinakina;

import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class StartupAdminGeneratorTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void testAdminIsCreated() {
        User admin = userRepository.findByPermissionLevelEquals(2);
        assertNotNull(admin);
    }

}
