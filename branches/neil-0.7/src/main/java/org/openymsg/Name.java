package org.openymsg;

/** User's name in Yahoo */
public class Name {
	/** first name */
	private String firstName;
	/** last name */
	private String lastName;

	/**
	 * Create a new instance with first and last name
	 * @param firstName first name, blank if null
	 * @param lastName last namee, blank if null
	 */
	public Name(String firstName, String lastName) {
		this.firstName = firstName == null ? "" : firstName;
		this.lastName = lastName == null ? "" : lastName;
	}

	/**
	 * get first name
	 * @return first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * get last name
	 * @return last name
	 */
	public String getLastName() {
		return lastName;
	}

}
