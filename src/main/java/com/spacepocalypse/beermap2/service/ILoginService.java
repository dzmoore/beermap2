package com.spacepocalypse.beermap2.service;

import com.spacepocalypse.beermap2.domain.MappedUser;
import com.spacepocalypse.beermap2.service.LoginService.AuthData;

public interface ILoginService {

    AuthData authUser(String username, String password);

    boolean changePassword(String username, String currentPassword, String newPassword);

    MappedUser createUser(String username, String password);

    String getUniqueSalt();

    boolean doesUserExist(String username);

    boolean addUserRole(int userId, String string);

    MappedUser findUserByName(String username);

    boolean removeUserRole(int userId, String roleName);

}
