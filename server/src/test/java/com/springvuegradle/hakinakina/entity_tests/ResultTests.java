package com.springvuegradle.hakinakina.entity_tests;

import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.AchievementRepository;
import com.springvuegradle.hakinakina.repository.ResultRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ResultTests {

    @Autowired
    AchievementRepository achievementRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ResultRepository resultRepository;

    @Test
    public void testCreatingNewAchievement() {
        Achievement achievement = new Achievement("Test", "Test", ResultType.TIME);
        achievementRepository.save(achievement);

        User user = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");
        userRepository.save(user);

        Result result = new Result("1:52");
        resultRepository.save(result);

        achievement = achievementRepository.findAll().get(0);
        user = userRepository.findUserByEmail("jacky@google.com");
        achievement.addResult(result);
        user.addResult(result);
        achievementRepository.save(achievement);
        userRepository.save(user);

        assertEquals("Maurice", resultRepository.findAll().get(0).getUser().getFirstName());
        assertEquals("Test", resultRepository.findAll().get(0).getAchievement().getName());
    }
}