import request from "@/utils/request"


export const registerApi = (registerForm) => request.post(
    '/user/register',
    registerForm,
    {

        headers: {
            'Content-Type': 'application/json'
        }
    }
);
export const requestCodeAPi = (email,nickName) => request.post(
    `/user/emailCode/${nickName}`,
    email,
    {
        headers: {
            'Content-Type': 'application/json'
        }
    }
);

