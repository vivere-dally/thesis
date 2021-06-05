import { IonItem, IonLabel } from "@ionic/react";
import { memo } from "react";
import { CurrencyType } from "../../account/account";
import { Transaction } from "../transaction";
import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";


dayjs.extend(relativeTime);


export interface TransactionFeedItemProps {
    currency: CurrencyType;
    transaction: Transaction;
}

const TransactionFeedItem: React.FC<TransactionFeedItemProps> = memo(({ currency, transaction }) => {
    return (
        <IonItem>
            <IonLabel className="ion-text-wrap">
                <h6>
                    {
                        dayjs(transaction.date).isBefore(dayjs().subtract(1, 'day')) ? (
                            dayjs(transaction.date).format('DD.MM.YYYY')
                        ) : (
                            dayjs(transaction.date).fromNow()
                        )
                    }
                </h6>
                <h3 style={{ fontWeight: "bolder" }} >{transaction.message}</h3>
                <h3 className="ion-text-end">
                    {
                        (() => {
                            switch (transaction.type) {
                                case "INCOME":
                                    return <span style={{ color: "green", fontWeight: "bold" }}>{transaction.value} {currency}</span>
                                case "EXPENSE":
                                    return <span style={{ fontWeight: "bold" }}>-{transaction.value} {currency}</span>
                                default:
                                    return <span></span>
                            }
                        })()
                    }
                </h3>
            </IonLabel>
        </IonItem>
    );
});

export default TransactionFeedItem;
