import { IonBackButton, IonButton, IonButtons, IonCol, IonContent, IonHeader, IonInput, IonItem, IonLabel, IonPage, IonRow, IonSelect, IonSelectOption, IonText, IonTitle, IonToolbar } from "@ionic/react";
import React, { useContext, useState } from "react";
import { RouteComponentProps } from "react-router";
import { toast, ToastContainer } from "react-toastify";
import { newLogger } from "../../../core/utils";
import { Account, CurrencyType } from "../account";
import { AccountContext } from "../account-provider";
import "./NewAccountPage.scss";


const log = newLogger('pages/account/component/NewAccountPage');


const NewAccountPage: React.FC<RouteComponentProps> = ({ history }) => {

    // Contexts
    const { post } = useContext(AccountContext);

    // States
    const [currencyType, setCurrencyType] = useState<CurrencyType>(CurrencyType.RON);
    const [money, setMoney] = useState<number>(0.0);
    const [monthlyIncome, setMonthlyIncome] = useState<number>(0.0);

    return (
        <IonPage id='new-account-page'>
            <IonHeader>
                <IonToolbar>
                    <IonButtons>
                        <IonBackButton text='Accounts' defaultHref='/account' />
                    </IonButtons>
                    <IonTitle>New account</IonTitle>
                </IonToolbar>
            </IonHeader>

            <IonContent fullscreen>
                <form onSubmit={handleNewAccount}>
                    <IonItem>
                        <IonLabel position="floating">Currency</IonLabel>
                        <IonSelect
                            value={currencyType}
                            onIonChange={e => setCurrencyType(e.detail.value)}
                        >
                            {
                                Object
                                    .keys(CurrencyType)
                                    .map((key, index) =>
                                        <IonSelectOption key={index} value={key}>{key}</IonSelectOption>
                                    )
                            }
                        </IonSelect>
                    </IonItem>

                    <IonItem>
                        <IonLabel position="floating">Money</IonLabel>
                        <IonInput
                            type="number"
                            step="0.01"
                            min="0"
                            required
                            value={money}
                            onIonChange={e => setMoney(parseFloat(e.detail.value || "0.0"))}
                        />
                    </IonItem>

                    <IonItem>
                        <IonLabel position="floating">Monthly Income</IonLabel>
                        <IonInput
                            type="number"
                            step="0.01"
                            min="0"
                            required
                            value={monthlyIncome}
                            onIonChange={e => setMonthlyIncome(parseFloat(e.detail.value || "0.0"))}
                        />
                    </IonItem>

                    <IonRow>
                        <IonCol className="ion-text-center">
                            <IonButton type="submit" expand="block">Create</IonButton>
                        </IonCol>
                    </IonRow>
                </form>

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

    function handleNewAccount(e: React.FormEvent) {
        log('{handleNewAccount}', 'start');
        e.preventDefault();
        if (money > 0.0 && monthlyIncome > 0.0) {
            const account: Account = {
                currency: currencyType,
                money: money,
                monthlyIncome: monthlyIncome
            }

            post && post(account)
                .then(() => {
                    log('{handleNewAccount}', 'sucess');
                    toast.success("Account created successfully.",
                        {
                            onClose: () => {
                                history.push("/account");
                            }
                        });
                })
                .catch((error) => {
                    log('{handleNewAccount}', 'error');
                    if (error) {
                        if (error.message) {
                            toast.error(error.message);
                        }
                        else {
                            toast.error(error);
                        }
                    }
                    else {
                        toast.error("Could not create the account.");
                    }
                });
        }
        else {
            log('{handleNewAccount}', 'invalid data');
            toast.warning("The money and monthly income must be greater than 0.")
        }
    }
}

export default NewAccountPage;
