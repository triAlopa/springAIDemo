import request from "@/utils/request"


export const registerApi = (registerForm) => request.post(
    '/user/register',
    registerForm,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: false
    },

);
export const requestCodeAPi = (email, nickName) => request.post(
    `/user/emailCode/${nickName}?email=${email}`,
    null,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: false
    }
);

export const loginApi = (loginForm) => request.post(
    '/user/login',
    loginForm,
    {

        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: false
    }
);

export const loginCodeApi = (email) => request.get(
    `/user/login/${email}`,
    {

        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: false
    }
);



export const userInfoApi = () => request.get(
    `/user/info`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);


export const userChangePassApi = (passworldForm) => request.post(
    `/user/modifyPass`,
        passworldForm,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);