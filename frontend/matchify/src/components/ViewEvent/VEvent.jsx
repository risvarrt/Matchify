import React, { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import ChatIcon from "@mui/icons-material/Chat";
import { useNavigate, useLocation } from "react-router-dom";

import {
  Button,
  Dialog,
  DialogTitle,
  DialogActions,
  List,
  ListItem,
  Typography,
  Card,
  CardContent,
  CardActions,
  CardMedia,
  Box,
  ListItemText,
} from "@mui/material";
import { getAPIURL } from "../../utils/common";

function VEvent() {
  const navigate = useNavigate();
  const location = useLocation();
  // useState hooks
  const [openAttendeesDialog, setOpenAttendeesDialog] = useState(false);
  const [openSuccessDialog, setOpenSuccessDialog] = useState(false);
  const [openErrorDialog, setOpenErrorDialog] = useState(false);
  const [isJoined, setIsJoined] = useState(false);
  const [attendees, setAttendees] = useState([]);
  const [eventDetails, setEventDetails] = useState(
    location.state?.eventDetails || {}
  );

  // Use useSelector to retrieve the logged-in user ID from the Redux store
  const userId = useSelector((state) => state.userId || "1"); // deafult user id added if the code cant access the user id from the state

  const token = localStorage.getItem("token");
  const eventId = eventDetails.eventId || "1";

  useEffect(() => {
    // Fetch attendees
    const fetchAttendees = async () => {
      try {
        const response = await fetch(getAPIURL("api/v1/event/event-attendees-list"), {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({ eventId }),
          }
        );

        if (response.ok) {
          const data = await response.json();
          const namesWithIds = data.map((item) => ({
            id: item.userId,
            fullName: `${item.firstName} ${item.lastName}`,
          }));
          setAttendees(namesWithIds);

          // Check if the current user's ID is in the list of attendees
          const isUserJoined = namesWithIds.some(
            (attendee) => attendee.id === parseInt(userId)
          );
          setIsJoined(isUserJoined);
        } else {
          console.error("Failed to fetch attendees");
        }
      } catch (error) {
        console.error("Error fetching attendees:", error);
      }
    };

    fetchAttendees();
  }, [token, eventId, userId]);

  const createMapLink = (latitude, longitude) => {
    return `https://www.google.com/maps/search/?api=1&query=${latitude},${longitude}`;
  };
  // function to Join or Leave event
  const postEvent = async (action) => {
    const url =
      action === "join"
        ? getAPIURL("api/v1/event/join-event")
        : getAPIURL("api/v1/event/leave-event");

    try {
      const response = await fetch(url, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ eventId, userId }),
      });
      return response.ok;
    } catch (error) {
      console.error(`There was an error during the event ${action}:`, error);
      return false;
    }
  };
  // fucntion to handle join event
  const handleJoin = async () => {
    const success = await postEvent("join");
    if (success) {
      setOpenSuccessDialog(true);
      setIsJoined(true);
    } else {
      setOpenErrorDialog(true);
    }
  };
  // function to handle user redirection to chat page
  const handleUserClick = (attendeeId, attendeeFullName) => {
    if (attendeeId !== userId) {
      navigate("/chat", {
        state: {
          navigationData: { id: attendeeId, fullName: attendeeFullName },
        },
      });
    }
  };
  // function to handle leave event
  const handleLeave = async () => {
    const success = await postEvent("leave");
    if (success) {
      setOpenSuccessDialog(true);
      setIsJoined(false);
    } else {
      setOpenErrorDialog(true);
    }
  };
  // function to handle dialog close
  const handleCloseDialog = () => {
    setOpenSuccessDialog(false);
    setOpenErrorDialog(false);
  };
  // function to handle open attendees dialog
  const handleOpenAttendeesDialog = () => {
    setOpenAttendeesDialog(true);
  };
  // function to handle close attendees dialog
  const handleCloseAttendeesDialog = () => {
    setOpenAttendeesDialog(false);
  };

  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        padding: "50px",
        backgroundColor: "#f8afa6",
      }}
    >
      <Card
        raised
        sx={{
          margin: "auto",
          width: "70%",
          boxShadow: "4px 4px 4px rgba(0.5, 0.5, 0.5, 0.5);",
          padding: "20px",
        }}
      >
        <Typography
          variant="h4"
          component="h2"
          sx={{ color: "#d44e1c", textAlign: "center" }}
        >
          Event Details
        </Typography>
        <CardMedia
          component="img"
          alt="Event"
          image={eventDetails.imageURL || "defaultImage.jpg"} //This is just an alternate image placeholder we'll place some image instead of this
          sx={{ width: "100%", maxHeight: "300px", objectFit: "cover", my: 1 }}
        />
        <CardContent>
          <Typography variant="h5" component="h3" sx={{ my: 3 }}>
            {eventDetails.eventName}
          </Typography>
          <Typography variant="body2" sx={{ my: 1 }}>
            Date: {eventDetails.eventDate}
          </Typography>
          <Typography variant="body2" sx={{ my: 1 }}>
            Time: {eventDetails.startTime} - {eventDetails.endTime}
          </Typography>
          <Typography variant="body1" color="textSecondary" sx={{ my: 1 }}>
            {eventDetails.description}
          </Typography>
          <Typography variant="body2" color="textSecondary" sx={{ my: 1 }}>
            Instructions: {eventDetails.additionalInstructions}
          </Typography>

          <Typography variant="body1" sx={{ my: 2 }}>
            Location:{" "}
            <a
              href={createMapLink(
                eventDetails.latitude,
                eventDetails.longitude
              )}
              target="_blank"
              rel="noopener noreferrer"
              style={{ textDecoration: "none" }}
            >
              {eventDetails.address}, {eventDetails.city}
            </a>
          </Typography>
          <Typography
            variant="h6"
            sx={{
              mt: 1,
              mb: 1,
            }}
          >
            Event Attendees:{" "}
            {attendees.length > 0 ? (
              <span
                onClick={handleOpenAttendeesDialog}
                style={{
                  textDecoration: "underline",
                  cursor: "pointer",
                  color: "#d44e1c",
                  fontSize: "1.0rem",
                }}
              >
                {attendees[0]?.fullName} and {attendees.length - 1} others
              </span>
            ) : (
              "No attendees yet"
            )}
          </Typography>
        </CardContent>
        <CardActions
          sx={{
            justifyContent: "center",
            p: 2,
          }}
        >
          <Button
            onClick={isJoined ? handleLeave : handleJoin}
            variant="contained"
            color="primary"
            fullWidth
            sx={{
              maxWidth: "25%",
              bgcolor: "#ffab91",
              "&:hover": {
                bgcolor: "#ff8a65",
              },
            }}
          >
            {isJoined ? "Leave Event" : "Join Event"}
          </Button>
        </CardActions>
      </Card>

      <Dialog open={openSuccessDialog} onClose={handleCloseDialog}>
        <DialogTitle>
          {isJoined
            ? "You have successfully joined the event!"
            : "You have successfully left the event!"}
        </DialogTitle>
        <DialogActions>
          <Button onClick={handleCloseDialog} variant="contained">
            OK
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openErrorDialog} onClose={handleCloseDialog}>
        <DialogTitle>
          {isJoined
            ? "An error occurred while trying to join the event."
            : "An error occurred while trying to leave the event."}
        </DialogTitle>
        <DialogActions>
          <Button onClick={handleCloseDialog} variant="contained">
            OK
          </Button>
        </DialogActions>
      </Dialog>
      <Dialog
        open={openAttendeesDialog}
        onClose={handleCloseAttendeesDialog}
        aria-labelledby="attendees-dialog-title"
        maxWidth="250px"
      >
        <DialogTitle
          id="attendees-dialog-title"
          sx={{
            textAlign: "center",
            color: "#d44e1c",
          }}
        >
          Event Attendees
        </DialogTitle>
        <List
          sx={{
            maxHeight: 250,
            width: 500,
            overflow: "auto",
            textAlign: "center",
          }}
        >
          {attendees.map((attendee, index) => (
            <ListItem
              key={index}
              button
              disabled={attendee.id === parseInt(userId)}
              onClick={() => handleUserClick(attendee.id, attendee.fullName)}
            >
              <ListItemText primary={attendee.fullName} />
              {attendee.id !== parseInt(userId) && <ChatIcon />}
            </ListItem>
          ))}
        </List>
        <DialogActions>
          <Button onClick={handleCloseAttendeesDialog} variant="contained">
            OK
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default VEvent;
