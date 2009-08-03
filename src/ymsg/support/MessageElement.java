package ymsg.support;

import java.util.Enumeration;
import java.util.Vector;
import java.util.StringTokenizer;
import java.awt.Color;
import java.awt.Font;
import javax.swing.text.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;

// *********************************************************************
// A message element represents a low level segment of a decoded message.
// The sections form a hierarchy, with zero or more sections nested inside
// a given section.
//
// Thanks to John Morris, who provided examples of some useful upgrades
// and optimisations to the Swing Document code.
// *********************************************************************
public class MessageElement implements Emoticons
{	public final static int NULL = -2;				// No meaning
	public final static int ROOT = -1;				// Root section
	public final static int TEXT = 0;				// Text data
	public final static int BOLD = 1;				// Bold container
	public final static int ITALIC = 2;				// Italic container
	public final static int COLOUR_INDEX = 3;		// Colour index 0-9 container
	public final static int UNDERLINE = 4;			// Underline container
	public final static int FONT = 5;				// Font container
	public final static int FADE = 6;				// Fade container
	public final static int ALT = 7;				// Alt container
	public final static int COLOUR_ABS = 8;			// Colour absolute #rrggbb container
	public final static int COLOUR_NAME = 9;		// Named colour <red> <blue> etc.

	protected int type;								// Type of section (see above)
	protected Vector children;						// Contained sections

	protected int fontSize;							// Attributes
	protected String fontFace,text;					// Attributes
	protected Color[] transition;					// Fade/alt colours
	protected Color colour;

	private MessageDecoderSettings settings;

	protected static final String[] COLOUR_INDEXES =
	{	"black",	"blue",		"cyan",		"pink",		"green",
		"gray",		"purple",	"orange",	"red",		"brown",
		"yellow"
	};

	protected static final Color[] COLOUR_OBJECTS =
	{	Color.black,	Color.blue,		Color.cyan,		Color.pink,		Color.green,
		Color.gray,		Color.magenta,	Color.orange,	Color.red,		Color.lightGray,	// FIX: ltGray
		Color.yellow
	};

	private final static String TAB="  ";

	static Icon[] emoticonCache;

	static StyleContext styleContext;				// Used by Document decoders

	// -----------------------------------------------------------------
	// CONSTRUCTORS
	// -----------------------------------------------------------------
	protected MessageElement(MessageDecoderSettings set,int t)
	{	settings=set;  type=t;  children = new Vector();
	}

	protected MessageElement(MessageDecoderSettings set,int t,String body)
	{	this(set,t);
		switch(t)
		{	case TEXT :
				text=body;
				break;
			case FONT :
				fontFace = _attr(body,"face");
				String s  = _attr(body,"size");
				if(s!=null)
					try { fontSize=Integer.parseInt(s); } catch(NumberFormatException e) {}
				if(fontFace==null)  type=NULL;
				// -----Modify if conflicts with settings
				if(settings!=null)
				{	if(settings.overMaxFontSize>=0 && fontSize>settings.overMaxFontSize)
						fontSize=settings.overMaxFontSize;
					if(settings.overMinFontSize>=0 && fontSize<settings.overMinFontSize)
						fontSize=settings.overMinFontSize;
					if(settings.overFontFace!=null)
						fontFace=settings.overFontFace;
				}
				break;
			case FADE :
			case ALT :
				StringTokenizer st = new StringTokenizer(body,",");
				transition = new Color[st.countTokens()];
				int i=0;  
				while(st.countTokens()>0)
				{	String a = st.nextToken();
					if(a.startsWith("#"))  a=a.substring(1);
					//System.out.println(a);
					try
					{	transition[i++] = new Color(Integer.parseInt(a,16));
					}catch(NumberFormatException e) { transition[i-1] = Color.black; }
				}
				break;
			case COLOUR_INDEX :
				colour=COLOUR_OBJECTS[body.charAt(0)-'0'];
				// -----Modify if conflicts with settings
				if(settings!=null && settings.overFg!=null)
					colour=settings.overFg;
				break;
			case COLOUR_ABS :
				colour = new Color(Integer.parseInt(body,16));
				// -----Modify if conflicts with settings
				if(settings!=null && settings.overFg!=null)  
					colour=settings.overFg;
				break;
		}
	}
	
	protected MessageElement(MessageDecoderSettings def,int t,int num)
	{	this(def,t);
		switch(t)
		{	case COLOUR_NAME :
				colour=COLOUR_OBJECTS[num];
				// -----Modify if conflicts with settings
				if(settings!=null && settings.overFg!=null)
					colour=settings.overFg;
				break;
		}
	}

	// -----------------------------------------------------------------
	// STATIC CONSTRUCTORS
	// -----------------------------------------------------------------
	static
	{	emoticonCache = new Icon[Emoticons.EMOTICONS.length];
	}

	// -----------------------------------------------------------------
	// Utility methods
	// -----------------------------------------------------------------
	private String _attr(String haystack,String at)
	{	at=at+"=\"";
		String lc = haystack.toLowerCase();

		int idx = lc.indexOf(at);
		if(idx>=0)
		{	haystack=haystack.substring(idx+at.length());
			idx = haystack.indexOf("\"");
			if(idx>=0)  haystack=haystack.substring(0,idx);
			return haystack;
		}
		else
		{	return null;
		}
	}

	static int whichColourName(String n)
	{	for(int i=0;i<COLOUR_INDEXES.length;i++)
		{	if(n.equals(COLOUR_INDEXES[i]))  return i;
		}
		return -1;
	}
	
	boolean colourEquals(int i)
	{	return (colour==COLOUR_OBJECTS[i]);
	}
	
	int childTextSize()
	{	int l=0;
		for(int i=0;i<children.size();i++)
		{	MessageElement e=(MessageElement)children.elementAt(i);
			if(e.type==TEXT)  l+=e.text.length();
				else  l+=e.childTextSize();
		}
		return l;
	}

	// -----------------------------------------------------------------
	// Add a child to this section
	// -----------------------------------------------------------------
	void addChild(MessageElement s)
	{	children.addElement(s);
	}


	// -----------------------------------------------------------------
	// Translate to HTML
	// -----------------------------------------------------------------
	public String toHTML()
	{	StringBuffer sb = new StringBuffer();
		toHTML(sb);
		return sb.toString();
	}

	private void toHTML(StringBuffer sb)
	{	switch(type)
		{	case NULL :			sb.append("<span>");  break;
			case TEXT :			sb.append(text);  break;
			case BOLD :			sb.append("<b>");  break;
			case ITALIC :		sb.append("<i>");  break;
			case COLOUR_INDEX :
			case COLOUR_ABS :	
//            case COLOUR_NAME:	sb.append("<font color=\"#").append(""+colour.getRGB()).append("\">");  break;                
			case COLOUR_NAME:	sb.append("<font color=\"#").append(""+Integer.toHexString(colour.getRGB()).substring(2)).append("\">");  break;
			case UNDERLINE :	sb.append("<u>");  break;
			case FONT :			sb.append("<font face=\""+fontFace+"\" size=\""+fontSize+"\">");  break;
			case FADE :			sb.append("<span>");  break;
			case ALT :			sb.append("<span>");  break;
		}
		for(int i=0;i<children.size();i++)
		{	MessageElement sc = (MessageElement)children.elementAt(i);
			sc.toHTML(sb);
		}
		switch(type)
		{	case NULL :			sb.append("</span>");  break;
			case BOLD :			sb.append("</b>");  break;
			case ITALIC :		sb.append("</i>");  break;
			case COLOUR_INDEX :
			case COLOUR_ABS :	
			case COLOUR_NAME:	sb.append("</font>");  break;
			case UNDERLINE :	sb.append("</u>");  break;
			case FONT :			sb.append("</font>");  break;
			case FADE :			sb.append("</span>");  break;
			case ALT :			sb.append("</span>");  break;
		}
	}

	// -----------------------------------------------------------------
	// Translate to HTML
	// -----------------------------------------------------------------
	public String toText()
	{	StringBuffer sb = new StringBuffer();
		toText(sb);
		return sb.toString();
	}

	private void toText(StringBuffer sb)
	{	if(type==TEXT)  sb.append(text);
		for(int i=0;i<children.size();i++)
		{	MessageElement sc = (MessageElement)children.elementAt(i);
			sc.toText(sb);
		}
	}

	// -----------------------------------------------------------------
	// Translate to Java Swing elements and add to existing document
	// -----------------------------------------------------------------
	public void appendToDocument(Document doc)
	{	try
		{	SimpleAttributeSet sas = new SimpleAttributeSet();
			// -----If settings are available, set them as the root attrs
			if(settings!=null)
			{	if(settings.defFontFace!=null)  StyleConstants.setFontFamily(sas,settings.defFontFace);
				if(settings.defFontSize>=0)  StyleConstants.setFontSize(sas,settings.defFontSize);
			}
			// -----Local ref (not a thread issue: copying one static member to another!)
			if(styleContext==null)  styleContext = StyleContext.getDefaultStyleContext();
			// -----Append styled text to doc
			appendToDocument(doc,sas,new LocalSettings());
			doc.insertString(doc.getLength(),"\n",sas);
		}catch(BadLocationException e) {}
	}

	private void appendToDocument(Document doc,AttributeSet at,LocalSettings locals)
	throws BadLocationException
	{	AttributeSet attrs = new SimpleAttributeSet(at);
		// -----Open of container
		switch(type)
		{	case TEXT :
				if(settings!=null && !settings.emoticonsOn)  
					_textWithoutIcons(doc,text,attrs,locals);
				else
					_textWithIcons(doc,text,attrs,locals);
				break;
			case BOLD :
				attrs = styleContext.addAttribute(attrs,StyleConstants.Bold,Boolean.TRUE);
				break;
			case ITALIC :
				attrs = styleContext.addAttribute(attrs,StyleConstants.Italic,Boolean.TRUE);
				break;
			case COLOUR_INDEX :
			case COLOUR_ABS :	
			case COLOUR_NAME:
				attrs = styleContext.addAttribute(attrs,StyleConstants.Foreground,colour);
				break;
			case UNDERLINE :
				attrs = styleContext.addAttribute(attrs,StyleConstants.Underline,Boolean.TRUE);
				break;
			case FONT :	
				attrs = styleContext.addAttribute(attrs,StyleConstants.FontFamily,fontFace);
				attrs = styleContext.addAttribute(attrs,StyleConstants.FontSize,new Integer(fontSize));
				break;
			case FADE :			
				if(settings!=null && settings.respectFade)
					locals.colourTransition = new FadeEnumeration(transition,childTextSize());
				break;
			case ALT :
				if(settings!=null && settings.respectAlt)
					locals.colourTransition = new AltEnumeration(transition);
				break;
		}
		// -----Container chilren
		for(int i=0;i<children.size();i++)
		{	MessageElement sc = (MessageElement)children.elementAt(i);
			sc.appendToDocument(doc,attrs,locals);
		}
		// -----Close container
		switch(type)
		{	case BOLD :
				attrs = styleContext.addAttribute(attrs,StyleConstants.Bold,Boolean.FALSE);
				break;
			case ITALIC :
				attrs = styleContext.addAttribute(attrs,StyleConstants.Italic,Boolean.FALSE);
				break;
			case COLOUR_INDEX :
			case COLOUR_ABS :	
			case COLOUR_NAME :
				attrs = styleContext.addAttribute(attrs,StyleConstants.Foreground,
					styleContext.getForeground(at));
				break;
			case UNDERLINE : 
				attrs = styleContext.addAttribute(attrs,StyleConstants.Underline,Boolean.FALSE);
				break;
			case FONT :
				Font f = styleContext.getFont(at);
				attrs = styleContext.addAttribute(attrs,StyleConstants.FontFamily,f.getFamily());
				attrs = styleContext.addAttribute(attrs,StyleConstants.FontSize,new Integer(f.getSize()));
				break;
			case FADE :			
			case ALT :
				locals.colourTransition=null; 
				break;
		}
	}

	private void _textWithIcons(Document doc,String text,AttributeSet at,LocalSettings locals)
	throws BadLocationException
	{	int ii,xx,yy;
		// -----Loop until no text left, or no smiley text left
		do
		{	// -----Find the leftmost smiley text; ii is the char index of
			// -----of the start of the smiley, xx and yy are the position
			// -----in EMOTICONS arrays.
			ii=text.length();  xx=-1;  yy=-1;
			for(int y=0;y<EMOTICONS.length;y++)
			{	for(int x=0;x<EMOTICONS[y].length;x++)
				{	int i=text.indexOf(EMOTICONS[y][x]);
					// -----Simley found?
					if(i>=0)
					{	// -----Before (left from) current ... or ...
						// -----same pos as current but bigger smiley
						if
						(	i<ii  ||
							(xx>=0 && i==ii && EMOTICONS[y][x].length()>EMOTICONS[yy][xx].length())
						)
						{	ii=i;  yy=y;  xx=x;
						}
					}
				}
			}
			// -----Found?
			if(xx>=0)
			{	// -----Add leading text
				_textWithoutIcons(doc,text.substring(0,ii),at,locals);
				// -----Add icon
				if(emoticonCache[yy]==null)
				{	if(settings!=null)  emoticonCache[yy]=settings.getEmoticon(yy);
						else  emoticonCache[yy] = new ImageIcon( getClass().getResource(yy+".gif") );
				}
				SimpleAttributeSet at2 = new SimpleAttributeSet();
				StyleConstants.setIcon(at2,emoticonCache[yy]);
				doc.insertString(doc.getLength(),EMOTICONS[yy][xx],at2);
				// -----Step over colour transitions for each char in smiley
				if(locals.colourTransition!=null)
				{	for(int i=0;i<EMOTICONS[yy][xx].length();i++)
						locals.colourTransition.nextElement();
				}
				// -----Update 'text' with trailing text
				text=text.substring(ii+EMOTICONS[yy][xx].length());
			}
		}while(text.length()>0 && xx>=0);
		// -----Okay, so there's no more smileys left, but any text left?
		if(text.length()>0)
			_textWithoutIcons(doc,text,at,locals);
	}
	
	private void _textWithoutIcons(Document doc,String text,AttributeSet at,LocalSettings locals)
	throws BadLocationException
	{	if(locals.colourTransition!=null)
		{	for(int i=0;i<text.length();i++)
			{	at = styleContext.addAttribute
				(	at,
					StyleConstants.Foreground,
					(Color)locals.colourTransition.nextElement()
				);
				doc.insertString(doc.getLength(),""+text.charAt(i),at);
			}
		}
		else
		{	doc.insertString(doc.getLength(),text,at);
		}
	}

	// *****************************************************************
	// Handy class to encapsulate data which must be passed around while
	// the element tree is being walked.  At the moment only one thing to
	// pass, but might expand as code grows.
	// *****************************************************************
	class LocalSettings
	{	Enumeration colourTransition;
	}

	// -----------------------------------------------------------------
	// Debug
	// -----------------------------------------------------------------
	String toTree() { return toTree(TAB); }

	String toTree(String tab)
	{	String s=toString()+"\n";
		for(int i=0;i<children.size();i++)
		{	MessageElement sc = (MessageElement)children.elementAt(i);
			s=s+tab+sc.toTree(tab+TAB);
		}
		return s;
	}

	public String toString()
	{	switch(type)
		{	case NULL :			return "[Null]";
			case ROOT :			return "[Root]";
			case TEXT :			return "Text:"+text;
			case BOLD :			return "<b>";
			case ITALIC :		return "<i>";
			case COLOUR_INDEX :	return "<col @"+colour+">";
			case UNDERLINE :	return "<u>";
			case FONT :			return "<font "+fontFace+":"+fontSize+">";
			case FADE :			return "<fade "+transition+">";
			case ALT :			return "<alt "+transition+">";
			case COLOUR_ABS :	return "<col #"+colour+">";
			case COLOUR_NAME :	return "<col name #"+colour+">";
			default :			return "?";
		}
	}
}
