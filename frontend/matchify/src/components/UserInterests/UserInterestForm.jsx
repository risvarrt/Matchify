import * as React from "react";
import { useState } from "react";
import {
  Button,
  Stack,
  Box,
  Typography,
  Chip,
  Autocomplete,
  TextField,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";

import * as userInterestsAPI from "../../api/userInterests";
import withAuth from "../Auth/withAuth";

const UserInterestPage = () => {
  const navigate = useNavigate();

  const [selectedGroupInterest, setSelectedGroupInterest] = useState([]);
  const [selectedCategoryInterests, setSelectedCategoryInterests] = useState([]);
  const [interests, setInterests] = useState([]);
  const [subInterests, setSubInterests] = useState([]);

  React.useEffect(() => {
    // get user interest from the server
    userInterestsAPI
      .getInterests()
      .then((_interests) => {
        const allSubInterests = _interests
          .map((interest) => interest.categories)
          .flat();

        const interests = _interests.map(interest => {
          return {
            id: interest.groupId,
            name: interest.groupName,
            sub: interest.categories,
          }
        });

        setInterests(interests);
        setSubInterests(allSubInterests);
      })
      .catch((error) => {
        console.error("Failed to fetch user interests", error);
      });
  }, []);

  const handleGroupInterestChange = (event, values) => {
    // unique interests check
    const selectedIds = selectedGroupInterest.map((i) => i.id);
    if (values.filter((i) => selectedIds.includes(i.id)).length > 1) return;

    setSelectedGroupInterest(values);
  };

  const handleSubInterestChange = (event, values) => {
    // unique sub interests check
    const selectedIds = selectedCategoryInterests.map((i) => i.id);
    if (values.filter((i) => selectedIds.includes(i)).length > 1) return;

    setSelectedCategoryInterests(values);
  };

  const onSubmit = async () => {
    if (!selectedGroupInterest.length && !selectedCategoryInterests.length) {
      toast.error("Please select at least one interest");
      return;
    }

    try {
      //    Add user interest object
      const interestsObj = {
        groups: selectedGroupInterest.map(x => x.id),
        categories: selectedCategoryInterests.map(x => x.id),
      };
      //    Make the request to the server
      const response = await userInterestsAPI.addInterests(interestsObj);
      console.log(response);

      toast.success("Interests added!");

      //   Redirect to the home page
      navigate("/");
    } catch (error) {
      console.error("Failed to add user interests", error);
    }
  };

  return (
      <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        minHeight: "100vh",
        width: "100%",
      }}
    >
      {/*<Typography variant="h1" gutterBottom color="primary">*/}
      {/*  Matchify*/}
      {/*</Typography>*/}
      <Box
        className="form-container"
        sx={{
          width: "33.33%",
          padding: "20px",
          backgroundColor: "#f9f1f0",
          borderRadius: "5px",
          boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
        }}
      >
        <Typography variant="h5" gutterBottom>
          Tell us your interests
        </Typography>
        <Typography variant="subtitle2" mb={2} gutterBottom>
          This will help us provide you with better matches
        </Typography>
        <Stack spacing={2}>
          <Autocomplete
            multiple
            id="group-interests"
            options={interests}
            getOptionLabel={(option) => option.name}
            value={selectedGroupInterest}
            onChange={handleGroupInterestChange}
            renderInput={(params) => (
              <TextField {...params} label="General Interests" />
            )}
            renderTags={(value, getTagProps) => {
              return value.map((option, index) => (
                <Chip
                  key={index}
                  label={option.name}
                  {...getTagProps({ index })}
                />
              ));
            }}
            disableCloseOnSelect
          />
          <Autocomplete
            multiple
            id="sub-interests"
            options={subInterests}
            getOptionLabel={(option) => {
              const group = interests.find((i) =>
                i.sub.map((x) => x.id).includes(option.id)
              ).name;
              return `${option.name} (${group})`;
            }}
            value={selectedCategoryInterests}
            onChange={handleSubInterestChange}
            renderInput={(params) => (
              <TextField {...params} label="Specific Interests" />
            )}
            renderTags={(value, getTagProps) => {
              return value.map((option, index) => (
                <Chip
                  key={option.id}
                  label={option.name}
                  {...getTagProps({ index })}
                />
              ));
            }}
            disableCloseOnSelect
          />
          <Button
            variant="contained"
            color="primary"
            onClick={onSubmit}
            sx={{ color: "#FAE8E0", }}
          >
            Find me a match!
          </Button>
        </Stack>
      </Box>
    </Box>
  );
};

export default withAuth(UserInterestPage);
