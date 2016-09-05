package com.practicaSV.gameLabz.services;

public interface AuthenticationService {

    String doLogin(String userName, String pass);

    boolean validateSession(String userName, String sessionId);

    void doLogout(String userName);
}
