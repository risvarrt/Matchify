package com.matchify.service;

import com.matchify.dto.response.FetchConnectedUsersResponse;
import com.matchify.dto.response.UserProfileResponse;
import com.matchify.dto.response.UserChatStatusResponse;
import com.matchify.dto.ws.ConnectUser;

import java.util.List;

public interface UserService {
    FetchConnectedUsersResponse connect(ConnectUser user);
    FetchConnectedUsersResponse disconnect(ConnectUser user);
    List<FetchConnectedUsersResponse> findConnectedUsers();
    UserProfileResponse fetchLoggedInUserDetails();


    List<UserChatStatusResponse> getStatusForUsers(List<Integer> userIds);
    List<UserChatStatusResponse> getMessagedUsers();
}
