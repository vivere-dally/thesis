import { ActionState, ActionType, newReducer, StateCrud } from "../../core/entity";
import { Account } from "./account";
import { newLogger, ReactNodeLikeProps } from "../../core/utils";
import React, { useContext, useReducer } from "react";
import { AuthenticationContext } from "../../security/authentication/authentication-provider";
import { deleteAccountApi, getOneAccountApi, postAccountApi, putAccountApi } from "./account-api";


const log = newLogger('pages/account/account-api');


interface AccountState extends StateCrud<Account, number> { }
const reducer = newReducer<AccountState, Account, number>();
const initialAccountState: AccountState = {
    executing: false
}

export const AccountContext = React.createContext<AccountState>(initialAccountState);
export const AccountProvider: React.FC<ReactNodeLikeProps> = ({ children }) => {

    const authenticationContext = useContext(AuthenticationContext);
    const [state, dispatch] = useReducer(reducer, initialAccountState);
    const { data, executing, actionType, actionError } = state;

    const value = { data, executing, actionType, actionError };
    return (
        <AccountContext.Provider value={value}>
            {children}
        </AccountContext.Provider>
    )

    async function __getOne(accountId: number): Promise<Account | void> {
        log('{__getOne}', 'start')
        dispatch({ actionState: ActionState.STARTED, actionType: ActionType.GET_ONE });
        return getOneAccountApi(authenticationContext.axiosInstance!, accountId)
            .then((result) => {
                log('{__getOne}', 'sucess');
                dispatch({ actionState: ActionState.SUCCEEDED, actionType: ActionType.GET_ONE, data: result });
                return result;
            })
            .catch((error) => {
                log('{__getOne}', 'failure');
                dispatch({ actionState: ActionState.FAILED, actionType: ActionType.GET_ONE, data: error });
            });
    }

    async function __post(account: Account): Promise<Account | void> {
        log('{__post}', 'start');
        dispatch({ actionState: ActionState.STARTED, actionType: ActionType.POST });
        return postAccountApi(authenticationContext.axiosInstance!, account)
            .then(result => {
                log('{__post}', 'success');
                dispatch({ actionState: ActionState.SUCCEEDED, actionType: ActionType.POST, data: result });
                return result;
            })
            .catch(error => {
                log('{__post}', 'failure');
                dispatch({ actionState: ActionState.FAILED, actionType: ActionType.POST, data: error });
            });
    }

    async function __put(account: Account): Promise<Account | void> {
        log('{__put}', 'start');
        dispatch({ actionState: ActionState.STARTED, actionType: ActionType.PUT });
        return putAccountApi(authenticationContext.axiosInstance!, account)
            .then(result => {
                log('{__put}', 'success');
                dispatch({ actionState: ActionState.SUCCEEDED, actionType: ActionType.PUT, data: result });
                return result;
            })
            .catch(error => {
                log('{__put}', 'failure');
                dispatch({ actionState: ActionState.FAILED, actionType: ActionType.PUT, data: error });
            });
    }

    async function __delete(accountId: number): Promise<Account | void> {
        log('{__delete}', 'start');
        dispatch({ actionState: ActionState.STARTED, actionType: ActionType.DELETE });
        return deleteAccountApi(authenticationContext.axiosInstance!, accountId)
            .then(result => {
                log('{__delete}', 'success');
                dispatch({ actionState: ActionState.SUCCEEDED, actionType: ActionType.DELETE, data: result });
                return result;
            })
            .catch(error => {
                log('{__delete}', 'failure');
                dispatch({ actionState: ActionState.FAILED, actionType: ActionType.DELETE, data: error });
            });
    }
}