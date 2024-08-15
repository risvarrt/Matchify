const INITIAL_STATE = {
    userId:null,
    userName: null,
    userEmail: null,
    userLocation: null,
    userAge: null,
    registerStatus: null,
    token: null,
    userInterest: {},
    matchedProfiles:null,
    userEvents: null,
    matchedEvents: null,
    joinedEvents: null
};

const rootReducer = (state = INITIAL_STATE, action) => {
    switch(action.type)
    {
        case 'create_user': return {
            ...state,
            userName: action.name,
            userEmail: action.email,
            userLocation: action.location,
            userAge: action.age
        }
        case 'register_success': 
        return {
            ...state,
            token: action.token,
            registerStatus: action.status
        }
        case 'logout_user': 
        return {
            ...state,
            userName: null,
            userEmail: null,
            userLocation: null,
            userAge: null,
            registerStatus: null,
            token: null,
            userInterest: {},
            matchedProfiles: null,
            userEvents: null,
            matchedEvents: null
        }
        case 'set_profile_matches':
            return {
                ...state,
                matchedProfiles:action.matchedProfiles
            }
        case 'set_user_events': return {
            ...state,
            userEvents: action.userEvents
        }
        case 'set_matched_events': return {
            ...state,
            matchedEvents: action.matchedEvents
        }
        case 'set_profile_info': 
        let name = action.profileInfo.firstName + " " + action.profileInfo.lastName;
        return {
            ...state,
            userId: action.profileInfo.userId,
            userName: name,
            userEmail: action.profileInfo.email,
            userLocation: action.profileInfo.location,
            userAge: action.profileInfo.ageRange,
            joinedEvents: action.profileInfo.joinedEvents
        }
        default: return state;
    }
}

export default rootReducer;