import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from "axios";
import { newLogger, storageSet } from "../../core/utils";
import { environment } from "../../environment/environment";
import { AuthenticationProps, UserAuthenticated, UserLogin } from "./authentication";


const log = newLogger("security/authentication/authentication-api");
const __axios = axios.create({
    baseURL: environment.WEB_API_URL
});

const __axiosRequestConfig: AxiosRequestConfig = {
    headers: {
        "Content-Type": "application/json"
    }
};

async function parseLoginResponse(response: AxiosResponse<any>): Promise<AuthenticationProps> {
    const authenticationProps: AuthenticationProps = {
        user: response.data as UserAuthenticated,
        tokenType: response.headers["tokentype"],
        accessToken: response.headers["accesstoken"],
        refreshToken: response.headers["refreshtoken"],
    };

    await storageSet(environment.STORAGE_REFRESH_TOKEN_KEY, authenticationProps.refreshToken);
    return authenticationProps;
}

export const loginByCredentialsApi: (userLogin: UserLogin) => Promise<AuthenticationProps> = async (userLogin) => {
    return __axios
        .post("login", userLogin, __axiosRequestConfig)
        .then(async (response) => {
            return await parseLoginResponse(response);
        })
        .catch((error) => {
            log("{loginByCredentialsApi}", error.response.data, error.message);
            throw error.message;
        });
};

export const loginByRefreshTokenApi: (refreshToken: string) => Promise<AuthenticationProps> = async (refreshToken) => {
    const __cfg: AxiosRequestConfig = {
        headers: {
            "Content-Type": "application/json",
            "refreshToken": refreshToken
        }
    };

    return __axios
        .post("login", null, __cfg)
        .then(async (response) => {
            return await parseLoginResponse(response);
        })
        .catch((error) => {
            log("{loginByRefreshTokenApi}", error.response.data, error.message);
            throw error.message;
        });
};

export const newAuthenticatedAxiosInstance:
    (
        authenticationProps: AuthenticationProps,
        onSuccess: (authenticationProps: AuthenticationProps) => Promise<void>,
        onFailure: () => Promise<void>
    ) => AxiosInstance = (authenticationProps, onSuccess, onFailure) => {
        let axiosInstance = axios.create({
            baseURL: `${environment.WEB_API_URL}/${authenticationProps.user.id}`
        });
        axiosInstance.interceptors.request
            .use(async (config) => {
                config.headers.common['Authorization'] = `${authenticationProps.tokenType} ${authenticationProps.accessToken}`;
                return config;
            }, async (error) => {
                if (error.response?.status !== 401) {
                    return Promise.reject(error);
                }

                loginByRefreshTokenApi(authenticationProps.refreshToken)
                    .then((result) => {
                        onSuccess(result);

                        // Redo original request.
                        axiosInstance = newAuthenticatedAxiosInstance(result, onSuccess, onFailure);
                        delete error.config['Authorization'];
                        axiosInstance.request(error.config);
                    })
                    .catch((error) => {
                        onFailure();
                        Promise.reject(error);
                    });
            });

        return axiosInstance;
    }

