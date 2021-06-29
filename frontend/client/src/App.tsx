import React from 'react';
import { Redirect, Route } from 'react-router-dom';
import { IonApp, IonRouterOutlet } from '@ionic/react';
import { IonReactRouter } from '@ionic/react-router';
import AuthenticationProvider from './security/authentication/authentication-provider';
import AuthenticationPage from './security/authentication/component/AuthenticationPage';
import SignupProvider from './security/signup/signup-provider';
import SignupPage from './security/signup/component/SignupPage';

/* Core CSS required for Ionic components to work properly */
import '@ionic/react/css/core.css';

/* Basic CSS for apps built with Ionic */
import '@ionic/react/css/normalize.css';
import '@ionic/react/css/structure.css';
import '@ionic/react/css/typography.css';

/* Optional CSS utils that can be commented out */
import '@ionic/react/css/padding.css';
import '@ionic/react/css/float-elements.css';
import '@ionic/react/css/text-alignment.css';
import '@ionic/react/css/text-transformation.css';
import '@ionic/react/css/flex-utils.css';
import '@ionic/react/css/display.css';

/* Theme variables */
import './theme/variables.css';
import { AccountProvider } from './pages/account/account-provider';
import AccountPage from './pages/account/component/AccountPage';
import PrivateRoute from './security/authentication/component/PrivateRoute';
import NewAccountPage from './pages/account/component/NewAccountPage';
import AccountFeedPage from './pages/account/component/AccountFeedPage';
import NewTransactionPage from './pages/transaction/component/NewTransactionPage';

const App: React.FC = () => (
  <IonApp>
    <IonReactRouter>
      <IonRouterOutlet>
        <AuthenticationProvider>
          <Route path="/login" component={AuthenticationPage} exact />
          <Route path="/" exact render={() => <Redirect to="/account" />} />

          <SignupProvider>
            <Route path="/signup" component={SignupPage} exact />
          </SignupProvider>

          <AccountProvider>
            <PrivateRoute path="/account" component={AccountPage} exact />
            <PrivateRoute path="/account-new" component={NewAccountPage} exact />
            <PrivateRoute path="/account/:id" component={AccountFeedPage} exact />
            <PrivateRoute path="/account/:id/transaction-new" component={NewTransactionPage} exact />
          </AccountProvider>
        </AuthenticationProvider>
      </IonRouterOutlet>
    </IonReactRouter>
  </IonApp>
);

export default App;
