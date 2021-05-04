export interface Constants {
  IS_PRODUCTION: boolean;
  WEB_API_URL: string;
  WEB_API_WS_URL: string;
  STORAGE_AUTHENTICATION_KEY: string;
}

export const constants: Constants =
  process.env.NODE_ENV === "production"
    ? (() => {
        let [domain, ...rest] = window.location.hostname.split(".");
        domain = `${domain}api`;
        const hostname = [domain, ...rest].join(".");
        return {
          IS_PRODUCTION: true,
          WEB_API_URL: `${window.location.protocol}//${hostname}/api`,
          WEB_API_WS_URL: `${window.location.protocol.replace(
            "http",
            "ws"
          )}//${hostname}/api`,
          STORAGE_AUTHENTICATION_KEY: "__AUTHENTICATION_PROPS__",
        };
      })()
    : {
        IS_PRODUCTION: false,
        WEB_API_URL: "http://localhost:5000/api",
        WEB_API_WS_URL: "ws://localhost:5000/api",
        STORAGE_AUTHENTICATION_KEY: "__AUTHENTICATION_PROPS__",
      };
