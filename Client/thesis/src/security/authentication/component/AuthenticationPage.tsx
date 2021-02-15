import { IonButton, IonButtons, IonCol, IonContent, IonHeader, IonInput, IonItem, IonLabel, IonList, IonLoading, IonMenuButton, IonPage, IonRow, IonText, IonTitle, IonToolbar } from "@ionic/react";
import React, { useContext, useState } from "react";
import { RouteComponentProps } from "react-router";
import { AuthenticationContext } from "../authentication-provider";
import './AuthenticationPage.scss';


const AuthenticationPage: React.FC<RouteComponentProps> = ({ history }) => {
    const [username, setUsername] = useState<string>('');
    const [password, setPassword] = useState<string>('');

    const { isAuthenticated, isAuthenticating, authenticationError, login } = useContext(AuthenticationContext);

    return (
        <IonPage id='authentication-page'>
            <IonHeader>
                <IonToolbar>
                    <IonButtons>
                        <IonMenuButton></IonMenuButton>
                    </IonButtons>
                    <IonTitle>Authentication</IonTitle>
                </IonToolbar>
            </IonHeader>

            <IonContent fullscreen>
                <IonLoading isOpen={isAuthenticating} message="Authenticationg..." />

                <form noValidate onSubmit={__handleAuthentication}>
                    <IonItem>
                        <IonLabel position={"floating"}>Username</IonLabel>
                        <IonInput
                            type="text"
                            value={username}
                            onIonChange={e => setUsername(e.detail.value!)}
                            required
                        />
                    </IonItem>

                    <IonItem>
                        <IonLabel position={"floating"}>Password</IonLabel>
                        <IonInput
                            type="password"
                            value={password}
                            onIonChange={e => setPassword(e.detail.value!)}
                            required
                        />
                    </IonItem>

                    <IonRow>
                        <IonCol>
                            <IonButton fill="clear" routerLink="/signup" expand={"block"}>Signup</IonButton>
                        </IonCol>

                        <IonCol>
                            <IonButton type={"submit"} expand={"block"}>Login</IonButton>
                        </IonCol>
                    </IonRow>
                </form>
            </IonContent>
        </IonPage>
    );

    async function __handleAuthentication(e: React.FormEvent) {
        e.preventDefault();

    }
};

export default AuthenticationPage;
