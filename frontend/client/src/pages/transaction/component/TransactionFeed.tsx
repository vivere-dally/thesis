import { IonInfiniteScroll, IonInfiniteScrollContent, IonList } from "@ionic/react";
import { useContext, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { newLogger } from "../../../core/utils";
import { environment } from "../../../environment/environment";
import { AuthenticationContext } from "../../../security/authentication/authentication-provider";
import { CurrencyType } from "../../account/account";
import { Transaction, TransactionType } from "../transaction";
import { getTransactionApi } from "../transaction-api";
import TransactionFeedItem from "./TransactionFeedItem";
import './TransactionFeed.scss'


const log = newLogger('pages/account/component/TransactionFeed')


export interface TransactionFeedProps {
    accountId: number,
    currencyType: CurrencyType,
    message: string | undefined,
    transactionType: TransactionType | undefined;
}

const TransactionFeed: React.FC<TransactionFeedProps> = ({ accountId, currencyType, message, transactionType }) => {

    // Contexts
    const { axiosInstance } = useContext(AuthenticationContext);

    // States
    const [data, setData] = useState<Transaction[]>([]);
    const [page, setPage] = useState<number>(0);

    useEffect(() => {
        if (!accountId) { return; }
        let cancelled = false;
        __get(false, cancelled);
        return () => {
            cancelled = true;
        }
    }, [accountId]);

    useEffect(() => {
        if (!accountId) { return; }
        let cancelled = false;
        __get(true, cancelled);
        return () => {
            cancelled = true;
        }
    }, [message, transactionType])

    log('render');
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

    async function __get(reset: boolean, cancelled?: boolean): Promise<Transaction[] | void> {
        log('{__get}', 'start');

        const pageToRequest = reset ? 0 : page;
        return getTransactionApi(axiosInstance!, accountId, pageToRequest, message, transactionType)
            .then((result) => {
                log('{__get}', 'success');
                if (!cancelled) {
                    if (reset) {
                        setData(result);
                        setPage(1);
                    }
                    else {
                        setData([...data, ...result]);
                        setPage(page + 1);
                    }
                }

                return result;
            })
            .catch((error) => {
                log('{__get}', 'failure');
                toast.error(error);
            })
    }

    async function __handleIonInfinite(e: CustomEvent<void>) {
        const result = await __get(false);
        if (result && result.length < environment.PAGE_SIZE) {
            (e.target as HTMLIonInfiniteScrollElement).disabled = true;
        }

        (e.target as HTMLIonInfiniteScrollElement).complete();
    }

    async function __reset() {
        setData([]);
        setPage(0);
    }
};

export default TransactionFeed;
