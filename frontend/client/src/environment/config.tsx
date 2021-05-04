import axios from "axios";
import axiosRetry from "axios-retry";
import { newLogger } from "../core/utils";

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

  private __fetched: boolean = false;
  private __appSettings: AppSettings = {
    WEB_API_URL: "http://localhost:8080/api",
    WEB_API_WS_URL: "ws://localhost:8080/api",
    STORAGE_AUTHENTICATION_KEY: "__AUTHENTICATION_PROPS__",
  };

  private __connStrings: ConnStrings = {};

  private constructor() {
    axiosRetry(axios, {
      retries: 7,
      retryDelay: (retryCount) => {
        log(`Retry count: ${retryCount}`);
        return 2000;
      },
      retryCondition: () => {
        return true;
      },
    });
  }

  /**
   * instance
   * @returns the instance of @type {Config}
   */
  public static get instance(): Config {
    return this.__instance;
  }

  /**
   * appSettings
   * @returns the appSettings of @type {AppSettings}
   */
  public get appSettings(): AppSettings {
    log('{appSettings}', this.__fetched);
    return this.__appSettings;
  }

  /**
   * connStrings
   * @returns the connStrings of @type {ConnStrings}
   */
  public get connStrings(): ConnStrings {
    return this.__connStrings;
  }

  /**
   * fetch the remote configuration
   */
  public async fetch(): Promise<void> {
    if (this.__fetched) {
      return;
    }

    try {
      const response = await axios
        .post(
          "http://localhost:3000/api/config",
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

      this.__fetched = true;
    } catch (error) {
      log(
        "{fetch}",
        "Could not fetch configs after 7 retries. Using default.",
        JSON.stringify(error)
      );
    }
  }
}
