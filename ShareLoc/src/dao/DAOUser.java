package dao;

import model.User;

public class DAOUser extends DAOAbstractFacade<User> {

	public DAOUser() {
		super(User.class);
	}

}
