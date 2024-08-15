import React from 'react';
import { List, ListItem, ListItemAvatar, Avatar, ListItemText, Divider } from '@mui/material';
import Grid from '@mui/material/Grid';

const UserList = ({ matchedUserStatuses, handleUserClick }) => {
    return (
        <Grid item xs={3} className="user-list" style={{borderRight: '1px solid white'}} >
            <Divider />
            <List>
                {matchedUserStatuses.map(user => (
                    <ListItem button key={user.userId} onClick={() => handleUserClick(user.userId)} style={{ borderBottom: '0.25px solid black' }}>
                        <ListItemAvatar>
                            <Avatar src={user?.avatarUrl || 'default_avatar_url'} />
                        </ListItemAvatar>
                        <ListItemText primary={user.fullName} secondary={user.status || 'OFFLINE'} />
                    </ListItem>
                ))}
            </List>
        </Grid>
    );
};

export default UserList;
