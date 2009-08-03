package ymsg.support;

import java.awt.Color;
import java.util.Enumeration;

// *********************************************************************
// Creates an enumeration object which produces a sequence of Color
// objects based upon a ALT.
//
// hasMoreElements() will always return true.
// *********************************************************************
class AltEnumeration implements Enumeration
{	private int index;						// Current step
	private Color[] colours;				// Colours in alt transition
	
	
	// -----------------------------------------------------------------
	// CONSTRUCTOR
	// -----------------------------------------------------------------
	AltEnumeration(Color[] c)
	{	colours=c;  index=0;
	}
	
	// -----------------------------------------------------------------
	// Enumeration methods
	// -----------------------------------------------------------------
	public boolean hasMoreElements()
	{	return true;
	}
	public Object nextElement()
	{	index++;
		return colours[(index-1)%colours.length];
	}
}
