package ymsg.network;

import java.util.Hashtable;

// *********************************************************************
// A cache of YahooUser objects.  The same user may be encountered through
// different means - this class attempts to ensure a canonical object is
// used to represent a given user.  (Note: identities of the same user will
// have different objects, as they 'appear' to be different users.)
//
// 'Encounters' may be as a result of a user being on your friends list, 
// because someone sent you a message/invite/request or because they were in 
// a chatroom the user visited.
// *********************************************************************
class UserStore
{	private Hashtable users;			// Key=yahoo_id, value=YahooUser

	UserStore()
	{	users = new Hashtable();
	}

	// Query hash for specific user
	boolean contains(String id) { return users.containsKey(id); }
	YahooUser get(String id) { return (YahooUser)users.get(id); }

	// Get a user object, or create if not known already
	YahooUser getOrCreate(String id)
	{	if(!contains(id)) users.put(id,new YahooUser(id));
		return get(id);
	}
	// Get and update a user object, or create if not known already 
	YahooUser getOrCreate(String id,String st,String ch,String pg)
	{	if(!contains(id))  users.put(id,new YahooUser(id,st,ch,pg));
			else get(id).update(id,st,ch,pg);
		return get(id);
	}

	// Used for things like conference invites.  This method takes a array
	// of ids and returns an array of associated YahooUser objects.
	YahooUser[] toUserArray(String[] arr)
	{	YahooUser[] dest = new YahooUser[arr.length];
		for(int i=0;i<arr.length;i++)  dest[i]=getOrCreate(arr[i]);
		return dest;
	}

	Hashtable getUsers() { return users; }
}
