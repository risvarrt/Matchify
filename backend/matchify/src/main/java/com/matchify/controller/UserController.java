package com.matchify.controller;

import com.matchify.dto.request.UserChatStatusRequest;
import com.matchify.dto.response.CreateEventResponse;
import com.matchify.dto.response.FetchConnectedUsersResponse;
import com.matchify.dto.response.UserProfileResponse;
import com.matchify.dto.response.UserChatStatusResponse;
import com.matchify.dto.ws.ConnectUser;
import com.matchify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Connect the user
     * @param connectUser the user to connect
     * @return the connected users
     */
    @MessageMapping("/user.connect")
    @SendTo("/user/public")
    public FetchConnectedUsersResponse connect(
            @Payload ConnectUser connectUser
    ) {
        return userService.connect(connectUser);
    }

    /**
     * Disconnect the user
     * @param user the user to disconnect
     * @return the connected user
     */
    @MessageMapping("/user.disconnect")
    @SendTo("/user/public")
    public FetchConnectedUsersResponse disconnect(
            @Payload ConnectUser user
    ) {
        return userService.disconnect(user);
    }

    /**
     * Find connected users
     * @return the connected user
     */
    @GetMapping("/api/v1/users")
    public ResponseEntity<List<FetchConnectedUsersResponse>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }

    @GetMapping("/api/v1/user-profile")
    public ResponseEntity<UserProfileResponse> getProfileDetails() {
        return ResponseEntity.ok(userService.fetchLoggedInUserDetails());
    }

    /**
     * Get status for users
     * @return the status for the users
     */
    @GetMapping("/api/v1/users/status")
    public ResponseEntity<List<UserChatStatusResponse>> getStatusForUsers() {
        return new ResponseEntity<List<UserChatStatusResponse>>(
            userService.getMessagedUsers(), HttpStatusCode.valueOf(200));
    }

}

