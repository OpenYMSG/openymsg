package ymsg.network.event;

public class SessionAdapter implements SessionListener
{
    public void fileTransferReceived(SessionFileTransferEvent ev){}
	public void connectionClosed(SessionEvent ev){}
	public void listReceived(SessionEvent ev){}

	public void messageReceived(SessionEvent ev){}
	public void buzzReceived(SessionEvent ev) {}
	public void offlineMessageReceived(SessionEvent ev){}
	public void errorPacketReceived(SessionErrorEvent ev){}
	public void inputExceptionThrown(SessionExceptionEvent ev){}

	public void newMailReceived(SessionNewMailEvent ev){}
	public void notifyReceived(SessionNotifyEvent ev){}

	public void contactRequestReceived(SessionEvent ev){}
	public void contactRejectionReceived(SessionEvent ev){}

	public void conferenceInviteReceived(SessionConferenceEvent ev){}
	public void conferenceInviteDeclinedReceived(SessionConferenceEvent ev){}
	public void conferenceLogonReceived(SessionConferenceEvent ev){}
	public void conferenceLogoffReceived(SessionConferenceEvent ev){}
	public void conferenceMessageReceived(SessionConferenceEvent ev){}

	public void friendsUpdateReceived(SessionFriendEvent ev){}
	public void friendAddedReceived(SessionFriendEvent ev){}
	public void friendRemovedReceived(SessionFriendEvent ev){}

	public void chatLogonReceived(SessionChatEvent ev){}
	public void chatLogoffReceived(SessionChatEvent ev){}
	public void chatMessageReceived(SessionChatEvent ev){}
	public void chatUserUpdateReceived(SessionChatEvent ev){}
	public void chatConnectionClosed(SessionEvent ev){}
    
        public void pictureReceived(SessionPictureEvent ev){}
        public void authorizationReceived(SessionAuthorizationEvent ev){}
}
