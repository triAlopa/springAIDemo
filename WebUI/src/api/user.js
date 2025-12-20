import request from "@/utils/request"


export const registerApi = (registerForm) => request.post(
    '/user/register',
    registerForm,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: false
    },
);
export const requestCodeAPi = (email, nickName) => request.post(
    `/user/emailCode/${nickName}?email=${email}`,
    null,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: false
    }
);

export const loginApi = (loginForm) => request.post(
    '/user/login',
    loginForm,
    {

        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: false
    }
);

export const loginCodeApi = (email) => request.get(
    `/user/login/${email}`,
    {

        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: false
    }
);

export const userInfoApi = () => request.get(
    `/user/info`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);

export const userChangePassApi = (passworldForm) => request.post(
    `/user/modifyPass`,
    passworldForm,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);

//admin查询全部用户接口
export const queryAllUserApi = (queryForm) => request.get(
    `/admin/user/queryAll?nickName=${queryForm.nickName}&gender=${queryForm.gender}&start=${queryForm.start}&end=${queryForm.end}&pageSize=${queryForm.pageSize}&pageNum=${queryForm.pageNum}`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);

//admin查询全部用户接口
export const updateOrSaveApi = (userForm) => request.put(
    `/admin/user`,
    userForm,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);

//admin查询全部用户接口
export const querySingleUser = (userId) => request.get(
    `/admin/user/single?id=${userId}`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);


//admin查询全部用户接口
export const delSingUser = (userId) => request.delete(
    `/admin/user/del/${userId}`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);

//admin查询全部用户接口
export const batchDelUser = (params) => request.delete(
    `/admin/user/del?${params.toString()}`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);

//admin查询全部用户接口
export const queryUserReport = (timeStamp) => request.get(
    `/admin/report/userInfo?timeStamp=${timeStamp}`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);

export const getEmailApi = () => request.get(
        `/admin/report/email`,
        {
            headers: {
                'Content-Type': 'application/json'
            },
            needAuth: true
        }
    );

export const getUserExcel = () => request.post(
    `/admin/report`,
    {}, // 如果没有 body 数据，也要传个空对象占位
    {
        headers: {
            'Content-Type': 'application/json' // 请求头通常是 json
        },
        responseType: 'blob', // 必须：告诉浏览器这是个二进制文件流
        needAuth: true
    }
);