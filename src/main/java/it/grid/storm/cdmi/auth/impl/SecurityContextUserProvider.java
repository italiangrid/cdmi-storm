package it.grid.storm.cdmi.auth.impl;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import it.grid.storm.cdmi.auth.User;
import it.grid.storm.cdmi.auth.UserProvider;
import it.grid.storm.cdmi.auth.UserProviderException;

public class SecurityContextUserProvider implements UserProvider {

	@Override
	public User getUser() throws UserProviderException {

		SecurityContext context = SecurityContextHolder.getContext();

		Authentication authentication = context.getAuthentication();
		if (authentication == null) {
			throw new UserProviderException("Null Authentication found!");
		}

		if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
			throw new UserProviderException("Unexpected Authentication found!");
		}

		return new IamUser((UsernamePasswordAuthenticationToken) authentication);
	}

}
