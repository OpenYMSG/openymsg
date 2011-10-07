package org.openymsg.network.challenge;

import org.junit.Test;

/**
 * Input validation for {@link UnixCrypt#md5Crypt(String, String)}
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public class UnixMD5CryptInputValidationTest {

    /**
     * Argument validation for md5Crypt(). The argument 'key' cannot be null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullKey() throws Exception {
        UnixCrypt.md5Crypt(null, "caeiHQwX$hsKqOjrFRRN6K32OWkCBf1");
    }

    /**
     * Argument validation for md5Crypt(). The argument 'key' cannot be an empty String.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyKey() throws Exception {
        UnixCrypt.md5Crypt("", "caeiHQwX$hsKqOjrFRRN6K32OWkCBf1");
    }

    /**
     * Argument validation for md5Crypt(). The argument 'salt' cannot be null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullSalt() throws Exception {
        UnixCrypt.md5Crypt("myPassword", null);
    }

    /**
     * Argument validation for md5Crypt(). The argument 'salt' cannot be an empty String.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptySalt() throws Exception {
        UnixCrypt.md5Crypt("myPassword", "");
    }
}
