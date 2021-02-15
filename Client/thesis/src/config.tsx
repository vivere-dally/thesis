export interface ClientConfig {
    WEB_API_URL: string;
    WEB_API_WS_URL: string;
    STORAGE_AUTHENTICATION_KEY: string;
};

export const IsProduction = false;
const __developmentConfig: ClientConfig = {
    WEB_API_URL: 'http://localhost:8080/api',
    WEB_API_WS_URL: 'ws://localhost:8080/api',
    STORAGE_AUTHENTICATION_KEY: '__AUTHENTICATION_PROPS__'
};

const __productionConfig: ClientConfig = {
    ...__developmentConfig
};

export const getConfig: () => ClientConfig = () => {
    if (IsProduction) {
        return __productionConfig;
    }

    return __developmentConfig;
}
