import request from "@/utils/request"
import { data } from "autoprefixer";


export const userQuerySessionApi = () => request.get(
    `/user/ai/session`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
         needAuth: true
    }
);

export const userDeleteSessionApi = (session_Id) => request.delete(
    `/user/ai/session/${session_Id}`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
         needAuth: true
    }
);

export const userCreateSessionApi = (session) => request.post(
    `/user/ai/session`,
    {
        sessionId:session.sessionId,
        sessionTitle:session.sessionTitle
    },
    {
        headers: {
            'Content-Type': 'application/json'
        },
         needAuth: true
    }
);