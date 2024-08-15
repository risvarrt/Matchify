import { all, put, takeLatest } from 'redux-saga/effects';
import { postCall, getCall } from './apiCalls';
import { PATHS } from './Constants';

export function* register(payload) 
{
    try{
        let response = yield postCall(PATHS.DEFAULT_PATH+'/auth/register',payload.params);
        if(response.status === 200) {
            // store the jwt token in local storage
            let data = yield response.json();
            localStorage.setItem('token', data.token);

            let params = payload.params;
            yield put({type: 'register_success', status: response.status, token: data.token})
            yield put({type: 'create_user', params})
        } else {
            throw new Error('Error in registering user');
        }
    }
    catch(e){
        console.log(e);
    }
}

export function* logout() 
{
    try{
        yield put({type:'logout_user'})
    }
    catch(e){
        console.log(e);
    }
}

export function* getProfileMatches()
{
    try{
        let response = yield getCall(PATHS.DEFAULT_PATH+'/match/findMatches');
        if(response.status === 200) {
            let data = yield response.json();
            yield put({type: 'set_profile_matches', matchedProfiles: data.matchedusers})
        } else {
            throw new Error('Error in getting profile matches');
        }
    }
    catch(e){
        console.log(e);
    }
}

export function* getUserEvents() 
{
    try{
        let response = yield getCall(PATHS.DEFAULT_PATH+'/event/my-events');
        if(response.status === 200) {
            let data = yield response.json();
            yield put({type: 'set_user_events', userEvents: data})
        } else {
            throw new Error('Error in getting events created by user');
        }
    }
    catch(e){
        console.log(e);
    }
}

export function* getMatchedEvents(payload)
{
    try{
        let response = yield postCall(PATHS.DEFAULT_PATH+'/event/matched-events',payload.params);
        if(response.status === 200) {
            let data = yield response.json();
            yield put({type: 'set_matched_events', matchedEvents: data})
        } else {
            throw new Error('Error in fetching recommended events');
        }
    }
    catch(e){
        console.log(e);
    }
}

export function* getProfileInfo()
{
    try{
        let response = yield getCall(PATHS.DEFAULT_PATH+'/user-profile');
        if(response.status === 200) {
            let data = yield response.json();
            yield put({type: 'set_profile_info', profileInfo: data})
        } else {
            throw new Error('Error in fetching recommended events');
        }
    }
    catch(e){
        console.log(e);
    }
}

export default function* rootSaga() {
    yield all([
        takeLatest('register_user', register),
        takeLatest('logout',logout),
        takeLatest('profile_matches', getProfileMatches),
        takeLatest('user_events', getUserEvents),
        takeLatest('get_matched_events', getMatchedEvents),
        takeLatest('profile_info', getProfileInfo)
    ]);
}
