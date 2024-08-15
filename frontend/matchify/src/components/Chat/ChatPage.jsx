import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {connect} from 'react-redux';
import Grid from "@mui/material/Grid";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

import withAuth from "../Auth/withAuth";
import ChatHeader from "./ChatHeader";
import UserList from "./UserList";
import MessageList from "./MessageList";
import MessageInput from "./MessageInput";

import * as userChatsAPI from "../../api/chat";
import { getAPIURL } from "../../utils/common";

const ChatComponent = ({ userId }) => {
  const navigate = useNavigate();
  const location = useLocation();

  // Assume these props are passed correctly
  const myId = userId;
  const [selectedUser, setSelectedUser] = useState({
    id: location.state?.navigationData?.id,
    fullName: location.state?.navigationData?.fullName,
  });

  const [chatMessages, setChatMessages] = useState([]);
  const [matchedUserStatuses, setMatchedUserStatuses] = useState([]);
  const [stompClient, setStompClient] = useState(null);

  useEffect(() => {
    // Fetch user statuses and chat messages whenever dependencies change
    fetchMatchedUserStatuses();
    if (selectedUser.id != null) {
      fetchChatMessages(myId, selectedUser.id);
    }
  }, [selectedUser]);

  useEffect(() => {
    const socket = new SockJS(getAPIURL("ws"));
    const client = Stomp.over(socket)
    client.connect({}, () => {
      setStompClient(client);

      // Subscribe to user's private messages
      client.subscribe(`/user/${myId}/queue/messages`, (message) => {
        if (message.body) {
          const newMessage = JSON.parse(message.body);
          setChatMessages((prevMessages) => [...prevMessages, newMessage]);
        }
      });
      // Subscribe to public messages
      client.subscribe("/user/public", (message) => {});

      // Notify server of user connection
      client.send("/app/user.connect", {}, JSON.stringify({ userId: myId }));
    });
  }, []);

  const handleDisconnect = () => {
    if (stompClient && stompClient.connected) {
      stompClient.send(
        "/app/user.disconnect",
        {},
        JSON.stringify({ userId: myId })
      );
      stompClient.disconnect(() => {
        console.log("Disconnected from WebSocket");
      });
      navigate("/dashboard");
    }
  };

  // Function to fetch user statuses
  const fetchMatchedUserStatuses = async () => {
    try {
      const data = await userChatsAPI.getMatchedUserStatus();
      setMatchedUserStatuses(data);
    } catch (error) {
      console.error("Fetch error:", error);
    }
  };

  const setSelectedUserfunc = (recipientId) => {
    const user = matchedUserStatuses.find(
      (user) => user.userId === recipientId
    );
    const currentUserData = {
      fullName: user?.fullName,
      id: user?.userId,
    };
    setSelectedUser(currentUserData);
  };

  // Function to fetch chat messages
  const fetchChatMessages = async (senderId, recipientId) => {
    try {
      const data = await userChatsAPI.getUserChats(senderId, recipientId);
      setChatMessages(data);
    } catch (error) {
      console.error("Error fetching chat messages:", error);
    }
  };

  const handleUserClick = (recipientId) => {
    setSelectedUserfunc(recipientId);
  };

  const handleSendMessage = (content) => {
    if (stompClient && selectedUser.id) {
      const message = {
        senderId: myId,
        recipientId: selectedUser.id,
        content: content,
        timestamp: new Date().toISOString(),
      };
      console.log(matchedUserStatuses);
      const isCurrentUserInList = matchedUserStatuses.some(
        (user) => user.userId === selectedUser.id
      );

      if (!isCurrentUserInList) {
        setMatchedUserStatuses([...matchedUserStatuses, selectedUser]);
      }

      stompClient.send("/app/chat", {}, JSON.stringify(message));
      setChatMessages((prevMessages) => [...prevMessages, message]);
    } else {
      console.log("Stomp client not connected or selectedId not set");
    }
  };

  return (
    <div>
      <ChatHeader
        currentUser={selectedUser}
        handleDisconnect={handleDisconnect}
      />
      <Grid container>
        <UserList
          matchedUserStatuses={matchedUserStatuses}
          handleUserClick={handleUserClick}
        />
        {selectedUser.id && (
          <Grid item xs={9}>
            <MessageList chatMessages={chatMessages} myId={myId} />
            <MessageInput handleSendMessage={handleSendMessage} />
          </Grid>
        )}
      </Grid>
    </div>
  );
};

const mapStateToProps = (state) => ({
    userId: state.userId
});

export default connect(mapStateToProps)(withAuth(ChatComponent));
/* Debug: UserID - {selectedUser.id}, FullName - {selectedUser.fullName}*/
