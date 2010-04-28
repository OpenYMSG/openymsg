package org.openymsg.network.challenge;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public class UnixMD5CryptTest {

    /**
     * Test method for {@link org.openymsg.network.challenge.UnixCrypt#md5Crypt(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testMd5CryptStringString() throws Exception {
        assertEquals("$1$caeiHQwX$hsKqOjrFRRN6K32OWkCBf1$802KwXGXHHXviR1gYFb580", UnixCrypt.md5Crypt("mykey",
                "caeiHQwX$hsKqOjrFRRN6K32OWkCBf1"));
    }
}
