import { IonButton, IonButtons, IonContent, IonFab, IonFabButton, IonHeader, IonIcon, IonItem, IonLabel, IonList, IonLoading, IonPage, IonTitle, IonToolbar } from "@ionic/react";
import { add } from "ionicons/icons";
import React, { useContext, useEffect } from "react";
import { RouteComponentProps } from "react-router";
import { toast, ToastContainer } from "react-toastify";
import { ActionType } from "../../../core/entity";
import { newLogger } from "../../../core/utils";
import { environment } from "../../../environment/environment";
import { AuthenticationContext } from "../../../security/authentication/authentication-provider";
import { AccountContext } from "../account-provider";
import "./AccountPage.scss";


const log = newLogger('pages/account/component/AccountPage');


const AccountPage: React.FC<RouteComponentProps> = ({ history }) => {
    const { logout } = useContext(AuthenticationContext);
    const { data, executing, actionType, actionError } = useContext(AccountContext);

    // Effects
    useEffect(() => {
        if (actionError) {
            toast.error(actionError.message, { autoClose: environment.TOAST_TIME_IN_SECONDS });
        }
    }, [actionError]);

    return (
        <IonPage id='account-page'>
            <IonHeader>
                <IonToolbar>
                    <IonButtons slot="end">
                        <IonButton onClick={handleLogout} id="logout-button">Logout</IonButton>
                    </IonButtons>
                    <IonTitle id='accounts-title'>Accounts</IonTitle>
                </IonToolbar>
            </IonHeader>

            <IonContent fullscreen>
                <IonLoading isOpen={executing && actionType === ActionType.GET} message='Fetching accounts' />

                {
                    !executing && data && (<div>
                        <IonList>
                            {
                                data
                                    .sort((a, b) => { return a.id! - b.id!; })
                                    .map((account) =>
                                        <IonItem key={account.id} routerLink={`/account/${account.id}`}>
                                            <IonLabel>{account.money} {account.currency}</IonLabel>
                                        </IonItem>
                                    )
                            }
                        </IonList>
                    </div>)
                }

                <IonFab slot='fixed' vertical='bottom' horizontal='end'>
                    <IonFabButton onClick={() => history.push('/account-new')} id="new_account-button">
                        <IonIcon icon={add} />
                    </IonFabButton>
                </IonFab>

                <ToastContainer
                    position="bottom-center"
                    //autoClose={environment.TOAST_TIME_IN_SECONDS}
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

    function handleLogout() {
        log('{handleLogout}', 'start');
        logout && logout();
        log('{handleLogout}', 'sucess');
    }
}

export default AccountPage;
