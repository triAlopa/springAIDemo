import request from "@/utils/request"


//http://localhost:8080/ai/chat?prompt=%E4%BD%A0%E5%A5%BD&chatId=1
export const chat = (prompt, chatId) => request.get(`/ai/chat?prompt=${prompt}&chatId=${chatId}`);


// export const test = (prompt, chatId) => request.post(
//     '/user/ai/chat',
//     { 'prompt': prompt, 'chatId': chatId },
//     {

//         headers: {
//             'Content-Type': 'application/json',
//             'Accept': 'text/event-stream;charset=UTF-8'
//         }
//     }
// );


//发送信息接口
export  const sendApi=async (text,sessionId,userToken) => await fetch('/api/user/ai/send', {
    method: 'POST',
    headers: {
        'authorization':userToken,
        'Content-type': 'Application/json',
        'accept': 'text/event-stream'
    },
    body: JSON.stringify({
        'prompt': text,
        'sessionId': sessionId
    }
    )

})

//保存信息接口
export  const  storeMessageApi = (message,sessionId) => request.post(`/user/ai/message/save`,
    {
        'sessionId':sessionId,
        'type':message.type,
        'contentType':message.contentType,
        'textContent':message.textContent,
    },
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);

//信息接口
export const queryMessages = (sessionId) => request.get(`/user/ai/message?sessionId=${sessionId}`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);

