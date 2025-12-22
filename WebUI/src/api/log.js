import request from "@/utils/request.js";

export const queryLogApi = (Form) => request.get(
    `/admin/log/queryAll?pageNum=${Form.pageNum}&pageSize=${Form.pageSize}`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);