import React, { useCallback, useEffect, useState } from "react";
import {
    ReactNodeLikeProps,
    newLogger,
    storageClearByKeyPrefix,
    storageGetByKeyPrefix
} from "../../core/utils";
import { loginByCredentialsApi, loginByRefreshTokenApi, newAuthenticatedAxiosInstance } from "./authentication-api";
import { AuthenticationProps, UserLogin } from "./authentication";
import { environment } from "../../environment/environment";
import { AxiosInstance, AxiosRequestConfig } from "axios";

const log = newLogger("security/authentication/authentication-provider");
interface AuthenticationState {
    userLogin?: UserLogin;
    authenticationProps?: AuthenticationProps;
    isAuthenticated: boolean;
    isAuthenticating: boolean;
    authenticationError: Error | null;
    axiosInstance?: AxiosInstance;
    login?: (userLogin: UserLogin) => Promise<void>;
    logout?: () => Promise<void>;
}

const authenticationInitialState: AuthenticationState = {
    isAuthenticated: false,
    isAuthenticating: false,
    authenticationError: null
};

export const AuthenticationContext = React.createContext<AuthenticationState>(
    authenticationInitialState
);
const AuthenticationProvider: React.FC<ReactNodeLikeProps> = ({ children }) => {
    const [state, setState] = useState<AuthenticationState>(authenticationInitialState);

    const login = useCallback<(userLogin: UserLogin) => Promise<void>>(__login, [state]);
    const logout = useCallback<() => Promise<void>>(__logout, [state]);

    useEffect(__loginEffect, [state.isAuthenticating]);

    const value = { ...state, login, logout };
    return (
        <AuthenticationContext.Provider value={value}>
            {children}
        </AuthenticationContext.Provider>
    );

    async function __login(userLogin: UserLogin) {
        log("__login");
        setState({
            ...state,
            userLogin: userLogin,
            authenticationProps: undefined,
            isAuthenticating: true,
            authenticationError: null,
            axiosInstance: undefined
        });
    }

    async function __logout() {
        log("__logout");
        setState({
            ...state,
            userLogin: undefined,
            authenticationProps: undefined,
            isAuthenticated: false,
            authenticationError: null,
            axiosInstance: undefined
        });

        await storageClearByKeyPrefix("");
    }

    function __loginEffect() {
        let cancelled = false;
        authenticate();
        return () => {
            cancelled = true;
        }

        async function authenticate() {
            if (state.isAuthenticated) {
                return;
            }

            try {
                log("{__loginEffect}", "(authenticate)", "start");
                await authenticateByRefreshToken();
                if (!state.isAuthenticating) {
                    return;
                }

                await authenticateByCredentials();
                log("{__loginEffect}", "(authenticate)", "success");
            } catch (error) {
                if (cancelled || !error) {
                    return;
                }

                log("{__loginEffect}", "(authenticate)", error);
                setState({
                    ...state,
                    userLogin: undefined,
                    authenticationProps: undefined,
                    isAuthenticating: false,
                    isAuthenticated: false,
                    authenticationError: error,
                    axiosInstance: undefined
                });
            }
        }

        async function authenticateByRefreshToken() {
            log("{__loginEffect}", "(authenticateByRefreshToken)", "start");
            const refreshToken: string = (await storageGetByKeyPrefix<string>(environment.STORAGE_REFRESH_TOKEN_KEY))[0];
            if (refreshToken) {
                const authenticationProps: AuthenticationProps = await loginByRefreshTokenApi(refreshToken);
                const axiosInstance: AxiosInstance = newAuthenticatedAxiosInstance(authenticationProps, __onInterceptorSuccess, __onInterceptorFailure);
                if (cancelled) {
                    log("{__loginEffect}", "(authenticateByRefreshToken)", "cancelled");
                    return;
                }

                setState({
                    ...state,
                    userLogin: undefined,
                    authenticationProps: authenticationProps,
                    isAuthenticating: false,
                    isAuthenticated: true,
                    authenticationError: null,
                    axiosInstance: axiosInstance
                });

                log("{__loginEffect}", "(authenticateByRefreshToken)", "success");
                return;
            }

            log("{__loginEffect}", "(authenticateByRefreshToken)", "no refresh token");
        }

        async function authenticateByCredentials() {
            log("{__loginEffect}", "(authenticateByCredentials)", "start");
            if (!state.userLogin) {
                throw "no credentials";
            }

            const authenticationProps: AuthenticationProps = await loginByCredentialsApi(state.userLogin);
            const axiosInstance: AxiosInstance = newAuthenticatedAxiosInstance(authenticationProps, __onInterceptorSuccess, __onInterceptorFailure);
            if (cancelled) {
                log("{__loginEffect}", "(authenticateByCredentials)", "cancelled");
                return;
            }

            setState({
                ...state,
                userLogin: undefined,
                authenticationProps: authenticationProps,
                isAuthenticating: false,
                isAuthenticated: true,
                authenticationError: null,
                axiosInstance: axiosInstance
            });

            log("{__loginEffect}", "(authenticateByCredentials)", "success");
        }
    }

    async function __onInterceptorSuccess(authenticationProps: AuthenticationProps, config: AxiosRequestConfig) {
        log('{__onInterceptorSuccess}', "tokens renewed");
        const axiosInstance: AxiosInstance = newAuthenticatedAxiosInstance(authenticationProps, __onInterceptorSuccess, __onInterceptorFailure);
        axiosInstance.request(config);
        setState({
            ...state,
            userLogin: undefined,
            authenticationProps: authenticationProps,
            isAuthenticated: true,
            authenticationError: null,
            axiosInstance: axiosInstance
        });
    }

    async function __onInterceptorFailure(error: any) {
        log('{__onInterceptorFailure}', "refresh token expired");
        setState({
            ...state,
            userLogin: undefined,
            authenticationProps: undefined,
            isAuthenticated: false,
            authenticationError: error,
            axiosInstance: undefined
        });
    }
};

export default AuthenticationProvider;
