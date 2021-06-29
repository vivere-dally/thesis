import { IonBackButton, IonButtons, IonContent, IonFab, IonFabButton, IonHeader, IonIcon, IonLabel, IonPage, IonSearchbar, IonSelect, IonSelectOption, IonToolbar } from "@ionic/react";
import { add, barChartOutline, closeCircleOutline } from "ionicons/icons";
import React, { useContext, useEffect, useState } from "react";
import { RouteComponentProps } from "react-router";
import { ToastContainer } from "react-toastify";
import { MyModal } from "../../../core/component/MyModal";
import { newLogger } from "../../../core/utils";
import { AuthenticationContext } from "../../../security/authentication/authentication-provider";
import TransactionFeed from "../../transaction/component/TransactionFeed";
import TransactionSumsPerMonthChart from "../../transaction/component/TransactionSumsPerMonthChart";
import { TransactionType } from "../../transaction/transaction";
import { Account } from "../account";
import { AccountContext } from "../account-provider";
import "./AccountFeedPage.scss";


const log = newLogger('pages/account/component/AccountFeedPage');


interface AccountFeedPageProps extends RouteComponentProps<{
    id?: string;
}> { }
const AccountFeedPage: React.FC<AccountFeedPageProps> = ({ history, match }) => {

    // Contexts
    const { authenticationProps } = useContext(AuthenticationContext);
    const { data } = useContext(AccountContext);

    // States
    const [account, setAccount] = useState<Account>();
    const [message, setMessage] = useState<string>();
    const [transactionType, setTransactionType] = useState<TransactionType>();

    // Effects
    useEffect(() => {
        log('{useEffect}', 'start');
        const routeId = Number(match.params.id || '');
        const __account = data?.find(it => it.id === routeId);
        setAccount(__account);
    }, [data, match.params.id]);

    return (
        <IonPage id='account-feed-page'>
            <IonHeader>
                <IonToolbar>
                    <IonButtons>
                        <IonBackButton text='Accounts' defaultHref='/account' />
                    </IonButtons>
                </IonToolbar>
                <IonToolbar>
                    <div className="ion-text-center" style={{ fontWeight: "bold" }} id='account_feed-title'>
                        <IonLabel>{authenticationProps?.user.username}</IonLabel><br />
                        <IonLabel>{account?.money} {account?.currency} {(() => {
                            if (account?.monthlyIncome! > 0) {
                                return <span style={{ color: "green" }}>({account?.monthlyIncome} &uarr;)</span>
                            }

                            return <span style={{ color: "red" }}>({account?.monthlyIncome} &darr;)</span>
                        })()}</IonLabel><br />
                    </div>
                </IonToolbar>
                <IonToolbar>
                    <IonButtons slot="start">
                        <IonSelect
                            value={String(transactionType)}
                            onIonChange={e => setTransactionType(e.detail.value == 'undefined' ? undefined : e.detail.value)}
                        >
                            {
                                (() => {
                                    const options = [<IonSelectOption key={0} value={'undefined'}>ALL</IonSelectOption>];
                                    options.push(
                                        ...Object
                                            .keys(TransactionType)
                                            .map((key, index) =>
                                                <IonSelectOption key={index + 1} value={key}>{key}</IonSelectOption>
                                            ));

                                    return options;
                                })()
                            }
                        </IonSelect>
                    </IonButtons>
                    <IonSearchbar value={message} onIonChange={e => { setMessage(e.detail.value ? e.detail.value : undefined); }}></IonSearchbar>
                    <IonButtons slot="end">
                        <MyModal openModalIcon={barChartOutline} closeModalIcon={closeCircleOutline}>
                            <TransactionSumsPerMonthChart account={account!} username={authenticationProps?.user.username!} />
                        </MyModal>
                    </IonButtons>
                </IonToolbar>
            </IonHeader>

            <IonContent fullscreen id='account_feed_page-ion_content'>
                <TransactionFeed accountId={account?.id!} currencyType={account?.currency!} message={message} transactionType={transactionType} />

                <IonFab slot='fixed' vertical='bottom' horizontal='end'>
                    <IonFabButton onClick={() => history.push(`${account?.id!}/transaction-new`)} id='new_transaction-button'>
                        <IonIcon icon={add} />
                    </IonFabButton>
                </IonFab>

                <ToastContainer
                    position="bottom-center"
                    hideProgressBar={false}
                    newestOnTop={false}
                    rtl={false}
                    pauseOnFocusLoss
                    draggable
                    pauseOnHover
                />
            </IonContent>
        </IonPage>
    )
}

export default AccountFeedPage;
