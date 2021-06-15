import { IonButton, IonButtons, IonCol, IonContent, IonHeader, IonInput, IonItem, IonLabel, IonLoading, IonMenuButton, IonPage, IonRow, IonTitle, IonToolbar } from "@ionic/react";
import React, { useContext, useEffect, useState } from "react";
import { RouteComponentProps } from "react-router";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { environment } from "../../../environment/environment";
import { AuthenticationContext } from "../authentication-provider";
import './AuthenticationPage.scss';


const AuthenticationPage: React.FC<RouteComponentProps> = ({ history }) => {
    const [username, setUsername] = useState<string>('');
    const [password, setPassword] = useState<string>('');

    const { isAuthenticated, isAuthenticating, authenticationError, login } = useContext(AuthenticationContext);

    useEffect(() => {
        if (authenticationError) {
            toast.error(authenticationError, { autoClose: environment.TOAST_TIME_IN_SECONDS });
        }
    }, [authenticationError]);

    useEffect(() => {
        if (isAuthenticated) {
            toast.success('Logged in successfully',
                {
                    onClose: () => {
                        history.push('/account');
                    },
                    autoClose: environment.TOAST_TIME_IN_SECONDS
                });
        }
    }, [isAuthenticated]);

    return (
        <IonPage id='authentication-page'>
            <IonHeader>
                <IonToolbar>
                    <IonButtons>
                        <IonMenuButton></IonMenuButton>
                    </IonButtons>
                    <IonTitle id='authentication-title'>Authentication</IonTitle>
                </IonToolbar>
            </IonHeader>

            <IonContent fullscreen>
                <IonLoading isOpen={isAuthenticating} message="Authenticationg..." />

                <form onSubmit={__handleAuthentication}>
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

                    <IonRow>
                        <IonCol>
                            <IonButton fill="clear" routerLink="/signup" expand={"block"} id="signup-button">Signup</IonButton>
                        </IonCol>

                        <IonCol>
                            <IonButton type={"submit"} expand={"block"} id="login-submit-button">Login</IonButton>
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

    async function __handleAuthentication(e: React.FormEvent) {
        e.preventDefault();
        if (username && password) {
            login && login({ username: username, password: password });
        }
    }
};

export default AuthenticationPage;
