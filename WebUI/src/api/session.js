import request from "@/utils/request"


export const userSessionApi = () => request.get(
    `/user/ai/session`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
         needAuth: true
    }
);