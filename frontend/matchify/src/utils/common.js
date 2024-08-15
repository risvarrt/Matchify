export const isProduction = process.env.REACT_APP_ENV === 'production';
export const getBaseURL = () => {
    return isProduction ? 'http://172.17.1.14:8080/' : 'http://localhost:8080/';
}

export const getAPIURL = (apiURL) => {
    return getBaseURL() + apiURL;
}