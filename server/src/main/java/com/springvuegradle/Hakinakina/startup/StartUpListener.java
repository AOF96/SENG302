package com.springvuegradle.Hakinakina.startup;

import com.springvuegradle.Hakinakina.entity.User;
import com.springvuegradle.Hakinakina.entity.UserRepository;
import com.springvuegradle.Hakinakina.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Event listener class for ContextRefreshedEvent
 */

@Component
public class StartUpListener {

    @Autowired
    private UserRepository userRepository;

    private String email = "passageAdmin@gmail.com";


    private final String charSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOP1234567890";

    private int passwordLength = 15;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        User defaultAdmin = userRepository.findByPermissionLevelEquals(2);
        if (defaultAdmin != null) {
            return;
        }
        try {
            updatePermissionsInDatabase();
            createDefaultAdmin();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Parses through to set users permission level if null
     */
    private void updatePermissionsInDatabase() {

        for (User user : userRepository.findAll()) {
            if (user.getPermissionLevel() == null) {
                user.setPermissionLevel(0);
                userRepository.save(user);
            }
        }
    }

    private String createDefaultAdminPassword() {
        Random random = new Random();
        String password = "";
        for (int i = 0; i < passwordLength; i++) {
            int charPosition = random.nextInt(charSet.length());
            password += charSet.charAt(charPosition);
        }
        return password;
    }

    /**
     * Generates a default admin
     */
    private void createDefaultAdmin() throws Exception {
        User u = new User();
        String salt = EncryptionUtil.getNewSalt();
        String password = createDefaultAdminPassword();
        u.setEncryptedPassword(EncryptionUtil.getEncryptedPassword(password, salt));
        u.setPrimaryEmail(email);
        u.setPermissionLevel(2); // Default admin permission level
        u.setSalt(salt);

        System.out.println("=-=-=-=- GENERATED A NEW ADMIN USER -=-=-=");
        System.out.println(email);
        System.out.println(password);
        userRepository.save(u);
    }


}
