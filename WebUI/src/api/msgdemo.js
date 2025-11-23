 import request from "@/utils/request"

//http://localhost:8080/ai/chat?prompt=%E4%BD%A0%E5%A5%BD&chatId=1
export const chat = (prompt, chatId) => request.get(`/ai/chat?prompt=${prompt}&chatId=${chatId}`);


export const test = (prompt, chatId) => request.post(
    '/ai/chat',
    { 'prompt': prompt, 'chatId': chatId },
    {
        
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'text/event-stream;charset=UTF-8'
        }
    }
);


