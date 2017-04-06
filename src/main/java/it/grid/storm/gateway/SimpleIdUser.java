package it.grid.storm.gateway;

import it.grid.storm.gateway.model.User;

public class SimpleIdUser implements User {

	private final String id;

	public SimpleIdUser(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

}
