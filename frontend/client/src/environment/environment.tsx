
const common = {
    STORAGE_REFRESH_TOKEN_KEY: "__REFRESH_TOKEN__",
    PAGE_SIZE: 10
}

export const environment =
    (!window.location.hostname.includes('localhost')) ?
        (
            () => {
                let [domain, ...rest] = window.location.hostname.split(".");
                domain = `${domain}api`;
                const hostname = [domain, ...rest].join(".");

                return {
                    IS_PRODUCTION: true,
                    WEB_API_URL: `${window.location.protocol}//${hostname}/api`,
                    WEB_API_WS_URL: `${window.location.protocol.replace("http", "ws")}//${hostname}/api`,
                    TOAST_TIME_IN_SECONDS: 3,
                    ...common
                }
            }
        )() : {
            IS_PRODUCTION: false,
            WEB_API_URL: "http://localhost:5000/api",
            WEB_API_WS_URL: "ws://localhost:5000/api",
            TOAST_TIME_IN_SECONDS: 1,
            ...common
        };
