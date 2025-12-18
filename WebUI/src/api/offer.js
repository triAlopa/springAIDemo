import request from "@/utils/request"





//offer
export  const  userOfferApi = (type,sessionId) => request.post(`/user/offer`,
    {
        'sessionId':sessionId,
        'type':type
    },
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);


