package ymsg.support;

import java.awt.Color;
import javax.swing.Icon;
import javax.swing.ImageIcon;

// *********************************************************************
// This class acts in tandem with the MessageDecoder, to give control
// over how the translated output is styled.  Rather than set these
// preferences inside the decoder object itself, this independent object
// allows for a single set of preferences to be shared between multiple
// decoders, and immediately updated across all.
//
// The font/text settings fall into two categories.  'Default' determines
// the initial state of the decoder, while 'overridden' determines the
// settings to use to override those specified in each message.
//
// For example, setting a default font will effect the way text is rendered
// for messages which do not set their fonts explicity.  (Not setting this
// will mean that the decoder will use the default style provided by Swing.)
// Setting an overriding font will effect the way text is rendered for
// messages which carry their own font details - your overriding font will
// always be used in preference to the message's choosen font.  (Not setting 
// this will mean that the message fonts will be respected.)
//
// To force all font faces to be Courier, for example, one would set both
// the default and the overriding font faces.  This will ensure that all
// messages without font data will be rendered using Courier (thanks to
// 'default') and all messages *with* font data  will also be rendered 
// using Courier (thanks to 'override').
//
// Setting a field to null or -1 will have the effect of disabling that
// style attribute.  It is therefore quite possible to override message 
// font faces, while not overriding their font size.  Or to set a maximum
// font size for messages, but no minimum.
// *********************************************************************
public class MessageDecoderSettings
{	boolean emoticonsOn=false;
	EmoticonLoader emoticonLoader=null;

	String defFontFace=null;
	int defFontSize=-1;
	Color defFg=null;

	String overFontFace=null;
	int overMaxFontSize=-1,overMinFontSize=-1;
	Color overFg=null;
	
	boolean respectFade=false;
	boolean respectAlt=false;

	// -----------------------------------------------------------------
	// Returns either the bespoke icon from loader, or default icon
	// from this package
	// -----------------------------------------------------------------
	Icon getEmoticon(int i)
	{	if(emoticonLoader!=null)
		{	Icon ic = emoticonLoader.loadEmoticon(i);
			if(ic!=null)  return ic;
		}
		return new ImageIcon( getClass().getResource("images/"+i+".gif") );
	}
	
	
	// -----------------------------------------------------------------
	// Setters
	// -----------------------------------------------------------------
	public void setEmoticonsDecoded(boolean b) { emoticonsOn=b; }
	public void setEmoticonLoader(EmoticonLoader l) { emoticonLoader=l; }

	public void setDefaultFontFace(String s) { defFontFace=s; }
	public void setDefaultFontSize(int sz) { defFontSize=sz; }
	public void setDefaultForeground(Color col) { defFg=col; }

	public void setOverrideFontFace(String s) { overFontFace=s; }
	public void setOverrideMaxFontSize(int sz) { overMaxFontSize=sz; }
	public void setOverrideMinFontSize(int sz) { overMinFontSize=sz; }
	public void setOverrideForeground(Color col) { overFg=col; }
	
	public void setDefaultFont(String face,int sz,Color fgCol)
	{	defFontFace=face;  defFontSize=sz;  defFg=fgCol;
	}
	public void setOverrideFont(String face,int min,int max,Color fgCol)
	{	overFontFace=face;  overMinFontSize=min;  overMaxFontSize=max;
		overFg=fgCol;
	}
	
	public void setRespectTextFade(boolean b) { respectFade=b; }
	public void setRespectTextAlt(boolean b) { respectAlt=b; }
	
	// -----------------------------------------------------------------
	// Getters
	// -----------------------------------------------------------------
	public boolean getEmoticonsDecoded() { return emoticonsOn; }
	public EmoticonLoader getEmoticonLoader() { return emoticonLoader; }

	public String getDefaultFontFace() { return defFontFace; }
	public int getDefaultFontSize() { return defFontSize; }
	public Color getDefaultForeground() { return defFg; }

	public String getOverrideFontFace() { return overFontFace; }
	public int getOverrideMaxFontSize() { return overMaxFontSize; }
	public int getOverrideMinFontSize() { return overMinFontSize; }
	public Color getOverrideForeground() { return overFg; }
	
	public boolean getRespectTextFade() { return respectFade; }
	public boolean getRespectTextAlt() { return respectAlt; }
}
