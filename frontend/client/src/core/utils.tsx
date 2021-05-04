import PropTypes from 'prop-types';
import { Plugins } from "@capacitor/core";
import { IS_PRODUCTION } from '../environment/constants';


const { Storage } = Plugins;

// TODO: use Application Insights from Azure to log when production is enabled
export const newLogger: (tag: string) =>
    (...args: any) =>
        void = tag =>
        (...args) => {
            if (!IS_PRODUCTION) {
                console.log(`[${tag}]`, ...args);
            }
        }

//#region Storage Base Functions

export async function storageSet(key: string, value: any) {
    await Storage.set({
        key: key,
        value: JSON.stringify(value)
    });
}

export async function storageGetByKeyPrefix<T>(keyPrefix: string): Promise<T[]> {
    const values: T[] = [];
    (await Storage.keys()).keys
        .forEach(async (key) => {
            if (key.startsWith(keyPrefix)) {
                const storedValue: string | null = (await Storage.get({ key: key })).value;
                if (storedValue) {
                    values.push(JSON.parse(storedValue) as T);
                }
            }
        });

    return values;
}

export async function storageClearByKeyPrefix(keyPrefix: string) {
    if (!keyPrefix) {
        await Storage.clear();
        return;
    }

    (await Storage.keys()).keys
        .forEach(async (key) => {
            if (key.startsWith(keyPrefix)) {
                await Storage.remove({ key: key });
            }
        });
}

//#endregion

export interface ReactNodeLikeProps {
    children: PropTypes.ReactNodeLike;
}
