import React, { useState, useEffect } from 'react';
import { Button, TextField, Typography, Container, Grid } from '@mui/material';
import { Link, useNavigate } from 'react-router-dom';
import { login } from '../Action';
import toast from "react-hot-toast";
import { getAPIURL } from '../../utils/common';

const Login = () => {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        checkLoggedIn();
    }, []);

    const checkLoggedIn = () => {
        const sessionToken = localStorage.getItem('token');
        if (sessionToken) {
            navigate('/dashboard');
        }
    };

    const handleEmailChange = (event) => {
        setEmail(event.target.value);
    };

    const handlePasswordChange = (event) => {
        setPassword(event.target.value);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            const response = await fetch(
                getAPIURL('api/v1/auth/login'), {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }, body: JSON.stringify({ email, password }),
            });

            if (response.ok) {
                const data = await response.json();
                const jwtToken = data.jwt;
                storeToken(jwtToken);
                navigate('/dashboard');
            } else {
                const errorData = await response.json();
                setErrorMessage(errorData.message);
            }
        } catch (error) {
            console.error('An error occurred:', error);
            setErrorMessage('An error occurred. Please try again later.');
        }
    };

    const storeToken = (token) => {
        localStorage.setItem('token', token);
    };

    return (<Container maxWidth="xl">
        <Grid container justifyContent="center" alignItems="center" spacing={2}>
            <Grid item xs={12}>
                <Typography variant="h2" gutterBottom color="#d44e1c" className="roboto-medium" style={{
                    display: 'flex', justifyContent: 'center', alignItems: 'center', marginTop: '5vh',
                }}>
                    Matchify
                </Typography>

                <div className="login-container">
                    <form onSubmit={handleSubmit} style={{
                        marginTop: '10vh', width: '30vw', padding: 40
                    }}>
                        {errorMessage && (<Typography variant="body2" color="error">
                            {errorMessage}
                        </Typography>)}
                        <TextField
                            fullWidth
                            type="email"
                            name="email"
                            label="E-Mail"
                            placeholder="youremailid@dal.ca"
                            variant="outlined"
                            margin="normal"
                            value={email}
                            onChange={handleEmailChange}
                            required
                        />
                        <TextField
                            fullWidth
                            type="password"
                            name="password"
                            label="Password"
                            variant="outlined"
                            margin="normal"
                            value={password}
                            onChange={handlePasswordChange}
                            required
                        />
                        <Button type="submit" variant="contained" color="primary" fullWidth style={{
                            marginTop: 20
                        }}>
                            Login
                        </Button>
                        <Typography variant="body2" align="center" paddingTop={2} gutterBottom>
                            New to Matchify?
                            <Link to={"/register"} style={{ marginLeft: 5 }}>Click here</Link>
                        </Typography>
                    </form>
                </div>
            </Grid>
        </Grid>
    </Container>);
};

export default Login;
