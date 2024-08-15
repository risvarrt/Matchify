// Import necessary modules
import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { Toaster } from "react-hot-toast";

// Import your components
import CreateEvent from './components/CreateEvent/CreateEvent';
import UserLogin from './components/UserLogin/Login';
import About from './components/About';
import Register from './components/UserRegistration/Register';
import NotFound from './components/NotFound';
import UserInterestForm from './components/UserInterests/UserInterestForm';
import Dashboard from "./components/Dashboard/dashboard";
import ChatPage from "./components/Chat/ChatPage";
import ViewEvent from "./components/ViewEvent/VEvent";

export default function App() {
  return (

    <>
      <Router>
        <Routes>
          <Route path="/" element={<UserLogin />} />
          <Route path="/view-event" element={<ViewEvent />} />
          <Route exact path="/register" element={<Register />} />
          <Route exact path="/dashboard" element={<Dashboard />} />
          <Route path="/about" element={<About />} />
          <Route path="/user-interests" element={<UserInterestForm />} />
          <Route path="/create-event" element={<CreateEvent />} />
          <Route path="/chat" element={<ChatPage />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </Router>
      <Toaster />
    </>
  );
}