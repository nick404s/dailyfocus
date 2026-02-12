package com.nick404s.dailyfocus.util;

import com.nick404s.dailyfocus.model.User;

public interface AuthenticatedUserProvider {

    User getAuthenticatedUser();
}
