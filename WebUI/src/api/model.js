import request from "@/utils/request"

/** 
 * 用户查询聊天窗口对应的hr及公司信息
 * @param {string} modelId 
  * @returns
 */
export const queryModelApi = (modelId) => request.get(
    `/model/user?modelId=${modelId}`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    },

);

