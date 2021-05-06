import { AxiosInstance } from "axios";
import { newLogger } from "../../core/utils";
import { Account } from "./account";


const log = newLogger('pages/account/account-api');

export const getAccountApi: (axiosInstance: AxiosInstance) => Promise<Account[]> = async (axiosInstance) => {
    return axiosInstance
        .get<Account[]>('/account')
        .then((response) => {
            log('{getAccountApi}', 'success');
            return response.data;
        })
}

export const getOneAccountApi: (axiosInstantce: AxiosInstance, accountId: number) => Promise<Account> = async (axiosInstance, accountId) => {
    return axiosInstance
        .get<Account>(`/account/${accountId}`)
        .then((response) => {
            log('{getOneAccountApi}', 'success');
            return response.data;
        })
}

export const postAccountApi: (axiosInstantce: AxiosInstance, account: Account) => Promise<Account> = async (axiosInstantce, account) => {
    return axiosInstantce
        .post<Account>('/account', account)
        .then((response) => {
            log('{postAccountApi}', 'success');
            return response.data;
        })
}

export const putAccountApi: (axiosInstantce: AxiosInstance, account: Account) => Promise<Account> = async (axiosInstantce, account) => {
    return axiosInstantce
        .put<Account>(`/account/${account.id}`, account)
        .then((response) => {
            log('{postAccountApi}', 'success');
            return response.data;
        })
}

export const deleteAccountApi: (axiosInstantce: AxiosInstance, accountId: number) => Promise<Account> = async (axiosInstantce, accountId) => {
    return axiosInstantce
        .delete<Account>(`/account/${accountId}`)
        .then((response) => {
            log('{postAccountApi}', 'success');
            return response.data;
        })
}
