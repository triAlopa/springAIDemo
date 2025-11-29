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


