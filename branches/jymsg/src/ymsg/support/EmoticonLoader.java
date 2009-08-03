package ymsg.support;

import javax.swing.Icon;

// *********************************************************************
// A concrete instance of this class can be used to supply icons to the
// message decoder code, for use when generating styled output which
// supports smileys.
// *********************************************************************
public interface EmoticonLoader
{	// -----------------------------------------------------------------
	// 'icon' will be 1-35.  Return null if the jYMSG's in-build icon
	// is to be used.
	// -----------------------------------------------------------------
	public Icon loadEmoticon(int icon);
}
