import styles from "./DashboardStyles";
import { Card, Button, ListGroup } from "react-bootstrap";
import {useNavigate} from "react-router-dom";
import eventImage from './event.jpg';

const Event = ({eventDetails, isSlider}) => {
    const name = eventDetails.createdBy.firstName+" "+eventDetails.createdBy.lastName
    const navigate = useNavigate();

    const goToEventDetails = () => {
        navigate('/view-event', {state: {eventDetails}})
    }

    const image = eventDetails.imageURL ? eventDetails.imageURL : eventImage;

    return (
        <Card style={{...styles.eventCard, ...(!isSlider && styles.cardWidth)}}>
            <Card.Img variant="top" src={image} style={styles.cardImage}/>
            <Card.Body style={styles.cardBody}>
                <Card.Title style={styles.cardTitle}>{eventDetails.eventName}</Card.Title>
                <ListGroup variant="flush" style={styles.listGroup}>
                    <ListGroup.Item style={styles.cardList}>Host Name: {name}</ListGroup.Item>
                    <ListGroup.Item style={styles.cardList}>Date: {eventDetails.eventDate}</ListGroup.Item>
                    <ListGroup.Item style={styles.cardList}>Time: {eventDetails.startTime}</ListGroup.Item>
                    <ListGroup.Item style={styles.cardList}>Location: {eventDetails.city}</ListGroup.Item>
                </ListGroup>
                <Button variant="secondary" style={styles.cardButton} onClick={goToEventDetails}><span style={styles.cardButtonText}>More Info</span></Button>
            </Card.Body>
      </Card>
    )
}

export default Event;