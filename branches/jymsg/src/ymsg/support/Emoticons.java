package ymsg.support;

public interface Emoticons
{	public final static int NULL = 0;

	public final static int SMILE = 1;			//  :)
	public final static int SAD = 2;			//  :(
	public final static int WINK = 3;			//  ;)
	public final static int GRIN = 4;			//  :D
	public final static int EYELASHES = 5;		//  ;;)

	public final static int CONFUSED = 6;		//  :-/
	public final static int LOVE_STRUCK = 7;	//  :x
	public final static int BLUSH = 8;			//  :">
	public final static int TONGUE = 9;			//  :p
	public final static int KISS = 10;			//  :*

	public final static int SHOCK = 11;			//  :O
	public final static int ANGER = 12;			//  X-(
	public final static int SMUG = 13;			//  :>
	public final static int COOL = 14;			//  B-)
	public final static int WORRIED = 15;		//  :-s

	public final static int DEVILISH = 16;		//  >:)
	public final static int CRYING = 17;		//  :((
	public final static int LAUGHING = 18;		//  :))
	public final static int STRAIGHT_FACE = 19;	//  :|
	public final static int EYEBROW = 20;		//  /:)

	public final static int ANGEL = 21;			//  O:)
	public final static int NERD = 22;			//  :-B
	public final static int TALK_TO_HAND = 23;	//  =;
	public final static int SLEEP = 24;			//  I-)
	public final static int ROLLING_EYES = 25;	//  8-|

	public final static int SICK = 26;			//  :-&
	public final static int SHHH = 27;			//  :-$
	public final static int NOT_TALKING = 28;	//  [-(
	public final static int CLOWN = 29;			//  :o)
	public final static int SILLY = 30;			//  8-}

	public final static int TIRED = 31;			//  (:|
	public final static int DROOLING = 32;		//  =P~
	public final static int THINKING = 33;		//  :-?
	public final static int DOH = 34;			//  #-o
	public final static int APPLAUSE = 35;		//  =D>
	
	public final static int PIG = 36;			//	:@)
	public final static int COW = 37;			//	3:-O
	public final static int MONKEY = 38;		//	:(|)
	public final static int CHICKEN = 39;		//	~:>
	public final static int ROSE = 40;			//	@};-

	public final static int GOOD_LUCK = 41;		//	%%-
	public final static int FLAG = 42;			//	**==
	public final static int PUMPKIN = 43;		//	(~~)
	public final static int COFFEE = 44;		//	~o)
	public final static int IDEA = 45;			//	*-:)

	public final static int SKULL = 46;			//	8-X
	public final static int BUG = 47;			//	=:)
	public final static int ALIEN = 48;			//	>-)
	public final static int FRUSTRATED = 49;	//	:-L
	public final static int COWBOY = 50;		//	<):)
	
	public final static int PRAYING = 51;		//	[-o<
	public final static int HYPNOTIZED = 52;	//	@-)
	public final static int MONEY_EYES = 53;	//	$-)
	public final static int WHISTLING = 54;		//	:-"
	public final static int LIAR_LIAR = 55;		//	:^o

	public final static int BEAT_UP = 56;		//	b-(
	public final static int PEACE = 57;			//	:)>-
	public final static int SHAME_ON_YOU = 58;	//	[-X
	public final static int DANCING = 59;		//	\:D/
	public final static int HUGS = 60;			//	>:D<

	public final static int WHEW = 61;			//  #:-S
	public final static int BROKEN_HEART = 62;	//	=((
	public final static int ROFL = 63;			//	=))
	public final static int LOSER = 64;			//	L-)
	public final static int PARTY = 65;			//	<:-P

	public final static int NAIL_BITING = 66;	//	:-SS
	public final static int WAITING = 67;		//	:-w
	public final static int SIGH = 68;			//	:-<
	public final static int PHBBBBT = 69;		//	>:P
	public final static int BRING_IT_ON = 70;	//	>:/

	public final static int TEE_HEE = 71;		//	;))
	public final static int CHATTERBOX = 72;	//	:-@
	public final static int NOT_WORTHY = 73;	//	^:)^
	public final static int GO_ON = 74;			//	:-j
	public final static int STAR = 75;			//	(*)  	
	

	public final static String[][] EMOTICONS =
	{	{},
		{	":)" , ":-)"	},					// Smile (1)
		{	":(" , ":-("	},					// Sad (2)
		{	";)" , ";-)"	},					// Wink (3)
		{	":D" , ":-D"	},					// Grin (4)
		{	";;)" 			},					// Eyelashes (5)

		{	":-/"	},							// Confused (6)
		{	":x" , ":X" , ":-x" , ":-X" },		// Love struck (7)
		{	":\">"	},							// Blush (8)
		{	":p" , ":-p"	},					// Tongue (9)
		{	":*" , ":-*"	},					// Kiss (10)

		{	":O" , ":o" , ":-O", ":-o"	},		// Shock (11)
		{	"X-("			},					// Anger (12)
		{	":>" , ":->"	},					// Smug (13)
		{	"B)" , "B-)"	},					// Cool (14)
		{	":s" , ":S" , ":-s" , ":-S"	},		// Worried (15)

		{	">:)"	},							// Devilish (16)
		{	":(("	},							// Crying (17)
		{	":))"	},							// Laughing (18)
		{	":|" , ":-|"	},					// Straight face (19)
		{	"/:)"	},							// Eyebrow (20)

		{	"O:)" , "o:)"	},					// Angel (21)
		{	":-B"	},							// Nerd (22)
		{	"=;"	},							// Talk to the hand (23)
		{	"I-)" , "|-)"	},					// Sleep (24)
		{	"8-|"	},							// Rolling eyes (25)

		{	":-&"	},							// Sick (26)
		{	":-$"	},							// Shhh (27)
		{	"[-("	},							// Not talking (28)
		{	":o)"	},							// Clown (29)
		{	"8-}"	},							// Silly (30)

		{	"(:|"	},							// Tired (31)
		{	"=P~"	},							// Drooling (32)
		{	":-?"	},							// Thinking (33)
		{	"#-o" , "#-O"	},					// D'oh (34)
		{	"=D>"	},							// Applause (35)

		{	":@)"	},							// Pig (36)
		{	"3:-O"	},							// Cow (37)
		{	":(|)"	},							// Monkey (38)
		{	"~:>"	},							// Chicken (39)
		{	"@};-"	},							// Rose (40)

		{	"%%-"	},							// Good luck (41)
		{	"**=="	},							// Flag (42)
		{	"(~~)"	},							// Pumpkin (43)
		{	"~o)"	},							// Coffee (44)
		{	"*-:)"	},							// Idea (45)

		{	"8-X"	},							// Skull (46)
		{	"=:)"	},							// Bug (47)
		{	">-)"	},							// Alien (48)
		{	":-L"	},							// Frustrated (49)
		{	"<):)"	},							// Cowboy (50)

		{	"[-o"	},							// Praying (51)
		{	"<@-)"	},							// Hypnotised (52)
		{	"$-)"	},							// Money eyes (53)
		{	":-\""	},							// Whistling (54)
		{	":^o"	},							// Liar liar (55)

		{	"b-("	},							// Beat up (56)
		{	":)>-"	},							// Peace (57)
		{	"[-X"	},							// Shame on you (58)
		{	"\\:D/"	},							// Dancing (59)
		{	">:D<"	},							// Hugs (60)

		{	"#:-S"	},							// Whew (61)
		{	"=(("	},							// Broken heart (62)
		{	"=))"	},							// R.O.F.L. (63)
		{	"L-)"	},							// Loser (64)
		{	"<:-P"	},							// Party (65)

		{	":-SS"	},							// Nail biting (66)
		{	":-w"	},							// Waiting (67)
		{	":-<"	},							// Sigh (68)
		{	">:P"	},							// Phbbbbt (raspberry) (69)
		{	">:/"	},							// Bring it on (70)

		{	";))"	},							// Tee hee (71)
		{	":-@"	},							// Chatterbox (72)
		{	"^:)^"	},							// Not worthy (73)
		{	":-j"	},							// Go on (74)
		{	"(*)"	}							// Star (75)
	};
}
