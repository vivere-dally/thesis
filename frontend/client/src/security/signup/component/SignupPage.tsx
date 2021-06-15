import { IonBackButton, IonButton, IonButtons, IonCol, IonContent, IonHeader, IonInput, IonItem, IonLabel, IonList, IonLoading, IonPage, IonRow, IonText, IonTitle, IonToolbar } from "@ionic/react";
import React, { useContext, useState } from "react";
import { RouteComponentProps } from "react-router";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { environment } from "../../../environment/environment";
import { SignupContext } from "../signup-provider";
import './SignupPage.scss';


const __usernameRegex = new RegExp('^[a-zA-Z0-9_-]{3,16}$', 'i');
const __passwordRegex = new RegExp('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\^&\*])(?=.{8,})');

const SignupPage: React.FC<RouteComponentProps> = ({ history }) => {
    const [username, setUsername] = useState<string>('');
    const [isUsernameValid, setIsUsernameValid] = useState<boolean>(false);
    const [password, setPassword] = useState<string>('');
    const [isPasswordValid, setIsPasswordValid] = useState<boolean>(false);
    const [formSubmitted, setFormSubmitted] = useState<boolean>(false);

    const { executing, signup } = useContext(SignupContext);

    return (
        <IonPage id='signup-page'>
            <IonHeader>
                <IonToolbar>
                    <IonButtons>
                        <IonBackButton text='Login' defaultHref='/login' />
                    </IonButtons>
                    <IonTitle id='signup-title'>Signup</IonTitle>
                </IonToolbar>
            </IonHeader>

            <IonContent fullscreen>
                <IonLoading isOpen={executing} message="Signing up..." />

                <form noValidate onSubmit={__handleSignup}>
                    <IonList>
                        <IonItem>
                            <IonLabel position={"floating"}>Username</IonLabel>
                            <IonInput
                                type="text"
                                value={username}
                                onIonChange={e => setUsername(e.detail.value!)}
                                required
                                id="username-text-input"
                            />
                        </IonItem>
                        {
                            formSubmitted && !isUsernameValid &&
                            <IonText color="danger">
                                {
                                    <p className="ion-padding-start">
                                        Use between 3 and 16 characters with a mix of letters, numbers, dashes and underscores
                                    </p>
                                }
                            </IonText>
                        }

                        <IonItem>
                            <IonLabel position={"floating"}>Password</IonLabel>
                            <IonInput
                                type="password"
                                value={password}
                                onIonChange={e => setPassword(e.detail.value!)}
                                required
                                id="password-password-input"
                            />
                        </IonItem>
                        {
                            formSubmitted && !isPasswordValid &&
                            <IonText color="danger">
                                <p className="ion-padding-start">
                                    Use 8 or more characters with a mix of letters, numbers and symbols
                                </p>
                            </IonText>
                        }
                    </IonList>

                    <IonRow>
                        <IonCol>
                            <IonButton type={"submit"} expand={"block"} id="signup-submit-button">Signup</IonButton>
                        </IonCol>
                    </IonRow>
                </form>
            </IonContent>

            <ToastContainer
                position="bottom-center"
                hideProgressBar={false}
                newestOnTop={false}
                rtl={false}
                pauseOnFocusLoss
                draggable
                pauseOnHover
            />
        </IonPage>
    );

    function __validateUsername(): boolean {
        const result = __usernameRegex.test(username);
        setIsUsernameValid(result);
        return result;
    }

    function __validatePassword() {
        const result = __passwordRegex.test(password);
        setIsPasswordValid(result);
        return result;
    }

    async function __handleSignup(e: React.FormEvent) {
        e.preventDefault();
        setFormSubmitted(true);
        const usernameResult = __validateUsername();
        const passwordResult = __validatePassword();
        if (usernameResult && passwordResult) {
            signup && signup({ username: username, password: password })
                .then(result => {
                    toast.success(result, {
                        onClose: () => { history.goBack(); },
                        autoClose: environment.TOAST_TIME_IN_SECONDS
                    });
                })
                .catch(error => {
                    toast.error(error, { autoClose: environment.TOAST_TIME_IN_SECONDS });
                });
        }
    }
};

export default SignupPage;
