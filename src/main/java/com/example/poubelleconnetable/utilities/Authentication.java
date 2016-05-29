package com.example.poubelleconnetable.utilities;

import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;

/**
 * Utility class used to authenticate and authorize users.
 */
public class Authentication {

	/**
	 * Checks if user is authorized.
	 *
	 * @param user
	 *            the user to be validated.
	 *
	 * @throws UnauthorizedException
	 *             if user is not authorized.
	 */
	public static void validateUser(final User user) throws UnauthorizedException {
		// Google Cloud Endpoints sets user to a non-null object if the request came from an approved
		// client and the request was correctly authenticated. However, if the request was not correctly
		// authenticated, the user object will be null.
		// Backend requires that all requests are authenticated and UnauthorizedException is thrown for
		// requests that are not correctly authenticated.
		if (user == null) {
			throw new UnauthorizedException("The user is not authorized.");
		}

	}
}
