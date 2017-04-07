package it.grid.storm.gateway;

import it.grid.storm.gateway.model.User;

public class SimpleUser implements User {

	private final String id;

	public SimpleUser(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

}
