import { IonBackButton, IonButtons, IonContent, IonFab, IonFabButton, IonHeader, IonIcon, IonLabel, IonPage, IonTitle, IonToolbar } from "@ionic/react";
import { add } from "ionicons/icons";
import React, { useContext, useEffect, useState } from "react";
import { RouteComponentProps } from "react-router";
import { ToastContainer } from "react-toastify";
import { newLogger } from "../../../core/utils";
import { AuthenticationContext } from "../../../security/authentication/authentication-provider";
import TransactionFeed from "../../transaction/component/TransactionFeed";
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
                    <IonTitle className="ion-text-center">
                        <IonLabel>{authenticationProps?.user.username}</IonLabel><br />
                        <IonLabel>{account?.money} {account?.currency}</IonLabel><br />
                    </IonTitle>
                </IonToolbar>
                <IonToolbar>
                    <IonButtons slot="end">
                        <IonLabel>Monthly income: {account?.monthlyIncome} {account?.currency}</IonLabel>
                    </IonButtons>
                </IonToolbar>
            </IonHeader>

            <IonContent fullscreen>
                <TransactionFeed accountId={account?.id!} currencyType={account?.currency!} />

                <IonFab slot='fixed' vertical='bottom' horizontal='end'>
                    <IonFabButton onClick={() => history.push('/transaction-new')}>
                        <IonIcon icon={add} />
                    </IonFabButton>
                </IonFab>

                <ToastContainer
                    position="bottom-center"
                    autoClose={2000}
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
