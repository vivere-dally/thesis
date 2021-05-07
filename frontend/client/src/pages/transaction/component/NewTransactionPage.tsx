import { IonBackButton, IonButtons, IonContent, IonDatetime, IonHeader, IonInput, IonItem, IonLabel, IonPage, IonSelect, IonSelectOption, IonToolbar } from "@ionic/react";
import React, { useContext, useState } from "react";
import { RouteComponentProps } from "react-router";
import { ToastContainer } from "react-toastify";
import { newLogger } from "../../../core/utils";
import { AuthenticationContext } from "../../../security/authentication/authentication-provider";
import { TransactionType } from "../transaction";
import "./NewTransactionPage.scss";


const log = newLogger('pages/transacation/component/NewTransactionPage');


const NewTransactionPage: React.FC<RouteComponentProps> = ({ }) => {

    // Contexts
    const { axiosInstance } = useContext(AuthenticationContext);

    // States
    const [value, setValue] = useState<number>(0.0);
    const [type, setType] = useState<TransactionType>(TransactionType.EXPENSE);
    const [date, setDate] = useState<string>(new Date().toISOString());

    return (
        <IonPage id='new-transaction-page'>
            <IonHeader>
                <IonToolbar>
                    <IonButtons>
                        <IonBackButton text='Transactions' />
                    </IonButtons>
                    <IonToolbar>New transaction</IonToolbar>
                </IonToolbar>
            </IonHeader>

            <IonContent fullscreen>
                <form onSubmit={handleNewTransaction}>
                    <IonItem>
                        <IonLabel position="floating">Value</IonLabel>
                        <IonInput
                            type="number"
                            step="0.01"
                            min="0"
                            required
                            value={value}
                            onIonChange={e => setValue(parseFloat(e.detail.value || "0.0"))}
                        />
                    </IonItem>

                    <IonItem>
                        <IonLabel position="floating">Transaction type</IonLabel>
                        <IonSelect
                            value={type}
                            onIonChange={e => setType(e.detail.value)}
                        >
                            {
                                Object
                                    .keys(TransactionType)
                                    .map((key, index) =>
                                        <IonSelectOption key={index} value={key}>{key}</IonSelectOption>
                                    )
                            }
                        </IonSelect>
                    </IonItem>

                    <IonItem>
                        <IonLabel position="floating">Date</IonLabel>
                        <IonDatetime
                            pickerFormat="MMM DD, YYYY HH:mm"
                            displayFormat="MMM DD, YYYY HH:mm"
                            value={date}
                            onIonChange={e => setDate((e.detail.value) ? new Date(e.detail.value).toISOString() : new Date().toISOString())}
                        />
                    </IonItem>
                </form>
            </IonContent>

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
        </IonPage>
    )

    function handleNewTransaction(e: React.FormEvent) {
        log('{handleNewTransaction}', 'start');
        e.preventDefault();

    }
}

export default NewTransactionPage;
