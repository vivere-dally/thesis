import { AxiosInstance } from "axios";
import { newLogger } from "../../core/utils";
import { environment } from "../../environment/environment";
import { Transaction, TransactionSumsPerMonth, TransactionType } from "./transaction";


const log = newLogger('pages/transaction/transaction-api');

export const getTransactionApi:(axiosInstance: AxiosInstance, accountId: number, page: number, message: string | undefined, transactionType: TransactionType | undefined) => Promise<Transaction[]> = async (axiosInstance, accountId, page, message, transactionType) => {
    let uri = `/account/${accountId}/transaction?page=${page}&size=${environment.PAGE_SIZE}`;
    if (message) {
        uri = `${uri}&message=${message}`;
    }

    if (transactionType) {
        uri = `${uri}&transactionType=${transactionType}`
    }

    return axiosInstance
        .get<Transaction[]>(uri)
        .then((response) => {
            log('{getTransactionApi}', 'success');
            return response.data;
        });
}

export const postTransactionApi: (axiosInstance: AxiosInstance, accountId: number, transaction: Transaction) => Promise<Transaction> = async (axiosInstance, accountId, transaction) => {
    return axiosInstance
        .post<Transaction>(`/account/${accountId}/transaction`, transaction)
        .then((response) => {
            log('{postTransactionApi}', 'success');
            return response.data;
        });
}

export const getTransactionValuesPerMonthApi: (axiosInstance: AxiosInstance, accountId: number) => Promise<TransactionSumsPerMonth[]> = async (axiosInstance, accountId) => {
    return axiosInstance
        .get<TransactionSumsPerMonth[]>(`/account/${accountId}/transaction/report/sumsPerMonth`)
        .then((response) => {
            log('{getTransactionValuesPerMonthApi}', 'success');
            return response.data;
        });
}
