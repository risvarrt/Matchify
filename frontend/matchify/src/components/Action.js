export const register = (params) => {
    return {
        type: 'register_user',
        params
    }
}

export const resetUser = () => {
    return {type:'logout'}
}

export const getProfileMatches = () => {
    return {type:'profile_matches'}
}

export const getMatchedEvents = (params) => {
    return {
        type:"get_matched_events", 
        params
    }
}

export const getUserEvents = () => {
    return {type:'user_events'}
}

export const getProfileInfo = () => {
    return {type:'profile_info'}
}