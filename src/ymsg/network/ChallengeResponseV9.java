package ymsg.network;

import java.security.NoSuchAlgorithmException;

class ChallengeResponseV9 extends ChallengeResponseUtility
{	final static int[] CHECKSUM_POS = { 7,9,15,1,3,7,9,15 };

	final static int USERNAME=0,PASSWORD=1,CHALLENGE=2;
	final static int[][] STRING_ORDER = 
	{	{ PASSWORD,USERNAME,CHALLENGE } ,	// 0
		{ USERNAME,CHALLENGE,PASSWORD } ,	// 1
		{ CHALLENGE,PASSWORD,USERNAME } ,	// 2
		{ USERNAME,PASSWORD,CHALLENGE } ,	// 3
		{ PASSWORD,CHALLENGE,USERNAME } ,	// 4
		{ PASSWORD,USERNAME,CHALLENGE } ,	// 5
		{ USERNAME,CHALLENGE,PASSWORD } ,	// 6
		{ CHALLENGE,PASSWORD,USERNAME }		// 7
	};

	// -----------------------------------------------------------------
	// Given a username, password and challenge string, this code returns
	// the two valid response strings needed to login to Yahoo
	// -----------------------------------------------------------------
	static String[] getStrings(String username,String password,String challenge)
	throws NoSuchAlgorithmException
	{	String[] s = new String[2];
		s[0] = yahoo64(md5(password));
		s[1] = yahoo64(md5(md5Crypt(password,"$1$_2S43d5f")));

		//System.out.println(s[0]+" "+s[1]);
		
		int mode = challenge.charAt(15) % 8;
		
		// -----The mode determines the 'checksum' character
		char c = challenge.charAt
		(	challenge.charAt
			(	CHECKSUM_POS[mode]
			) % 16
		);
		
		// -----Depending upon the mode, the various strings are combined
		// -----differently
		s[0] = yahoo64( md5( c+combine(username,s[0],challenge,mode) ) );
		s[1] = yahoo64( md5( c+combine(username,s[1],challenge,mode) ) );
		
		return s;
	}
	
	// -----------------------------------------------------------------
	// The 'mode' (see getStrings() above) determines the order the
	// various strings and the hashed/encyrpted password are concatenated.
	// For efficiency I stuff all the values into an array and use a
	// table to determine the order they should be glued together.
	// -----------------------------------------------------------------
	private static String combine(String u,String p,String c,int mode)
	{	String s = ""; 
		String[] sa = { u,p,c };
		for(int i=0;i<3;i++)  s=s+sa[STRING_ORDER[mode][i]];
		return s;
	}

	
	// -----------------------------------------------------------------
	// Test code (these are all fake u/p's - so don't bother trying them!)
	// -----------------------------------------------------------------
	/*public static void main(String[] args)
	{	try
		{	String[] s = ChallengeResponseV9.getStrings("javakid","dikavaj","S3qrCsgTteaSjH6GUbZOqg--");
			System.out.println(s[0]+" "+s[1]);
			
			s = ChallengeResponseV9.getStrings("spidey","peterparker","AkONHp_jJNV4SPEDWXw2cg--");
			System.out.println(s[0]+" "+s[1]);
			
			s = ChallengeResponseV9.getStrings("luke_skywalker","darthishisfather","eyM63R_CYgCuzjGw7nJgGg--");
			System.out.println(s[0]+" "+s[1]);
			
			s = ChallengeResponseV9.getStrings("sminkypinky","youaintseenmeright","G4MsFLd05F_TgD16VtdNBw--");
			System.out.println(s[0]+" "+s[1]);
		}catch(Exception e) { e.printStackTrace(); }
	}*/
}
