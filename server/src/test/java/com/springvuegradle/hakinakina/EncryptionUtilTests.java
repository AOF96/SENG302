package com.springvuegradle.Hakinakina;

import com.springvuegradle.Hakinakina.util.EncryptionUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class EncryptionUtilTests {
    @Test
    public void passwordsHashToSameValueWithSameSalt() {
        String initialPassword = "abc123";
        try {
            String salt = EncryptionUtil.getNewSalt();
            String initialPasswordEncrypted = EncryptionUtil.getEncryptedPassword(initialPassword, salt);
            String loginPassword = "abc123";
            String loginPasswordEncrypted = EncryptionUtil.getEncryptedPassword(loginPassword, salt);
            assertEquals(initialPasswordEncrypted, loginPasswordEncrypted);
        } catch (Exception e) {
        }
    }

    @Test
    public void samePasswordsDoNotHashToSameValueWithDifferentSalt() {
        String initialPassword = "abc123";
        try {
            String salt = EncryptionUtil.getNewSalt();
            String initialPasswordEncrypted = EncryptionUtil.getEncryptedPassword(initialPassword, salt);

            String salt2 = EncryptionUtil.getNewSalt();
            String loginPassword = "abc123";
            String loginPasswordEncrypted = EncryptionUtil.getEncryptedPassword(loginPassword, salt2);

            assertNotEquals(initialPasswordEncrypted, loginPasswordEncrypted);
        } catch (Exception e) {
        }
    }


}
