export const IS_PRODUCTION = Boolean(process.env.NODE_ENV === "production");
export const CONFIG_API_URL =
  process.env.NODE_ENV === "production"
    ? "frontend_config_provider://localhost:3000/api/config"
    : "http://localhost:3000/api/config";