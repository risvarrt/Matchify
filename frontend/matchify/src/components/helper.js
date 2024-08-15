export const validateEmail = (email) => {
    const regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9. -]+\.[a-zA-Z]{2,4}$/;
    return regex.test(email);
}

export const isValidString = (data) => {
    return data !== null && data !== undefined && data !== '' && data.length > 0;
};

export const validatePassword = (password) => {
    const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()\-_=+\\|[\]{};:'",.<>/?]).{8,}$/;
    return regex.test(password);
}

export const isValidElement = (data) => {
    return data !== null && data !== undefined;
};