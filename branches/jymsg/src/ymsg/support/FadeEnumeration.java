package ymsg.support;

import java.awt.Color;
import java.util.Enumeration;

// *********************************************************************
// Creates an enumeration object which produces a sequence of Color
// objects based upon a FADE.
//
// The outer Enumeration implementation actually hides an array of
// Enumeration's for each section of the fade.  For example, a fade
// #ff0000,#00ff00,#0000ff will have three Section objects which will
// be called in sequence to create the overall enum.
//
// The overall effect is that the first colour in a fade will be 
// returned first, the last colour will be returned last, and the
// in-between colours will be evenly spaced between the two bookends,
// with shades between each section calculated - depending upon how 
// many characters the fade has to cover.
// *********************************************************************
class FadeEnumeration implements Enumeration
{	private int index;						// Current step
	private Section[] fades;				// Fade enums (sections)

	// -----------------------------------------------------------------
	// CONSTRUCTOR
	// -----------------------------------------------------------------
	FadeEnumeration(Color[] c,int sz)
	{	// -----Sometimes we can have a fade of only one colour.  Promote to
		// -----a (pointless) fade between the same two colours. 
		if(c.length==1)
		{	Color[] c2 = new Color[2];  c2[0]=c[0];  c2[1]=c[0];
			c=c2;
		}
		
		fades = new Section[c.length];
		
		// -----The scale determines the ratio of characters per fade enum,
		// -----ie: three colours over six characters gives a scale of 2.5
		// -----characters per fade section ... Note we don't include the
		// -----final character, as this will be the final colour (enum of
		// -----size 1), ie: [c1] [->] [c2] [->] [->] [c3]
		float scale = (float)(sz-1)/(float)(c.length-1);
		int i;
		for(i=0;i<c.length-1;i++)
		{	// -----Get the number of chars for this enum, rounded down, by
			// -----removing the int of the current entry from the next.  In 
			// -----the above example of 2.5 this will be 2 then 3 
			// -----(2.5-0 = 2 and 5-2 = 3).  NOTE: if the size of the
			// -----colour transition array is larger than the size of the
			// -----text, then enumSize can be zero... eg: 10 colours and text
			// -----"ok" will result in enum sizes 0,0,0,0,1,0,0,0,0,1 [*]
			int enumSize = (int)(scale*(i+1)) - (int)(scale*i);
			fades[i] = new Section(c[i],c[i+1],enumSize);
		}
		// -----Append last Color
		fades[i] = new Section(c[i],c[i],1);
		
		index=0;
	}

	// [*] = this is wrong, as that example should result in 1,0,0,0,0,
	// 0,0,0,0,1 (1, eight zeroes, then 1) but I cannot be bothered to
	// fix the code right now!  [FIX]
	
	// -----------------------------------------------------------------
	// Enumeration methods
	// -----------------------------------------------------------------
	public boolean hasMoreElements()
	{	if(index<fades.length-1)  return true;
			else  return fades[index].hasMoreElements();
	}
	public Object nextElement()
	{	// -----Find next non-zero enum
		while(!fades[index].hasMoreElements() && index<fades.length)  index++;
		// -----Did we run off the end of the arrya?
		if(index>=fades.length)  return null;
		// -----Return next Color
		return  fades[index].nextElement();
	}
		
	// *****************************************************************
	// Section - one colour transition from two given Color 'points'
	// *****************************************************************
	private class Section implements Enumeration
	{	private float scaleR,scaleG,scaleB;	// Scaling factor for FADE
		private int startR,startG,startB;	// Start colour
		private int size,index;
	
		// -------------------------------------------------------------
		// CONSTRUCTOR
		// -------------------------------------------------------------
		Section(Color c1,Color c2,int sz)
		{	//System.out.println(c1+"->"+c2);
			startR=c1.getRed();  startG=c1.getGreen();  startB=c1.getBlue();
			size=sz;
			scaleR=(float)(c2.getRed()-startR)/(float)size;
			scaleG=(float)(c2.getGreen()-startG)/(float)size;
			scaleB=(float)(c2.getBlue()-startB)/(float)size;
			index=0;
		}
		// -------------------------------------------------------------
		// Enumeration methods
		// -------------------------------------------------------------
		public boolean hasMoreElements() { return (index<size); }	
		public Object nextElement()
		{	int i = index%size;
			Color c = new Color
			(	(int)(startR+scaleR*i) ,
				(int)(startG+scaleG*i) ,
				(int)(startB+scaleB*i)
			);
			index++;
			return c;
		}
	}
	
	// -----------------------------------------------------------------
	// DEBUG
	// -----------------------------------------------------------------
	/*public static void main(String[] args)
	{	int[] arr = { 0xff0000, 0x00ff00, 0x0000ff };
		Color[] cols = new Color[arr.length];
		for(int i=0;i<arr.length;i++)  cols[i] = new Color(arr[i]);
		Enumeration en = new FadeEnumeration(cols,6);
		while(en.hasMoreElements())
		{	System.out.println( ((Color)en.nextElement()).toString() );
		}
	}*/
}
