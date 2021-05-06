import { IonBackButton, IonButtons, IonHeader, IonItemDivider, IonLabel, IonPage, IonTitle, IonToolbar } from "@ionic/react";
import React, { useContext, useEffect, useState } from "react";
import { RouteComponentProps } from "react-router";
import { newLogger } from "../../../core/utils";
import { AuthenticationContext } from "../../../security/authentication/authentication-provider";
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
    }, [data, match.params.id])

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
        </IonPage>
    )
}

export default AccountFeedPage;
