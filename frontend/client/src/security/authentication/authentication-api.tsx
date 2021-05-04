import axios, { AxiosRequestConfig } from 'axios';
import { newLogger, storageSet } from '../../core/utils';
import { Config } from '../../environment/config';
import { AuthenticationProps, UserAuthenticated, UserLogin } from './authentication';


const log = newLogger('security/authentication/authentication-api');
const __axios = axios.create({
    baseURL: Config.instance.appSettings.WEB_API_URL
});
const __axiosRequestConfig: AxiosRequestConfig = {
    headers: {
        'Content-Type': 'application/json'
    }
};

export const loginApi: (userLogin: UserLogin) => Promise<AuthenticationProps> = async (userLogin) => {
    return __axios.post('/login', userLogin, __axiosRequestConfig)
        .then(async (response) => {
            const authenticationProps: AuthenticationProps = {
                user: response.data as UserAuthenticated,
                tokenType: response.headers['tokentype'],
                accessToken: response.headers['accesstoken'],
                refreshToken: response.headers['refreshtoken']
            };

            await storageSet(Config.instance.appSettings.STORAGE_AUTHENTICATION_KEY, authenticationProps);
            return authenticationProps;
        })
        .catch(error => {
            log('{loginApi}', error.response.data, error.message);
            throw error.message;
        });
}
