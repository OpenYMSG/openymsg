package ymsg.support;

import javax.swing.*;
import javax.swing.text.Document;
import java.util.Stack;

// *********************************************************************
// This message decoder class is designed to work along side the main
// jYMSG9 classes, accepting raw message strings from Yahoo, and trans-
// lating them into something a bit more Java-friendly.
//
// Currently the decoder can translate messages into basic HTML (although
// fade is not supported) and plain text (formatting stripped).  It can
// also locate smiley (or 'emoticon' as Yahoo calls them) text strings.
// *********************************************************************
public class MessageDecoder
{	protected char[] msg;						// Incoming message as chars
	protected StringBuffer out;					// Decode to this buffer
	protected int pos;							// Char position in incoming msg	
	protected Stack stack;						// Element stack
	private MessageDecoderSettings settings=null;
	
	protected static final char ESC = 0x1b;
	protected static final String ESC_SEQ = "\u001b[";

	// -----------------------------------------------------------------
	// CONSTRUCTOR
	// -----------------------------------------------------------------
	public MessageDecoder()
	{	out = new StringBuffer();
		stack = new Stack();
	}

	public MessageDecoder(MessageDecoderSettings md)
	{	this();  settings=md;
	}


	// -----------------------------------------------------------------
	// Accessors
	// -----------------------------------------------------------------
	public void setDecoderSettings(MessageDecoderSettings md) { settings=md; }
	public MessageDecoderSettings getDecoderSettings() { return settings; }


	// -----------------------------------------------------------------
	// Decode message
	// -----------------------------------------------------------------
	public synchronized MessageElement decode(String m)
	{	msg=m.toCharArray();  out.setLength(0);
		stack.removeAllElements();  pos=0;

		MessageElement section = new MessageElement(settings,MessageElement.ROOT);
		stack.push(section);

		while(pos<msg.length)
		{	//System.out.println(msg[pos]);
			if(startsWith("<font "))		handleFont();
			else if(startsWith("<fade "))	handleFade();
			else if(startsWith("<alt "))	handleAlt();
			else if(startsWith(ESC_SEQ))	handleEscape();
			else if(startsWith("</font>"))	handleFontExit();
			else if(startsWith("</fade>"))	handleFadeExit();
			else if(startsWith("</alt>"))	handleAltExit();
			else if(startsWith("<"))		handleOtherTag();
			else							out.append(msg[pos]);
			pos++;
		}
		addText();
		return section;
	}

	// -----------------------------------------------------------------
	// All-in-one decoder methods for convenience
	// -----------------------------------------------------------------
	public String decodeToHTML(String m)
	{	MessageElement me = decode(m);
		return me.toHTML();
	}

	public String decodeToText(String m)
	{	MessageElement me = decode(m);
		return me.toText();
	}

	public void appendToDocument(String m,Document doc)
	{	MessageElement me = decode(m);
		me.appendToDocument(doc);
	}

	// -----------------------------------------------------------------
	// Handler for each type of data
	// -----------------------------------------------------------------
	protected void handleFont()
	{	addText();  pos+=6;		// Skip over '<font '
		int end=nextNonLiteral('>');
		String s = new String(msg,pos,end-pos);
		add( new MessageElement(settings,MessageElement.FONT,s) );
		pos=end;
	}

	protected void handleFade()
	{	addText();  pos+=6;		// Skip over '<FADE '
		int end=nextNonLiteral('>');
		String s = new String(msg,pos,end-pos);
		add( new MessageElement(settings,MessageElement.FADE,s) );
		pos=nextNonLiteral('>');
	}

	protected void handleAlt()
	{	addText();  pos+=5;		// Skip over '<ALT '
		int end=nextNonLiteral('>');
		String s = new String(msg,pos,end-pos);
		add( new MessageElement(settings,MessageElement.ALT,s) );
		pos=nextNonLiteral('>');
	}

	protected void handleEscape()
	{	addText();  pos+=2;		// Skip over ESC [

		MessageElement section = null;
		char c=msg[pos];

		if(c>='1' && c<='4')		// Bold, italic, colour, underline
		{	// -----Add a new section to stack
			switch(c)
			{	case '1' : section = new MessageElement(settings,MessageElement.BOLD);  break;
				case '2' : section = new MessageElement(settings,MessageElement.ITALIC);  break;
				case '3' : section = new MessageElement(settings,MessageElement.COLOUR_INDEX,""+msg[pos+1]);  break;
				case '4' : section = new MessageElement(settings,MessageElement.UNDERLINE);  break;
			}
			add(section);
		}
		else if(c=='x')				// End: bold, italic, underline
		{	pos++;
			// -----Up stack to find matching opening section
			int ty=MessageElement.ROOT;
			switch(msg[pos])
			{	case '1' : ty=MessageElement.BOLD;  break;
				case '2' : ty=MessageElement.ITALIC;  break;
				case '4' : ty=MessageElement.UNDERLINE;  break;
			}
			if(ty>MessageElement.ROOT)  remove(ty);
		}
		else if(c=='#')				// Absolute colour #rrggbb
		{	pos++;
			String s = new String(msg,pos,6);
			section = new MessageElement(settings,MessageElement.COLOUR_ABS,s);
			add(section);
		}
		pos=nextNonLiteral('m');
	}
	
	protected void handleFontExit()
	{	addText();  pos+=6;
		remove(MessageElement.FONT);
	}

	protected void handleFadeExit()
	{	addText();  pos+=6;
		remove(MessageElement.FADE);
	}

	protected void handleAltExit()
	{	addText();  pos+=5;
		remove(MessageElement.ALT);
	}

	protected void handleOtherTag()
	{	addText();  pos+=1;
		int end=nextNonLiteral('>');
		String s = new String(msg,pos,end-pos).toLowerCase();		
		// -----Is this a colour name?  ie.  <red> <blue> ...etc...
		int i1=MessageElement.whichColourName(s);
		if(i1>=0)
		{	MessageElement section = new MessageElement(settings,MessageElement.COLOUR_NAME,i1);
			add(section);
			pos=end;
		}
		// -----Is this b i u or /b /i /u ?
		else if(s.equals("b")) { add(new MessageElement(settings,MessageElement.BOLD));  pos++; }
		else if(s.equals("/b")) { remove(MessageElement.BOLD);  pos+=2; }
		else if(s.equals("i")) { add(new MessageElement(settings,MessageElement.ITALIC));  pos++; }
		else if(s.equals("/i")) { remove(MessageElement.ITALIC);  pos+=2; }
		else if(s.equals("u")) { add(new MessageElement(settings,MessageElement.UNDERLINE));  pos++; }
		else if(s.equals("/u")) { remove(MessageElement.UNDERLINE);	 pos+=2; }
		// -----Is this a /colour name?  ie.  </red> </blue> ...etc...
		else if(s.length()>1 && s.charAt(0)=='/')
		{	int i2=MessageElement.whichColourName(s.substring(1));
			if(i2>=0)  remove(MessageElement.COLOUR_NAME,i2);  pos=end;
		}
		// -----<#rrggbb> ?
		else if(s.startsWith("#")) { add(new MessageElement(settings,MessageElement.COLOUR_ABS,s.substring(1))); pos+=7; }
		else
		{	// -----If we fail to identify the tag, put it out as text.  We've 
			// -----already swallowed the opening '<', so add that to the start 
			// -----of the new out buffer and let the parser pick up from the 
			// -----next character.  (Unswallow last character!)
			out.append('<');  pos--;
		}
	}

	// -----------------------------------------------------------------
	// Stack ops
	// -----------------------------------------------------------------
	private void add(MessageElement s)
	{	((MessageElement)stack.peek()).addChild(s);			// Add to current element
		stack.push(s);										// Become current element
	}

	private void remove(int type)
	{	if(stack.size()>1)
		{	MessageElement me=(MessageElement)stack.pop();
			while(me.type!=type && stack.size()>1)
			{	me=(MessageElement)stack.pop();
			}
		}
	}
	
	private void remove(int type,int col)
	{	if(stack.size()>1)
		{	MessageElement me=(MessageElement)stack.pop();
			while((me.type!=type || !me.colourEquals(col)) && stack.size()>1)
			{	me=(MessageElement)stack.pop();
			}
		}
	}

	private void addText()
	{	if(out.length()>0)
		{	MessageElement me=(MessageElement)stack.peek();
			MessageElement me2 = new MessageElement(settings,MessageElement.TEXT,out.toString());
			me.addChild(me2);
			out = new StringBuffer();
		}
	}

	// -----------------------------------------------------------------
	// Low level util methods
	// -----------------------------------------------------------------
	private boolean startsWith(String s)
	{	for(int i=0;i<s.length();i++)
		{	// -----Exit if we run out of msg half way through a successful match
			if(pos+i >= msg.length)  return false;
			// -----Otherwise, check next char in pattern
			char c=msg[pos+i];
			if(c>='A' && c<='Z')  c=Character.toLowerCase(c);
			if(c!=s.charAt(i))  return false;
		}
		return true;
	}

	private int nextNonLiteral(char t)
	{	boolean literal=false;
		int p=pos;
		while(p<msg.length && (msg[p]!=t || literal))
		{	if(msg[p]=='\"')  literal=!literal;
			p++;
		}
		return p;
	}


	// -----------------------------------------------------------------
	// Bootstrap
	// -----------------------------------------------------------------
	/*public static void main(String[]  args)
	{	String a[] =
		{	"\u001b[#000000m<font face=\"Verdana\" size=\"10\">No, and it's not that common anyway now, Fats.",
			"\u001b[31m<font face=\"Verdana\" size=\"13\">can any1 help me?",
			"\u001b[31mRDavi, I got it, hold on.",
			"<FADE #00002b,#4848ff,#4848ff,#00002b,#4848ff,#4848ff><font face=\"Comic Sans MS\" size=\"16\">any programmerz who make yahoo programz:-?</FADE>",
			"<ALT #0000ff,#00ff00,#ff0000><font face=\"Comic Sans MS\" size=\"16\">any programmerz who make yahoo programz:-?</ALT>",
			"<fade #000000,#0000ff,#00ff00,#ff0000,#00ffff,#000000><font face=\"Comic Sans MS\" size=\"16\">any programmerz who make :-( yahoo programz:-?</fade>",
			"<alt #0000ff,#00ff00,#ff0000><font face=\"Comic Sans MS\" size=\"16\">any programmerz :-) who make yahoo programz:-?</alt>",
			"\u001b[30m<font face=\"times\" size=\"14\">\u001b[1m\u001b[2manyone here using gyach enhanced?",
			"can you make such a monster?",
			"<font face=\"Arial\" size=\"10\">\u001b[#000000m<FADE #000000,#FF0000,#000000>whassup mah fellow programma's?!</FADE><font YmLite 41>",
			"<font face=\"Symbol\" size=\"12\">\u001b[#a0a0ffm\u001b[1m\u001b[4m$\u001b[x1m\u001b[x4m</font></font><font face=\"Arial\" size=\"10\"> \u001b[#000000m.9 times 10 <> 9.9</font><font YHLT ~></font>",
			"Happy :-) :)  Sad :(  Wink ;)  Grin :D  Eyes ;;) Confused :/  Love :X  Blush :\"> Tongue :p Kiss :*",
			"Shock :O  Anger X-(  Smug :>  Cool B)  Worried :s  Devil >:)  Cry :((  Laugh :))  Straight :|  Eyebrow /:)",
			"Angel O:)  Nerd :-B  Hand =;  Sleep I-)  Rolling 8-|  Sick :-&  Shhh :-$  Not talk [-(  Clown :o)  Silly 8-}",
			"Tired (:|  Drool =P~  Think :-?  D'oh #-o  Applause =D>",
			"normal<red>red<green>green</red>normal",
			"<fade #007eb5,#006ea5,#005e95,#004e85,#003e75,#002e65,#001e55,#000e45,#000000,#000000,#001e55,#002e65,#003e75,#004e85,#105e95,#206ea5><font face=\"Lucida Sans Unicode\" size=\"10\">ignored</font><font YHLT ~tBNHGIODKOHBDAAAA~></font></fade>",
			
			"<font face=\"Wingdings\" size=\"12\" tattoo>\u001b[#0000a0m\u001b[1m`\u001b[x1m</font></font> "+
				"<font INF ID:YHLT VER:285 LINE:4 SHARE:5,63.159.116.107:8001 SEX:F TM:06 AVA:YA\turtlewheelfire "+
				"PROT:YMSG12></font><fade #008500><font face=\"Courier\" size=\"10\">\u001b[1"+
				"mafk....gonna do my own java...</font></fade>"
		};

		JFrame frame = new JFrame();
		JTextPane tp = new JTextPane();
		tp.setEditable(false);
		frame.getContentPane().add(tp);  frame.setSize(400,450);
		frame.setVisible(true);

		Document doc = tp.getDocument();

		for(int i=0;i<a.length;i++)
		{	MessageDecoderSettings sets = new MessageDecoderSettings();
			MessageDecoder md = new MessageDecoder(sets);
			sets.setEmoticonsDecoded(true);
			sets.setRespectTextFade(true);
			sets.setRespectTextAlt(true);
			System.out.println("Trying:"+a[i]);
			MessageElement sc=md.decode(a[i]);
			System.out.print(sc.toTree());
			//System.out.println(sc.toHTML());
			//System.out.println(sc.toText());
			sc.appendToDocument(doc);
		}
	}*/
}
