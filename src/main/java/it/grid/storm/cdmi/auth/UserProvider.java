package it.grid.storm.cdmi.auth;

public interface UserProvider {

	public User getUser() throws UserProviderException;
}
