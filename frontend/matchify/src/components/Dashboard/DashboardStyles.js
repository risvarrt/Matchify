const styles = {
    topbarRow: {
        width:'100%', 
        margin:0,
        paddingTop: '1vh',
    },
    hello: {
        fontSize: '2.5em',
        fontFamily: 'Georgia',
        marginBottom: 0,
        color: "#BA0F30"
    },
    userInfoCol: {
        display:'flex', 
        flexDirection:'row',
        alignItems: 'center'
    },
    dp: {
        height: '3.5em',
        width:'3.5em',
    },
    buttons: {
        float: 'right',
        marginTop: '2vh',
        paddingTop: 0,
        paddingBottom: '3px',
        paddingLeft: '10px',
        paddingRight: '10px'
    },
    createEventCol: {
        display: 'flex',
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center'
    },
    sectionDivider: {
        backgroundColor: "rgba(255,253,208,0.6)",
        height: '7vh',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        boxShadow: '5px 5px 10px rgba(0,0,0,0.3)',
        marginTop: '2vh',
        marginBottom: '2vh'
    },
    sectionDividerText: {
        marginLeft: '1vw',
        fontSize: '4vh',
        marginBottom: 0,
        fontFamily: 'Georgia',
        color: "#9C2D41"
    },
    profileCardDiv: {
        display: 'flex',
        flexDirection: 'row',
        paddingTop: '3vh',
        paddingBottom: '3vh'
    },
    userCard: {
        marginLeft: '1vw',
        marginRight: '1vw',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center'
    },
    eventCard: {
        backgroundColor: 'rgba(182, 182, 179,0.8)',
        borderColor: 'rgba(255,253,208,0.6)',
        borderWidth: '3px',
        height: '65vh',
        marginLeft: '1vw',
    },
    cardWidth: {
        width: '15.5%'
    },
    cardImage: {
        height:'25vh'
    },
    cardBody: {
        padding:'10px',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'flex-start',
        justifyContent: 'center'
    },
    cardTitle: {
        fontSize: '1em',
    },
    listGroup: {
        width: '100%'
    },
    cardList: {
        backgroundColor: 'rgba(250, 250, 241, 0.1)',
        fontSize: '13px',
        padding: '5px'
    },
    cardButton: { 
        marginTop: '1vh',
        paddingTop: 0,
        paddingBottom: '3px',
        paddingLeft: '6px',
        paddingRight: '6px'
    },
    cardButtonText: {
        fontSize: '13px', 
    },
    circleDiv: {
        height: '6vw',
        width: '6vw',
        borderRadius: '50%',
        backgroundImage: 'linear-gradient(to bottom right, rgba(220,174,150,1), rgba(255,105,180, 1))',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        boxShadow: '5px 5px 10px rgba(0,0,0,0.3)'
    },
    initials: {
        fontSize: '2vw',
        marginBottom: 0
    },
    username: {
        marginBottom: 0,
        fontWeight: 'bold',
    },
    chat: {
        height: '4vh',
        width: '4vh',
        marginLeft: '1vw',
        cursor: 'pointer',
    },
    userNameDiv: {
        display: 'flex',
        flexDirection: 'row',
        marginTop: '2vh'
    },
    eventImage: {
        height: '9vw',
        width: '9vw',
        margin: '1vw'
    },
    eventName: {
        color: '#EDD7D7',
        margin:0,
        paddingBottom: '2vh',
        paddingRight: '1vw',
        paddingLeft: '1vw'
    },
    eventsDiv: {
        marginBottom: '7vh',
    },
    upComingEventsDiv: {
        marginTop: '8vh'
    },
    finalDiv: {
        height:'7vh'
    },
    sliderDiv: {
        display: 'flex',
        flexDirection: 'row'
    },
    noEventsDiv: {
        width: '100%',
        display: 'flex',
        justifyContent: 'center'
    },
    noEventsText: {
        fontSize: '25px'
    }
}

export default styles;
