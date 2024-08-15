import React from 'react';
import { Grid, TextField, Fab } from '@mui/material';
import SendIcon from '@mui/icons-material/Send';

const MessageInput = ({ handleSendMessage }) => {

    const sendMessage = (message) => {
        if (message) {
            handleSendMessage(message);
        }
    };

    return (
        <Grid container style={{ padding: '20px' }}>
            <Grid item xs={11}>
                <TextField
                    id="outlined-basic-email"
                    label="Type Something"
                    fullWidth
                    variant="outlined"
                    InputProps={{ style: { backgroundColor: 'white' } }}
                    onKeyPress={(e) => {
                        if (e.key === 'Enter' && e.target.value) {
                            sendMessage(e.target.value);
                            e.preventDefault();
                            e.target.value = '';
                        }
                    }}
                />
            </Grid>
            <Grid item xs={1} align="right">
                <Fab color="primary" aria-label="add" onClick={() => {
                    const input = document.getElementById('outlined-basic-email');
                    if (input.value) {
                        sendMessage(input.value);
                        input.value = '';
                    }
                }}>
                    <SendIcon />
                </Fab>
            </Grid>
        </Grid>
    );
};

export default MessageInput;
