package model;

import java.util.List;

public class Colocation {

	public String id;
	public String admin;
	List<String> members;
	String member;

	public Colocation(String id, String admin) {
		super();
		this.id = id;
		this.admin = admin;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

}
