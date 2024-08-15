import React, { useEffect } from 'react';
import {connect} from 'react-redux';
import TopBar from "./TopBar";
import styles from "./DashboardStyles"
import UserMatch from "./UserMatch";
import Event from "./Event";
import NoEvents from './NoEvents';
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import { getProfileMatches, getMatchedEvents, getUserEvents, getProfileInfo } from '../Action';
import {isValidElement} from '../helper';
import { Container } from 'react-bootstrap';
import withAuth from '../Auth/withAuth';

const Dashboard = (props) => {
    const slidesToShow = 6;

    useEffect(()=>{
        props.getProfileMatches();
        props.getUserEvents(); 
        props.getProfileInfo();      
    },[])

    useEffect(()=>{
        if(isValidElement(props.matchedProfiles))
        {
            let userIds = [];
            props.matchedProfiles.forEach(element => {
                userIds.push(element.userId)
            });
            const data = {
                "matchedUserIds": userIds
            }
            props.getMatchedEvents(data);
        }
    },[props.matchedProfiles])

    const settings = {
        dots: true,                                         
        infinite: true,
        speed: 500,
        slidesToShow: slidesToShow,
        slidesToScroll: slidesToShow,
        arrows: false
    }

    return (
        <Container fluid>
            <TopBar />
            <div style={styles.sectionDivider}><p style={styles.sectionDividerText}>Profiles That Matches With Yours</p></div>
            <div style={styles.profileCardDiv}>
                {isValidElement(props.matchedProfiles) && props.matchedProfiles.map((user) => {
                    return <UserMatch userDetails = {user}/>
                })}
            </div>
            <div style={{...styles.sectionDivider, ...styles.eventsDiv}}>
                <p style={styles.sectionDividerText}>Events You might be Interested In</p>
            </div>
            {isValidElement(props.matchedEvents) && props.matchedEvents.length >= slidesToShow && <Slider {...settings}>
                {props.matchedEvents.map((event) => {
                    return <Event eventDetails = {event} isSlider={true}/>
                })}
            </Slider>}
            <div style={styles.sliderDiv}>
                {isValidElement(props.matchedEvents) && props.matchedEvents.length < slidesToShow && 
                props.matchedEvents.map((event) => {
                        return <Event eventDetails = {event} isSlider={false}/>
                    })
                }
            </div>
            <div style={{...styles.sectionDivider, ...styles.eventsDiv, ...styles.upComingEventsDiv}}>
                <p style={styles.sectionDividerText}>Events Created By You</p>
            </div>
            {isValidElement(props.userEvents) && props.userEvents.length >= slidesToShow && <Slider {...settings}>
                 {isValidElement(props.userEvents) && props.userEvents.map((event) => {                    
                    return <Event eventDetails = {event} isSlider={true}/>
                })}
            </Slider>}
            <div style={styles.sliderDiv}>
                {isValidElement(props.userEvents) && props.userEvents.length < slidesToShow && props.userEvents.map((event) => {                    
                        return <Event eventDetails = {event} isSlider={false}/>
                })}
                 {isValidElement(props.userEvents) && props.userEvents.length === 0 && <NoEvents />}
            </div>
            <div style={{...styles.sectionDivider, ...styles.eventsDiv, ...styles.upComingEventsDiv}}>
                <p style={styles.sectionDividerText}>Your Joined Events</p>
            </div>
            {isValidElement(props.joinedEvents) && props.joinedEvents.length >= slidesToShow && <Slider {...settings}>
                 {isValidElement(props.joinedEvents) && props.joinedEvents.map((event) => {                    
                    return <Event eventDetails = {event} isSlider={true}/>
                })}
            </Slider>}
            <div style={styles.sliderDiv}>
                {isValidElement(props.joinedEvents) && props.joinedEvents.length < slidesToShow && props.joinedEvents.map((event) => {                    
                        return <Event eventDetails = {event} isSlider={false}/>
                })}
            {isValidElement(props.joinedEvents) && props.joinedEvents.length === 0 && <NoEvents />}
            </div>
            <div style={styles.finalDiv}></div>
        </Container>
    )
}

const mapStateToProps = (state) => ({
    matchedProfiles: state.matchedProfiles,
    userEvents: state.userEvents,
    matchedEvents: state.matchedEvents,
    joinedEvents: state.joinedEvents
});

const mapDispatchToProps = {
    getProfileMatches, 
    getMatchedEvents,
    getUserEvents,
    getProfileInfo
}

export default connect(mapStateToProps, mapDispatchToProps)(withAuth(Dashboard));