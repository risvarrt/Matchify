// write a function that makes a request to the server to get all the interests and subinterests from backend
import api from "../utils/api";

export const getInterests = async () => {
  const token = localStorage.getItem("token");
  const response = await api.get('/api/v1/interest', {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const addInterests = async (interests) => {
  // read token from local storage
  const token = localStorage.getItem("token");
  const response = await api.post('/api/v1/interest/fill-my-interest', interests, {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};
