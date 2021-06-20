import { IonInfiniteScroll, IonInfiniteScrollContent, IonList } from "@ionic/react";
import { memo, useContext, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { newLogger } from "../../../core/utils";
import { environment } from "../../../environment/environment";
import { AuthenticationContext } from "../../../security/authentication/authentication-provider";
import { CurrencyType } from "../../account/account";
import { Transaction } from "../transaction";
import { getTransactionApi } from "../transaction-api";
import TransactionFeedItem from "./TransactionFeedItem";
import './TransactionFeed.scss'


const log = newLogger('pages/account/component/TransactionFeed')


export interface TransactionFeedProps {
    accountId: number,
    currencyType: CurrencyType
}

const TransactionFeed: React.FC<TransactionFeedProps> = memo(({ accountId, currencyType }) => {

    // Contexts
    const { axiosInstance } = useContext(AuthenticationContext);

    // States
    const [data, setData] = useState<Transaction[]>([]);
    const [page, setPage] = useState<number>(1);

    useEffect(() => {
        if (!accountId) {
            return;
        }

        let cancelled = false;
        __get(cancelled);
        return () => {
            cancelled = true;
        }
    }, [accountId]);

    return (
        <>
            <IonList>
                {
                    data.map((transaction) => {
                        return <TransactionFeedItem key={transaction.id!} currency={currencyType} transaction={transaction} />
                    })
                }
            </IonList>
            <IonInfiniteScroll threshold="10px" onIonInfinite={(e: CustomEvent<void>) => __handleIonInfinite(e)}>
                <IonInfiniteScrollContent loadingText="Loading transactions" />
            </IonInfiniteScroll>
        </>
    );

    async function __get(cancelled?: boolean): Promise<Transaction[] | void> {
        log('{__get}', 'start');

        return getTransactionApi(axiosInstance!, accountId, page)
            .then((result) => {
                log('{__get}', 'success');
                if (!cancelled) {
                    setData([...data, ...result]);
                    setPage(page + 1);
                }

                return result;
            })
            .catch((error) => {
                log('{__get}', 'failure');
                toast.error(error);
            })
    }

    async function __handleIonInfinite(e: CustomEvent<void>) {
        const result = await __get();
        if (result && result.length < environment.PAGE_SIZE) {
            (e.target as HTMLIonInfiniteScrollElement).disabled = true;
        }

        (e.target as HTMLIonInfiniteScrollElement).complete();
    }
});

export default TransactionFeed;
