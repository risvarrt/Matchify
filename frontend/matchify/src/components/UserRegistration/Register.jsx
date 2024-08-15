import React, {useEffect, useState} from 'react';
import {connect} from 'react-redux';
import {Container, Grid, TextField, Button, Typography, Select, MenuItem, InputLabel} from '@mui/material';
import {register} from '../Action';
import {validateEmail, isValidString, validatePassword} from '../helper';
import toast from "react-hot-toast";
import {useNavigate} from "react-router-dom";

const Register = (props) => {
    const navigate = useNavigate();
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [location, setLocation] = useState('');
    const [ageRange, setAgeRange] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errors, setErrors] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        location: '',
        confirmPassword: '',
        age: ''
    });

    useEffect(()=>{
        if(props.registerStatus === 200) {
            toast.success('Registration Successful');
            navigate("/user-interests");
        }
    },[props.registerStatus])

    const handleChange = (event) => {
        const {name, value} = event.target;
        const newErrors = {...errors};
        if (name === 'firstName') {
            setFirstName(value);
            newErrors.firstName = isValidString(value) ? '' : 'First name is required.';
        } else if (name === 'lastName') {
            setLastName(value);
            newErrors.lastName = isValidString(value) ? '' : 'Last name is required.';
        } else if (name === 'email') {
            setEmail(value);
            newErrors.email = validateEmail(value) ? '' : 'Invalid Email';
        } else if (name === 'password') {
            setPassword(value);
            newErrors.password = validatePassword(value) ? '' : 'Password must contain 8 characters with at least one uppercase, one lowercase, one digit, and one special character.';
        } else if (name === 'confirmPassword') {
            setConfirmPassword(value);
            newErrors.confirmPassword = password === value ? '' : 'Passwords do not match.';
        } else if (name === 'location') {
            setLocation(value);
            newErrors.location = isValidString(value) ? '' : 'Select location.';
        } else if (name === 'ageRange') {
            setAgeRange(value);
            newErrors.age = isValidString(value) ? '' : 'Select age range.';
        }
        setErrors(newErrors);
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        const newErrors = {
            firstName: isValidString(firstName) ? '' : 'First name is required.',
            lastName: isValidString(lastName) ? '' : 'Last name is required.',
            email: validateEmail(email) ? '' : 'Invalid Email',
            password: validatePassword(password) ? '' : 'Password must contain 8 characters with at least one uppercase, one lowercase, one digit, and one special character.',
            location: isValidString(location) ? '' : 'Select location.',
            confirmPassword: password === confirmPassword ? '' : 'Passwords do not match.',
            age: isValidString(ageRange) ? '' : 'Select age range.',
        };

        setErrors(newErrors);

        if (isValidForm(newErrors)) {
            props.register({firstName, lastName, email, password, location, ageRange});
        } else {
            toast.error('Please check the form for errors.');
        }
    };

    const isValidForm = (errors) => {
        for (const key in errors) {
            if (errors[key]) {
                return false;
            }
        }
        return true;
    };

    return (
        <Container maxWidth="md">

            <Typography variant="h2" gutterBottom color="#d44e1c" className="roboto-medium" style={{
                display: 'flex', justifyContent: 'center', alignItems: 'center', marginTop: '5vh',
            }}>
                Matchify
            </Typography>

            <form onSubmit={handleSubmit} style={{padding: 40, minWidth: "40vw"}}>
                <Grid container spacing={3}>
                    <Grid item xs={6}>
                        <TextField
                            label="First Name"
                            variant="outlined"
                            fullWidth
                            name="firstName"
                            value={firstName}
                            onChange={handleChange}
                            error={!!errors.firstName}
                            helperText={errors.firstName}
                        />
                    </Grid>
                    <Grid item xs={6}>
                        <TextField
                            label="Last Name"
                            variant="outlined"
                            fullWidth
                            name="lastName"
                            value={lastName}
                            onChange={handleChange}
                            error={!!errors.lastName}
                            helperText={errors.lastName}
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField
                            label="Email Address"
                            variant="outlined"
                            fullWidth
                            type="email"
                            name="email"
                            value={email}
                            onChange={handleChange}
                            error={!!errors.email}
                            helperText={errors.email}
                        />
                    </Grid>
                    <Grid item xs={6}>
                        <TextField
                            label="Password"
                            variant="outlined"
                            fullWidth
                            type="password"
                            name="password"
                            value={password}
                            onChange={handleChange}
                            error={!!errors.password}
                            helperText={errors.password}
                        />
                    </Grid>
                    <Grid item xs={6}>
                        <TextField
                            label="Confirm Password"
                            variant="outlined"
                            fullWidth
                            type="password"
                            name="confirmPassword"
                            value={confirmPassword}
                            onChange={handleChange}
                            error={!!errors.confirmPassword}
                            helperText={errors.confirmPassword}
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <InputLabel htmlFor="location">Location</InputLabel>
                        <Select
                            id="location"
                            variant="outlined"
                            fullWidth
                            name="location"
                            value={location}
                            onChange={handleChange}
                            error={!!errors.location}
                        >
                            <MenuItem value="Toronto">Toronto</MenuItem>
                            <MenuItem value="Calgary">Calgary</MenuItem>
                            <MenuItem value="Ottawa">Ottawa</MenuItem>
                            <MenuItem value="Edmonton">Edmonton</MenuItem>
                            <MenuItem value="Winnipeg">Winnipeg</MenuItem>
                            <MenuItem value="Mississauga">Mississauga</MenuItem>
                            <MenuItem value="Brampton">Brampton</MenuItem>
                            <MenuItem value="Kitchener-Waterloo">Kitchener-Waterloo</MenuItem>
                            <MenuItem value="Halifax">Halifax</MenuItem>
                            <MenuItem value="Vancouver">Vancouver</MenuItem>
                            <MenuItem value="Hamilton">Hamilton</MenuItem>
                            <MenuItem value="London">London</MenuItem>
                            <MenuItem value="Winnipeg">Winnipeg</MenuItem>
                            <MenuItem value="Regina">Regina</MenuItem>
                        </Select>
                        {errors.location && <Typography variant="caption" color="error">{errors.location}</Typography>}
                    </Grid>
                    <Grid item xs={12}>
                        <InputLabel htmlFor="ageRange">Age</InputLabel>
                        <Select
                            id="ageRange"
                            variant="outlined"
                            fullWidth
                            name="ageRange"
                            value={ageRange}
                            onChange={handleChange}
                        >
                            <MenuItem value="18-24">18-24</MenuItem>
                            <MenuItem value="25-34">25-34</MenuItem>
                            <MenuItem value="35-44">35-44</MenuItem>
                            <MenuItem value="45-54">45-54</MenuItem>
                            <MenuItem value="55-64">55-64</MenuItem>
                            <MenuItem value="65+">65+</MenuItem>
                        </Select>
                        {errors.age && <Typography variant="caption" color="error">{errors.age}</Typography>}
                    </Grid>
                    <Grid item xs={12}>
                        <Button variant="contained" color="primary" type="submit">
                            Register
                        </Button>
                    </Grid>
                </Grid>
            </form>
        </Container>
    );
};

const mapStateToProps = (state) => ({
    registerStatus: state.registerStatus
});

const mapDispatchToProps = {register}

export default connect(mapStateToProps, mapDispatchToProps)(Register);
