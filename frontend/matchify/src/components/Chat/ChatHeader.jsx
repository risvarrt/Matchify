import React from 'react';
import { Grid, Typography, Avatar, ListItem, ListItemIcon, ListItemText, List } from '@mui/material';
import WestIcon from '@mui/icons-material/West';

const ChatHeader = ({ currentUser, handleDisconnect }) => {
    return (
        <Grid container className="chat-header" >
            <Grid item xs={3} style={{ backgroundColor: 'white' ,  height: '9vh', alignItems: 'center', justifyContent: 'center' }} >
                <ListItem>
                    <ListItemIcon>
                        <WestIcon onClick={handleDisconnect} />
                    </ListItemIcon>
                    <ListItemText primary="Chat" />
                </ListItem>
            </Grid>
            <Grid item xs={9} style={{ backgroundColor: 'pink', alignItems: 'center', justifyContent: 'center', height: '9vh' }}>
                <List>
                    <ListItem>
                        <ListItemIcon>
                            <Avatar src={currentUser?.avatarUrl || 'default_avatar_url'} />
                        </ListItemIcon>
                        <ListItemText primary={currentUser?.fullName || 'User Name'} />
                    </ListItem>
                </List>
            </Grid>
        </Grid>
    );
};

export default ChatHeader;
