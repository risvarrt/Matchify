import React, { useState } from "react";
import {
  Button,
  TextField,
  Grid,
  Typography,
  Paper,
  Input,
  IconButton,
  Tooltip,
} from "@mui/material";
import PhotoCamera from "@mui/icons-material/PhotoCamera";
import toast from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import withAuth from "../Auth/withAuth";
import { getAPIURL } from "../../utils/common";

const CreateEvent = () => {
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [date, setDate] = useState("");
  const [STime, setSTime] = useState("");
  const [ETime, setETime] = useState("");
  const [additionalInstructions, setAdditionalInstructions] = useState("");
  const [address, setAddress] = useState("");
  const [pincode, setPincode] = useState("");
  const [city, setCity] = useState("");
  const [image, setImage] = useState(null);

  const handleTitleChange = (event) => {
    setTitle(event.target.value);
  };

  const handleImageChange = (event) => {
    setImage(event.target.files[0]);
  };

  const handleDescriptionChange = (event) => {
    setDescription(event.target.value);
  };

  const handleCityChange = (event) => {
    setCity(event.target.value);
  };

  const handleDateChange = (event) => {
    setDate(event.target.value);
  };

  const handleSTimeChange = (event) => {
    setSTime(event.target.value);
  };

  const handleETimeChange = (event) => {
    setETime(event.target.value);
  };

  const handleAdditionalInstructionsChange = (event) => {
    setAdditionalInstructions(event.target.value);
  };

  const handleAddressChange = (event) => {
    setAddress(event.target.value);
  };

  const handlePincodeChange = (event) => {
    setPincode(event.target.value);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const token = localStorage.getItem("token");

    const formData = new FormData();
    formData.append("EventImage", image);
    formData.append("eventName", title);
    formData.append("description", description);
    formData.append("eventDate", date);
    formData.append("startTime", STime);
    formData.append("endTime", ETime);
    formData.append("additionalInstructions", additionalInstructions);
    formData.append("address", address);
    formData.append("pincode", pincode);
    formData.append("city", city);

    try {
      const response = await fetch(getAPIURL("api/v1/event/create-event"), {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      });
      if (response.ok) {
        toast.success("Event created successfully");
        navigate("/dashboard");
      } else {
        toast.error("Failed to create event");
      }
    } catch (error) {
      toast.error("Failed to create event");
      console.error("Error creating event:", error);
    }
  };

  return (
    <div style={{ padding: 20 }}>
      <form onSubmit={handleSubmit} style={{ padding: 40, minWidth: "50vw" }}>
        <Typography
          variant="h5"
          gutterBottom
          style={{
            textAlign: "left",
            color: "#d44e1c",
            paddingTop: "10px",
            paddingBottom: "10px",
          }}
        >
          Create Your Event
        </Typography>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Title"
              variant="outlined"
              fullWidth
              value={title}
              onChange={handleTitleChange}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Date"
              variant="outlined"
              fullWidth
              type="date"
              value={date}
              onChange={handleDateChange}
              InputLabelProps={{
                shrink: true,
              }}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              label="Description"
              variant="outlined"
              fullWidth
              multiline
              rows={4}
              value={description}
              onChange={handleDescriptionChange}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Event Start Time"
              variant="outlined"
              fullWidth
              type="time"
              value={STime}
              onChange={handleSTimeChange}
              InputLabelProps={{
                shrink: true,
              }}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Event End Time"
              variant="outlined"
              fullWidth
              type="time"
              value={ETime}
              onChange={handleETimeChange}
              InputLabelProps={{
                shrink: true,
              }}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              label="Additional Instructions"
              variant="outlined"
              fullWidth
              multiline
              rows={4}
              value={additionalInstructions}
              onChange={handleAdditionalInstructionsChange}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              label="Address"
              variant="outlined"
              fullWidth
              multiline
              rows={2}
              value={address}
              onChange={handleAddressChange}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Pincode"
              variant="outlined"
              fullWidth
              value={pincode}
              onChange={handlePincodeChange}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="City"
              variant="outlined"
              fullWidth
              value={city}
              onChange={handleCityChange}
            />
          </Grid>
          <Grid item xs={12}>
            <Input
              style={{ display: "none" }}
              accept="image/*"
              id="icon-button-file"
              type="file"
              onChange={handleImageChange}
            />
            <label htmlFor="icon-button-file">
              <Tooltip title="Upload Image">
                <IconButton
                  color="primary"
                  aria-label="upload picture"
                  component="span"
                >
                  <PhotoCamera />
                </IconButton>
              </Tooltip>
            </label>
          </Grid>
          <Grid item xs={12}>
            <Button variant="contained" color="primary" type="submit">
              Create Event
            </Button>
          </Grid>
        </Grid>
      </form>
    </div>
  );
};

export default withAuth(CreateEvent);
