package org.openymsg.roster;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.addressBook.YahooAddressBookEntry;
import org.openymsg.network.*;
import org.openymsg.network.event.SessionEvent;
import org.openymsg.network.event.SessionFriendAcceptedEvent;
import org.openymsg.network.event.SessionFriendEvent;
import org.openymsg.network.event.SessionFriendRejectedEvent;
import org.openymsg.network.event.SessionListEvent;
import org.openymsg.network.event.SessionListener;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Roster object is a representation of the contact list of a particular user. The Roster is a set of all users to
 * which the owner of the Roster has subscribed.
 * <p>
 * Listeners can be registered to a Roster instance. After the listener has been registered, it will receive events that
 * reflect changes to the Roster.
 * <p>
 * The Roster class implements the Set interface, as it represents a unique set of Yahoo Users. To avoid accidental mass
 * subscription or unsubscription, most bulk operations (addAll, removeAll) are unsupported in this implementation.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public class Roster implements Set<YahooUser>, SessionListener {

    private static final Log log = LogFactory.getLog(Roster.class);

    /**
     * The collection of RosterListener instances that will be notified of new changes to the roster.
     */
    private final Collection<RosterListener> listeners = new LinkedList<RosterListener>();

    /**
     * A collection of all YahooUsers on the roster. The YahooUsers are mapped by the value returned by {@link
     * YahooUser.getId()}
     */
    private final Map<String, YahooUser> usersById = new ConcurrentHashMap<String, YahooUser>();

    /**
     * A collection of all YahooUsers from address book. The YahooUsers are mapped by the value returned by {@link
     * YahooUser.getId()}
     */
    private final Map<String, YahooAddressBookEntry> addressBookUsersById = new Hashtable<String, YahooAddressBookEntry>();

    /**
     * Interface used to relay changes to the roster to the Yahoo network.
     */
    private final FriendManager friendManager;

    /**
     * Creates a new roster object, that makes use of the provided manager to transmit changes made to the roster to the
     * Yahoo network.
     * 
     * @param manager
     *            The object used to relay changes made to this Roster to the Yahoo network.
     */
    public Roster(final FriendManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("Argument 'manager' cannot be null");
        }

        friendManager = manager;
    }

    // Event Listening management methods

    /**
     * Adds a listener that will be notified of any roster changes. This operation is thread safe.
     * 
     * @param listener
     *            The new listener that gets notified of any roster changes.
     */
    public void addRosterListener(final RosterListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Argument 'listener' cannot be null.");
        }

        synchronized (listeners) {
            listeners.add(listener);
        }

        log.debug("Added new RosterListener.");
    }

    /**
     * Removes the listener from the set of listeners that will be notified of changes to this roster. This operation is
     * thread safe.
     * 
     * @param listener
     *            The listener that should be removed.
     */
    public void removeRosterListener(final RosterListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Argument 'listener' cannot be null.");
        }

        synchronized (listeners) {
            listeners.remove(listener);
        }

        log.debug("Removed RosterListener.");
    }

    /**
     * Utility method to broadcast an event to all registered Listeners. This operation is thread safe.
     * 
     * @param event
     *            The event that is going to be broadcasted to all registered event listeners.
     */
    void broadcastEvent(final RosterEvent event) {
        final RosterListener[] copies;

        synchronized (listeners) {
            copies = listeners.toArray(new RosterListener[listeners.size()]);
        }

        for (final RosterListener rosterListener : copies) {
            rosterListener.rosterChanged(event);
        }

        log.trace("Broadcasted RosterEvent to " + copies.length + " listeners: " + event);
    }

    // Modification methods

    /**
     * Adds the specified user to this roster if it is not already present. If this roster already contains the
     * specified user, the call leaves this set unchanged and returns <tt>false</tt>.
     * 
     * @param user
     *            The YahooUser to be added to this roster.
     * @return <tt>true</tt> if this roster did not already contain the specified element.
     * 
     * @throws NullPointerException
     *             if the specified user is null.
     * @throws IllegalArgumentException
     *             if some aspect of the specified user prevents it from being added to this set.
     */
    public boolean add(YahooUser user) {
        if (user == null) {
            throw new NullPointerException();
        }

        final String id = user.getId();
        if (id == null || id.length() == 0) {
            throw new IllegalArgumentException("The user to be added must have a valid, non-empty String ID field set.");
        }

        if (user.getGroupIds() == null || user.getGroupIds().isEmpty()) {
            throw new IllegalArgumentException("The user to be added must have at least on groupId.");
        }

        log.trace("Adding new user: " + user);

        YahooProtocol yahooProtocol = YahooProtocol.YAHOO;
        if (user.getProtocol() == null) {
            log.debug("default protocol used for: " + id);
        }
        else {
            yahooProtocol = user.getProtocol();
        }

        // TODO : input validation on userId/groupId?

        for (final String groupId : user.getGroupIds()) {
            try {
                log.trace("Adding new user: " + user + ", group: " + groupId);
                friendManager.sendNewFriendRequest(user.getId(), groupId, yahooProtocol);
            }
            catch (IOException ex) {
                log.error("Failed adding user: " + user, ex);
                throw new RuntimeException("Unexpected exception.", ex);
            }
        }
        return true;
        // return syncedAdd(user); //add user when getting the ack
    }

    /**
     * Utility method that adds the new user to the backend in a thread-safe manner. This method also triggers an Event
     * to be sent out to the applicable listeners.
     * <p>
     * Note that users of the Roster class should not use this method directly, but one of the add() methods instead.
     * 
     * @param user
     *            The YahooUser to be added to this roster.
     * @return <tt>true</tt> if this roster did not already contain the specified element.
     * @throws NullPointerException
     *             if the specified user is null.
     * @throws IllegalArgumentException
     *             if some aspect of the specified user prevents it from being added to this set.
     */
    private boolean syncedAdd(YahooUser user) {
        if (user == null) {
            throw new NullPointerException();
        }

        final String id = user.getId();
        if (id == null || id.length() == 0) {
            throw new IllegalArgumentException("The user to be added must have a valid, non-empty String ID field set.");
        }

        log.trace("Adding new user: " + user);
        YahooAddressBookEntry addressBookEntry = this.addressBookUsersById.get(id);

        synchronized (usersById) {
            // if (usersById.containsKey(id)) {
            // log.debug("Roster already contained this userId "
            // + "(backend storage will not be updated): " + id);
            // return false;
            // }
            if (addressBookEntry != null) {
                user = this.createMergedUser(addressBookEntry, id, user);
            }
            usersById.put(id, user);
            log.trace("Added new user: " + user);
        }

        // notify listeners.
        broadcastEvent(new RosterEvent(this, user, RosterEventType.add));

        return true;
    }

    /**
     * Removes the specified user from this roster if it is present. Returns <tt>true</tt> if the roster contained the
     * specified user (or equivalently, if the roster changed as a result of the call). The roster will not contain the
     * specified user once the call returns.
     * 
     * @param userObject
     *            user object to be removed from this roster, if present.
     * @return <tt>true</tt> if the roster contained the specified user.
     * @throws ClassCastException
     *             if the type of the specified user object is not a YahooUser instance.
     * @throws NullPointerException
     *             if the argument is null.
     */
    public boolean remove(Object userObject) {
        if (userObject == null) {
            throw new NullPointerException();
        }

        if (!(userObject instanceof YahooUser)) {
            throw new ClassCastException("This method needs a YahooUser value.");
        }

        final YahooUser user = (YahooUser) userObject;

        if (!contains(user)) {
            log.trace("Cannot remove a user, because it's not on this roster: " + user);
            return false;
        }
        log.trace("Removing a user: " + user);

        for (final String groupId : user.getGroupIds()) {
            try {
                friendManager.removeFriendFromGroup(user.getId(), groupId);
            }
            catch (IOException ex) {
                throw new RuntimeException("Unexpected exception.", ex);
            }
        }
        return true;
        // return syncedRemove(user.getId()); // remover user when getting the ack
    }

    /**
     * Utility method that removes the user from the backend in a thread-safe manner. This method also triggers an Event
     * to be sent out to the applicable listeners.
     * <p>
     * Note that users of the Roster class should not use this method directly, but one of the remove() methods instead.
     * 
     * @param userId
     *            ID of the user to be removed from this roster, if present.
     * @return <tt>true</tt> if the roster contained the specified user.
     * @throws IllegalArgumentException
     *             if the argument is null or an empty String.
     */
    private boolean syncedRemove(final String userId) {
        if (userId == null || userId.length() == 0) {
            throw new IllegalArgumentException("Argument 'userId' cannot be null or an empty String.");
        }

        log.trace("Removing user by userId: " + userId);
        final YahooUser user;
        synchronized (usersById) {
            if (!usersById.containsKey(userId)) {
                log.debug("Roster does not contain this userId " + "(backend storage will not be updated): " + userId);
                return false;
            }
            user = usersById.remove(userId);
            log.trace("Removed user: " + user);
        }

        // notify listeners.
        broadcastEvent(new RosterEvent(this, user, RosterEventType.remove));

        return true;
    }

    /**
     * Utility method that updates the user from the backend in a thread-safe manner. This method also triggers an Event
     * to be sent out to the applicable listeners.
     * <p>
     * Note that users of the Roster class should not use this method directly.
     * 
     * @param userId
     *            ID of the user to be removed from this roster, if present.
     * @throws NullPointerException
     *             if the specified user object is null.
     * @throws IllegalArgumentException
     *             If the userId String is null or empty, or if user.getId() does not match the userId provided.
     * @throws IllegalStateException
     *             If the userId does not match a user that's currently on this roster.
     * 
     */
    private void syncedUpdate(final String userId, YahooUser user) {
        if (userId == null || userId.length() == 0) {
            throw new IllegalArgumentException("Argument 'userId' cannot be null or an empty String.");
        }

        if (user == null) {
            throw new NullPointerException();
        }

        if (!userId.equals(user.getId())) {
            throw new IllegalArgumentException(
                    "The user object that is updated must have the same userId as provided in the userId argument (updating a userID is illegal).");
        }

        YahooAddressBookEntry addressBookEntry = this.addressBookUsersById.get(userId);

        synchronized (usersById) {
            if (!usersById.containsKey(userId)) {
                throw new IllegalStateException("No user on roster with this id: " + userId);
            }
            if (user.getGroupIds().isEmpty()) {
                // this is an early user warning
                for (String groupId : usersById.get(userId).getGroupIds()) {
                    user.addGroupId(groupId);
                }
            }
            if (addressBookEntry != null) {
                user = this.createMergedUser(addressBookEntry, userId, user);
            }
            usersById.put(userId, user);
        }

        log.trace("Updated user identified by userId: " + userId);

        // notify listeners.
        broadcastEvent(new RosterEvent(this, user, RosterEventType.update));
    }

    // Query information methods

    /**
     * Checks if the provided object is a user on this roster.
     * 
     * @param user
     *            object whose presence on this roster is to be tested.
     * @return <tt>true</tt> if this set contains the specified element.
     * @throws ClassCastException
     *             if the type of the specified element is incompatible with this set (not a YahooUser).
     * @throws NullPointerException
     *             if the specified element is null.
     */
    public boolean contains(Object user) {
        if (user == null) {
            throw new NullPointerException();
        }

        if (!(user instanceof YahooUser)) {
            throw new ClassCastException("This method needs a YahooUser value.");
        }

        return usersById.containsKey(((YahooUser) user).getId());
    }

    /**
     * Checks if this roster contains a user that is identified by the provided ID.
     * 
     * @param userId
     *            ID of a user whose presence on this roster is to be tested.
     * @return <tt>true</tt> if this set contains the specified element.
     * @throws NullPointerException
     *             if the specified userId is null.
     * @throws IllegalArgumentException
     *             if the specified userId is an empty String.
     */
    public boolean containsUser(String userId) {
        if (userId == null) {
            throw new NullPointerException();
        }

        if (userId.length() == 0) {
            throw new IllegalArgumentException("Argument 'userId' cannot be an empty String.");
        }
        for (String idOfUser : this.usersById.keySet()) {
            if (idOfUser.toLowerCase().equals(userId.toLowerCase())) {
                if (!idOfUser.equals(userId)) {
                    log.debug("contains user with mixed case: " + idOfUser + " looking for: " + userId);
                }
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException();
        }

        for (final Object object : c) {
            if (!contains(object)) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#isEmpty()
     */
    public boolean isEmpty() {
        return usersById.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#size()
     */
    public int size() {
        return usersById.size();
    }

    /**
     * Returns an iterator over the YahooUsers in this Roster. The users are returned in no particular order. Removing
     * the users through the remove method is unsupported, and will cause a {@link UnsupportedOperationException} to be
     * thrown.
     * 
     * @return an iterator over the users on this roster.
     */
    public Iterator<YahooUser> iterator() {
        return new Iterator<YahooUser>() {
            private final Iterator<YahooUser> i = usersById.values().iterator();

            public boolean hasNext() {
                return i.hasNext();
            }

            public YahooUser next() {
                return i.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Returns the user specified by the provided ID, or null if no such user exists on the roster.
     * 
     * @param userId
     *            the ID of the user to return.
     * @return the User matched by the ID, or null if no such user exists on this roster.
     */
    public YahooUser getUser(final String userId) {
        if (userId == null || userId.length() == 0) {
            throw new IllegalArgumentException("Argument 'userId' cannot be null or an empty String.");
        }

        return usersById.get(userId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#toArray()
     */
    public Object[] toArray() {
        return usersById.values().toArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#toArray(T[])
     */
    public <T> T[] toArray(T[] a) {
        return usersById.values().toArray(a);
    }

    // unsupported bulk operations

    /**
     * This bulk-change Set operation is not supported in the Roster implementation.
     * 
     * @throws UnsupportedOperationException
     *             removeAll is not supported by this set.
     */
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This bulk-change Set operation is not supported in the Roster implementation.
     * 
     * @throws UnsupportedOperationException
     *             removeAll is not supported by this set.
     */
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This bulk-change Set operation is not supported in the Roster implementation.
     * 
     * @throws UnsupportedOperationException
     *             removeAll is not supported by this set.
     */
    public boolean addAll(Collection<? extends YahooUser> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This bulk-change Set operation is not supported in the Roster implementation.
     * 
     * @throws UnsupportedOperationException
     *             removeAll is not supported by this set.
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openymsg.network.event.SessionListener#dispatch(org.openymsg.network.FireEvent)
     */
    public void dispatch(FireEvent event) {
        final SessionEvent sEvent = event.getEvent();
        final ServiceType sType = event.getType();
        if (!(sEvent instanceof SessionFriendEvent) && sType != ServiceType.LIST) {
            log.trace("Ignoring non-list: " + event);
            return;
        }

        if (sType == ServiceType.LIST) {
            final SessionListEvent lEvent = (SessionListEvent) sEvent;
            if (lEvent.getType() != ContactListType.Friends) {
                log.trace("Ignoring non-Friends list");
                return;
            }
            log.trace("Session just received the inital user list. " + "Initializing this roster, as triggered by: "
                    + event);
            final Set<YahooUser> contacts = lEvent.getContacts();
            for (final YahooUser contact : contacts) {
                syncedAdd(contact);
            }
            return;
        }

        final SessionFriendEvent fEvent = (SessionFriendEvent) sEvent;
        final YahooUser user = fEvent.getUser();

        if (fEvent.isFailure()) {
            return;
        }
        switch (event.getType()) {
        case FRIENDADD:
            log.trace("Adding user to roster, as triggered by " + "SessionFriendEvent: " + event);
            syncedAdd(user);
            break;

        case CONTACTREJECT:
        case FRIENDREMOVE:
            log.trace("Removing user from roster as triggered by " + "SessionFriendEvent: " + event);
            syncedRemove(user.getId());
            break;

        case Y6_STATUS_UPDATE:
            log.trace("Updating user on roster as triggered by " + "SessionFriendEvent: " + event);
            syncedUpdate(user.getId(), user);
            break;

        case Y7_AUTHORIZATION:
            if (fEvent instanceof SessionFriendAcceptedEvent) {
                log.debug("Adding user to roster, as triggered by " + "SessionFriendAcceptedEvent: " + event);
                syncedAdd(user);
            }
            else if (fEvent instanceof SessionFriendRejectedEvent) {
                log.debug("Removing user from roster as triggered by " + "SessionFriendRejectedEvent: " + event);
                syncedRemove(user.getId());
            }
            else {
                log.info("Ignoring SessionFriendEvent of type " + event.getType() + " that contains an event that we"
                        + " do not know how to process: " + fEvent);
            }
            break;
        default:
            log.info("Ignoring SessionFriendEvent that came" + " with an unsupported ServiceType: " + event.getType());
            break;
        }
    }

    public void addOrUpdateAddressBook(YahooAddressBookEntry addressBookEntry) {
        String userId = addressBookEntry.getId();
        log.trace("Adding to address book: " + addressBookEntry);
        synchronized (this.addressBookUsersById) {
            this.addressBookUsersById.put(userId, addressBookEntry);
        }

        boolean isUpdate = false;
        YahooUser newUser = null;
        synchronized (usersById) {
            YahooUser user = usersById.get(userId);
            if (user != null) {
                isUpdate = true;
                newUser = createMergedUser(addressBookEntry, userId, user);
                log.trace("updated user with addressBook: " + user);
                usersById.put(userId, newUser);
            }
        }

        RosterEventType rosterType;
        // notify listeners.
        if (isUpdate) {
            rosterType = RosterEventType.update;
            broadcastEvent(new RosterEvent(this, newUser, rosterType));
        }
        log.trace("Done Adding to address book: " + addressBookEntry);

    }

    private YahooUser createMergedUser(YahooAddressBookEntry addressBookEntry, String userId, YahooUser user) {
        YahooUser newUser;
        Set<String> groupIds = user.getGroupIds();
        YahooProtocol protocol = user.getProtocol();
        newUser = new YahooUser(userId, groupIds, protocol, addressBookEntry);
        Status status = user.getStatus();
        String customMessage = user.getCustomStatusMessage();
        String customStatus = user.getCustomStatus();
        newUser.update(status, user.isOnChat(), user.isOnPager());
        if (status.equals(Status.CUSTOM)) {
            newUser.setCustom(customMessage, customStatus);
        }

        return newUser;
    }
}
