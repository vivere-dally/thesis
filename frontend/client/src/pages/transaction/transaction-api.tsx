import { AxiosInstance } from "axios";
import { newLogger } from "../../core/utils";
import { environment } from "../../environment/environment";
import { Transaction } from "./transaction";


const log = newLogger('pages/transaction/transaction-api');

export const getTransactionApi: (axiosInstance: AxiosInstance, accountId: number, page: number) => Promise<Transaction[]> = async (axiosInstance, accountId, page) => {
    return axiosInstance
        .get<Transaction[]>(`/account/${accountId}/transaction?page=${page}&size=${environment.PAGE_SIZE}`)
        .then((response) => {
            log('{getTransactionApi}', 'success');
            return response.data;
        });
}

export const postTransactionApi: (axiosInstance: AxiosInstance, accountId: number) => Promise<Transaction> = async (axiosInstance, accountId) => {
    return axiosInstance
        .post<Transaction>(`/account/${accountId}/transaction`)
        .then((response) => {
            log('{postTransactionApi}', 'success');
            return response.data;
        });
}
