package ymsg.network;

import java.security.*;

/**
 * @author Damian Minkov
 */
class ChallengeResponseV16 extends ChallengeResponseUtility
{
    // -----------------------------------------------------------------
    // Given a username, password and challenge string, this code returns
    // the two valid response strings needed to login to Yahoo
    // -----------------------------------------------------------------
    static String[] getStrings(String cookieY, String cookieT, String challenge)
        throws NoSuchAlgorithmException
    {
        String[] s = new String[3];
        s[0] = cookieY;
        s[1] = cookieT;

        byte[] md5_digest = md5(challenge);
        String base64_string = yahoo64(md5_digest);
        s[2] = base64_string;

        return s;
    }
}
