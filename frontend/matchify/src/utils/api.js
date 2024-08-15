import axios from "axios";
import { getBaseURL } from "./common";

const api = axios.create({
  baseURL: getBaseURL(),
});

export default api;
