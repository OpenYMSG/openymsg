package org.openymsg;

/** User's name in Yahoo */
public class Name {
	/** first name */
	private final String firstName;
	/** last name */
	private final String lastName;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Name))
			return false;
		Name other = (Name) obj;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Name [firstName=" + firstName + ", lastName=" + lastName + "]";
	}
}
