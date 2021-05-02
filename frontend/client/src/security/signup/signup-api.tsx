import axios, { AxiosRequestConfig } from 'axios';
import { getConfig } from '../../config';
import { newLogger } from '../../core/utils';
import { UserSignup } from './signup';


const log = newLogger('security/signup/signup-api');
const __axios = axios.create({
    baseURL: getConfig().WEB_API_URL
});
const __axiosRequestConfig: AxiosRequestConfig = {
    headers: {
        'Content-Type': 'application/json'
    }
};

export const signupApi: (userSignup: UserSignup) => Promise<any> = async (userSignup) => {
    return __axios.post('/signup', userSignup, __axiosRequestConfig)
        .then(response => {
            log('{signupApi}', 'User created successfully.');
            return response.data;
        })
        .catch(error => {
            log('{signupApi}', error.response.status, error.response);
            throw error;
        });
}
