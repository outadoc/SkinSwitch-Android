package fr.outadev.skinswitch.skin;

import java.util.Date;

/**
 * Represents a stored skin, as it is in the database.
 * 
 * @author outadoc
 * 
 */
public class Skin {

	private int id;
	private String name;
	private String description;
	private Date creationDate;

	/**
	 * Creates a new skin.
	 * 
	 * @param id
	 *            the skin ID.
	 * @param name
	 *            the name of the skin.
	 * @param description
	 *            the description of the skin.
	 * @param creationDate
	 *            the date of creation of the skin.
	 */
	public Skin(int id, String name, String description, Date creationDate) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.creationDate = creationDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}
