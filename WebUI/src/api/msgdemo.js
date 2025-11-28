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


export  const response=async (text,chatId) => await fetch('/api/user/ai/chat', {
    method: 'POST',
    headers: {
        'Content-type': 'Application/json',
        'accept': 'text/event-stream'
    },
    body: JSON.stringify({
        'prompt': text,
        'chatId': chatId
    }
    )

})

