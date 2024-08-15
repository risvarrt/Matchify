import React from 'react';
import { List, ListItem, ListItemText, Box } from '@mui/material';

const MessageList = ({ chatMessages, myId }) => {
    return (
        <List className="messageArea" style={{
            height: '75vh',
            overflowY: 'auto',
            backgroundColor: '#e0e0e0',
        }}>
            {chatMessages.map(message => {
                const isMyMessage = message.senderId === myId;
                return (
                    <ListItem key={message.id} sx={{ justifyContent: isMyMessage ? 'flex-end' : 'flex-start' }}>
                        <Box sx={{
                            maxWidth: '100%',
                            wordBreak: 'break-word',
                            padding: '10px',
                            borderRadius: '10px',
                            bgcolor: 'white',
                            color: 'text.primary',
                            mt: 1,
                            mb: 1,
                        }}>
                            <ListItemText
                                primary={message.content}
                                secondary={new Date(message.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                sx={{
                                    '& .MuiListItemText-primary': {
                                        fontWeight: 'bold',
                                    },
                                    '& .MuiListItemText-secondary': {
                                        fontSize: '0.7em',
                                        mt: 0.5
                                    }
                                }}
                            />
                        </Box>
                    </ListItem>
                );
            })}
        </List>
    );
};

export default MessageList;
