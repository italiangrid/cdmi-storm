package it.grid.storm.spi;

public class UserId implements User {

	private final String id;

	UserId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

}
