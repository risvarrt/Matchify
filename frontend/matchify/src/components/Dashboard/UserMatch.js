import {Image} from "react-bootstrap";
import styles from "./DashboardStyles";
import {useNavigate} from "react-router-dom";

const UserMatch = ({userDetails}) => {
    const navigate = useNavigate();
    const name = userDetails.name.split(" ");
    let firstInitial = name[0].charAt(0);
    let lastInitial = name[(name.length)-1].charAt(0);
    const initials = firstInitial+lastInitial;
    const navigationData = {id: userDetails.userId, fullName: userDetails.name};

    const goToChat = () => {
        navigate('/chat', {state: {navigationData}});
    }

    return (
            <div style={styles.userCard}>
                <div style={styles.circleDiv}><p style={styles.initials}>{initials}</p></div>
                <div style={styles.userNameDiv}>
                    <p style={styles.username}>{userDetails.name}</p>
                    <Image src={require("./chat-icon.png")} style={styles.chat} onClick={goToChat} />
                </div>
            </div>
    )
}

export default UserMatch;