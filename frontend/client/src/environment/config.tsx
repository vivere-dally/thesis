import axios, { AxiosInstance } from "axios";
import axiosRetry from "axios-retry";
import { newLogger } from "../core/utils";
import { CONFIG_API_URL } from "./constants";

const log = newLogger("environment");

const neededAppSettings: string[] = ["WEB_API_URL", "WEB_API_WS_URL"];
export interface AppSettings {
  [key: string]: string;

  WEB_API_URL: string;
  WEB_API_WS_URL: string;
  STORAGE_AUTHENTICATION_KEY: string;
}

const neededConnStrings: [string, string][] = [];
export interface ConnStrings {
  [key: string]: string;
}

export class Config {
  private static __instance: Config = new Config();

  private __axios: AxiosInstance;
  private __fetchPromise: Promise<void>;

  private __appSettings: AppSettings = {
    WEB_API_URL: "http://localhost:8080/api",
    WEB_API_WS_URL: "ws://localhost:8080/api",
    STORAGE_AUTHENTICATION_KEY: "__AUTHENTICATION_PROPS__",
  };

  private __connStrings: ConnStrings = {};

  private constructor() {
    this.__axios = axios.create();
    axiosRetry(this.__axios, {
      retries: 7,
      retryDelay: (retryCount) => {
        log(`Retry count: ${retryCount}`);
        return 2000;
      },
      retryCondition: () => {
        return true;
      },
    });

    this.__fetchPromise = this.__fetch();
  }

  /**
   * instance
   * @returns the instance
   * @type {Config}
   */
  public static get instance(): Config {
    return this.__instance;
  }

  /**
   * appSettings
   * @returns the appSettings
   * @type {AppSettings}
   */
  public get appSettings(): Promise<AppSettings> {
    return this.__fetchPromise.then(() => this.__appSettings);
  }

  /**
   * connStrings
   * @returns the connString
   * @type {ConnStrings}
   */
  public get connStrings(): Promise<ConnStrings> {
    return this.__fetchPromise.then(() => this.__connStrings);
  }

  /**
   * fetch the remote configuration
   */
  private async __fetch(): Promise<void> {
    try {
      const response = await this.__axios
        .post(
          CONFIG_API_URL,
          {
            appSettings: neededAppSettings,
            connStrings: neededConnStrings,
          },
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        )
        .then((response) => {
          log("{fetch}", "Fetched config successfully.");
          log(response.data);
          return response.data;
        })
        .catch((error) => {
          log("{fetch}", JSON.stringify(error));
          throw error;
        });

      for (const appSetting of neededAppSettings) {
        this.__appSettings[appSetting] = response.appSettings[appSetting];
      }

      for (const connString of neededConnStrings) {
        this.__connStrings[connString[0]] = response.connStrings[connString[0]];
      }
    } catch (error) {
      log(
        "{fetch}",
        "Could not fetch configs after 7 retries. Using default.",
        JSON.stringify(error)
      );
    }
  }
}
