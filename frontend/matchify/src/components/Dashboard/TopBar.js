import {connect} from 'react-redux';
import styles from "./DashboardStyles";
import { Button, Col, Row, Image } from "react-bootstrap";
import { resetUser } from "../Action";
import {useNavigate} from "react-router-dom";

const TopBar = (props) => {   
    const navigate = useNavigate();

    const logout = () => {
        props.resetUser();
        localStorage.removeItem('token');
        navigate("/");
    }

    const goToCreateEvent = () => {
        navigate("/create-event");
    }

    return (
            <Row style={styles.topbarRow}>
                <Col md="9" style={styles.userInfoCol}>
                    <p style={styles.hello}>Hello {props.userName}!</p>
                    </Col>
                <Col md="2">
                    <Button variant = "info" style={styles.buttons} onClick={goToCreateEvent}>+ Create Event</Button>
                </Col>
                <Col md="1"><Button variant = "danger" style={styles.buttons} onClick={logout}>Logout</Button></Col>
            </Row>
    )
}

const mapStateToProps = (state) => ({
    userName: state.userName
})

const mapDispatchToProps = {resetUser}

export default connect(mapStateToProps, mapDispatchToProps)(TopBar);