import request from "@/utils/request"

export const queryAllCompanyApi = (queryForm) => request.get(
    `/admin/company?name=${queryForm.name}&type=${queryForm.type}&pageSize=${queryForm.pageSize}&pageNum=${queryForm.pageNum}`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);

//admin查询全部用户接口
//admin查询全部用户接口
export const batchDelCompanyApi = (params) => request.delete(
    `/admin/company/del?${params.toString()}`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);

//admin查询全部用户接口
export const querySingleCompanyApi = (id) => request.get(
    `/admin/company/single?companyId=${id}`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);

// UpdateCompanyApi
//admin查询全部用户接口
export const UpdateCompanyApi = (companyForm) => request.put(
    `/admin/company`,
    companyForm,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);
export const SaveCompanyApi = (companyForm) => request.post(
    `/admin/company`,
    companyForm,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);

export const DelCompanyApi = (companyId) => request.delete(
    `/admin/company?companyId=${companyId}`,
    {
        headers: {
            'Content-Type': 'application/json'
        },
        needAuth: true
    }
);
