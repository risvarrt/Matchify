// Fetching the matched user status and the user chats from the backend
import api from "../utils/api";

export const getMatchedUserStatus = async () => {
    const token = localStorage.getItem("token");
    const response = await api.get('/api/v1/users/status', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });
    return response.data;
  };


export const getUserChats = async (senderId, recipientId) => {
    const token = localStorage.getItem("token");
    const response = await api.get(`/api/v1/chat/messages/${senderId}/${recipientId}`, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });
    return response.data;
  };