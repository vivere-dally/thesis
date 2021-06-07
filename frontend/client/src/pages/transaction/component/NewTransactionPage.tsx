import { IonBackButton, IonButton, IonButtons, IonCol, IonContent, IonDatetime, IonHeader, IonInput, IonItem, IonLabel, IonPage, IonRow, IonSelect, IonSelectOption, IonTitle, IonToolbar } from "@ionic/react";
import React, { useContext, useEffect, useState } from "react";
import { RouteComponentProps } from "react-router";
import { toast, ToastContainer } from "react-toastify";
import { newLogger } from "../../../core/utils";
import { AuthenticationContext } from "../../../security/authentication/authentication-provider";
import { Transaction, TransactionType } from "../transaction";
import { postTransactionApi } from "../transaction-api";
import "./NewTransactionPage.scss";


const log = newLogger('pages/transacation/component/NewTransactionPage');

interface NewTransactionPageProps extends RouteComponentProps<{
    id?: string;
}> { }
const NewTransactionPage: React.FC<NewTransactionPageProps> = ({ history, match }) => {

    // Contexts
    const { axiosInstance } = useContext(AuthenticationContext);

    // States
    const [accountId, setAccountId] = useState<number>();
    const [message, setMessage] = useState<string>('');
    const [value, setValue] = useState<number>(0.0);
    const [type, setType] = useState<TransactionType>(TransactionType.INCOME);
    const [date, setDate] = useState<string>(new Date().toISOString());

    // Effects
    useEffect(() => {
        log('{useEffect}', '(accountId)', 'start');
        const routeId = Number(match.params.id || '');
        setAccountId(routeId);
    }, [match.params.id]);

    return (
        <IonPage id='new-transaction-page'>
            <IonHeader>
                <IonToolbar>
                    <IonButtons>
                        <IonBackButton text='Transactions' />
                    </IonButtons>
                </IonToolbar>
                <IonToolbar>
                    <IonTitle id='new_transaction-title'>New transaction</IonTitle>
                </IonToolbar>
            </IonHeader>

            <IonContent fullscreen>
                <form onSubmit={handleNewTransaction}>
                    <IonItem>
                        <IonLabel position="floating">Message</IonLabel>
                        <IonInput
                            type="text"
                            maxlength={255}
                            required
                            value={message}
                            onIonChange={e => setMessage(e.detail.value || '')}
                            id="message-text-input"
                        />
                    </IonItem>

                    <IonItem>
                        <IonLabel position="floating">Value</IonLabel>
                        <IonInput
                            type="number"
                            step="0.01"
                            min="0"
                            required
                            value={value}
                            onIonChange={e => setValue(parseFloat(e.detail.value || "0.0"))}
                            id="value-number-input"
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

                    <IonRow>
                        <IonCol className="ion-text-center">
                            <IonButton type="submit" expand="block" id="create-submit-button">Create</IonButton>
                        </IonCol>
                    </IonRow>
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
        if (value <= 0) {
            log('{handleNewTransaction}', 'invalid data');
            toast.warning("The value must be greater than 0.");
            return;
        }

        const transaction: Transaction = {
            message: message,
            value: value,
            type: type,
            date: date
        }

        postTransactionApi(axiosInstance!, accountId!, transaction)
            .then(() => {
                log('{handleNewTransaction}', 'sucess');
                toast.success("Transaction created successfully.",
                    {
                        onClose: () => {
                            history.goBack();
                        }
                    });
            })
            .catch((error) => {
                log('{handleNewTransaction}', 'error');
                if (error) {
                    if (error.message) {
                        toast.error(error.message);
                    }
                    else {
                        toast.error(error);
                    }
                }
                else {
                    toast.error("Could not create the transaction.");
                }
            })
    }
}

export default NewTransactionPage;
