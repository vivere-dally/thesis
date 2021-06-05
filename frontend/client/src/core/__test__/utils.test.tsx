import { storageClearByKeyPrefix, storageGetByKeyPrefix, storageSet } from '../utils'
import { Plugins } from "@capacitor/core";
import { when } from 'jest-when';

const { Storage } = Plugins;

describe('Storage CRUD', () => {

    afterEach(() => {
        jest.restoreAllMocks();
    })

    it('storageSet', async () => {
        const key = 'key';
        const value = { testProperty: 'testProperty' };
        const storage: { [k: string]: string } = {};
        jest.spyOn(Storage, 'set')
            .mockImplementation((options: { key: string, value: string }) => {
                storage[options.key] = JSON.stringify(value);
                return Promise.resolve();
            });

        await storageSet(key, value);

        expect(Storage.set).toBeCalledTimes(1);
        expect(Storage.set).toBeCalledWith({ key: key, value: JSON.stringify(value) });
        expect(storage).toHaveProperty(key);
    });

    it('storageGetByKeyPrefix', async () => {
        const keys = ['key1', 'key2', 'definitelyNotAKey'];
        const values = [
            { testProperty: 'testProperty_key1' },
            { testProperty: 'testProperty_key1' },
            { testProperty: 'testProperty_definitelyNotAKey' }
        ];

        jest.spyOn(Storage, 'keys')
            .mockImplementation(() => Promise.resolve<{ keys: string[] }>({ keys: keys }));
        const spiedGet = jest.spyOn(Storage, 'get');
        for (let index = 0; index < keys.length; index++) {
            when(spiedGet)
                .calledWith({ key: keys[index] })
                .mockReturnValue(Promise.resolve<{ value: string | null }>({ value: JSON.stringify(values[index]) }));
        }

        const actual = await storageGetByKeyPrefix<any>('key');
        expect(Storage.keys).toBeCalledTimes(1);
        expect(Storage.get).toBeCalledTimes(2);
        expect(actual).toEqual(values.slice(0, -1));
    });

    it('storageClearByKeyPrefix', async () => {
        const keys = ['key1', 'key2', 'definitelyNotAKey'];
        const expected = ['definitelyNotAKey'];
        jest.spyOn(Storage, 'keys')
            .mockImplementation(() => Promise.resolve<{ keys: string[] }>({ keys: [...keys] }));
        const spiedRemove = jest.spyOn(Storage, 'remove');
        for (let index = 0; index < keys.length; index++) {
            when(spiedRemove)
                .calledWith({ key: keys[index] })
                .mockImplementation((options: { key: string }) => {
                    const index = keys.findIndex(it => it.startsWith(options.key));
                    if (index !== -1) {
                        keys.splice(index, 1);
                    }

                    return Promise.resolve();
                });
        }

        await storageClearByKeyPrefix('key');
        expect(Storage.keys).toBeCalledTimes(1);
        expect(Storage.remove).toBeCalledTimes(2);
        expect(keys).toEqual(expected);
    })
});
