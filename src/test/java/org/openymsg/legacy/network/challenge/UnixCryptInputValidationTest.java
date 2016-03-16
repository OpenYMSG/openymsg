package org.openymsg.legacy.network.challenge;

import org.junit.Test;

/**
 * Input validation for {@link UnixCrypt#crypt(String, String)}
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public class UnixCryptInputValidationTest {

    /**
     * Argument validation for Crypt(). The argument 'key' cannot be null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullKey() throws Exception {
        UnixCrypt.crypt(null, "$1$caeiHQwX$hsKqOjrFRRN6K32OWkCBf1");
    }

    /**
     * Argument validation for Crypt(). The argument 'key' cannot be an empty String.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyKey() throws Exception {
        UnixCrypt.crypt("", "$1$caeiHQwX$hsKqOjrFRRN6K32OWkCBf1");
    }

    /**
     * Argument validation for Crypt(). The argument 'salt' cannot be null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullSalt() throws Exception {
        UnixCrypt.crypt("myPassword", null);
    }

    /**
     * Argument validation for Crypt(). The argument 'salt' cannot be an empty String.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptySalt() throws Exception {
        UnixCrypt.crypt("myPassword", "");
    }

    /**
     * Currently, only MD5 crypt is supported. Attempting BlowFishCrypt should throw an exception Test method for
     * {@link org.openymsg.legacy.network.challenge.UnixCrypt#crypt(java.lang.String, java.lang.String)}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCryptForBlowFishSalt() throws Exception {
        UnixCrypt.crypt("myPassword", "$2a$12$eIAq8PR8sIUnJ1HaohxX2O9x9Qlm2vK97LJ5dsXdmB.eXF42qjchC");
    }

    /**
     * Currently, only MD5 crypt is supported. Attempting Extended Crypt should throw an exception Test method for
     * {@link org.openymsg.legacy.network.challenge.UnixCrypt#crypt(java.lang.String, java.lang.String)}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCryptForExtendedSalt() throws Exception {
        UnixCrypt.crypt("myPassword", "_w5xs42df");
    }

    /**
     * Currently, only MD5 crypt is supported. Attempting MD5 Crypt should NOT throw an exception Test method for
     * {@link org.openymsg.legacy.network.challenge.UnixCrypt#crypt(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testCryptForMD5Salt() throws Exception {
        UnixCrypt.crypt("myPassword", "$1$caeiHQwX$hsKqOjrFRRN6K32OWkCBf1");
    }

    /**
     * Currently, only MD5 crypt is supported. Attempting Crypt without a recognized method in the salt should throw an
     * exception Test method for
     * {@link org.openymsg.legacy.network.challenge.UnixCrypt#crypt(java.lang.String, java.lang.String)}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCryptForUnknownSalt() throws Exception {
        UnixCrypt.crypt("myPassword", "$9$caeiHQwX$hsKqOjrFRRN6K32OWkCBf1");
    }

    /**
     * Currently, only MD5 crypt is supported. Attempting Crypt without a method in the salt should throw an exception
     * Test method for {@link org.openymsg.legacy.network.challenge.UnixCrypt#crypt(java.lang.String, java.lang.String)}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCryptWithoutSalt() throws Exception {
        UnixCrypt.crypt("myPassword", "caeiHQwX$hsKqOjrFRRN6K32OWkCBf1");
    }

    /**
     * Attempting Crypt without a salt Stripped from its method should throw an exception Test method for
     * {@link org.openymsg.legacy.network.challenge.UnixCrypt#crypt(java.lang.String, java.lang.String)}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCryptForHalfSalt() throws Exception {
        UnixCrypt.crypt("myPassword", "$caeiHQwX$hsKqOjrFRRN6K32OWkCBf1");
    }

}
