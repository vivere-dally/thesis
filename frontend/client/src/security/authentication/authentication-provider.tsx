import React, { useCallback, useEffect, useState } from "react";
import {
  ReactNodeLikeProps,
  newLogger,
  storageClearByKeyPrefix,
  storageGetByKeyPrefix,
} from "../../core/utils";
import { loginApi } from "./authentication-api";
import {
  AuthenticationProps,
  isAuthenticationPropsValid,
  UserLogin,
} from "./authentication";
import { constants } from "../../environment/constants";

const log = newLogger("security/authentication/authentication-provider");
interface AuthenticationState {
  userLogin?: UserLogin;
  authenticationProps?: AuthenticationProps;
  isAuthenticated: boolean;
  isAuthenticating: boolean;
  authenticationError: Error | null;
  login?: (userLogin: UserLogin) => Promise<void>;
  logout?: () => Promise<void>;
}

const authenticationInitialState: AuthenticationState = {
  isAuthenticated: false,
  isAuthenticating: false,
  authenticationError: null,
};

export const AuthenticationContext = React.createContext<AuthenticationState>(
  authenticationInitialState
);
const AuthenticationProvider: React.FC<ReactNodeLikeProps> = ({ children }) => {
  const [state, setState] = useState<AuthenticationState>(
    authenticationInitialState
  );

  const login = useCallback<(userLogin: UserLogin) => Promise<void>>(
    __login,
    []
  );
  const logout = useCallback<() => Promise<void>>(__logout, []);

  useEffect(__loginEffect, [state.isAuthenticating]);

  const value = { ...state, login, logout };
  return (
    <AuthenticationContext.Provider value={value}>
      {children}
    </AuthenticationContext.Provider>
  );

  async function __login(userLogin: UserLogin) {
    log("__login");
    setState({
      ...state,
      userLogin: userLogin,
      authenticationProps: undefined,
      isAuthenticating: true,
      authenticationError: null,
    });
  }

  async function __logout() {
    log("__logout");
    setState({
      ...state,
      authenticationProps: undefined,
      isAuthenticated: false,
      authenticationError: null,
    });

    await storageClearByKeyPrefix("");
  }

  function __loginEffect() {
    let cancelled = false;
    authenticate();
    return () => {
      cancelled = true;
    };

    async function authenticate() {
      const authenticationProps: AuthenticationProps = (
        await storageGetByKeyPrefix<AuthenticationProps>(
          constants.STORAGE_AUTHENTICATION_KEY
        )
      )[0];
      if (isAuthenticationPropsValid(authenticationProps)) {
        log("{__loginEffect}", "(authenticate)", "success");
        setState({
          ...state,
          authenticationProps: authenticationProps,
          isAuthenticating: false,
          isAuthenticated: true,
          authenticationError: null,
        });

        return;
      }

      if (!state.isAuthenticating) {
        return;
      }

      try {
        log("{__loginEffect}", "(authenticate)", "start");
        if (!state.userLogin) {
          throw "no credentials";
        }

        const authenticationProps: AuthenticationProps = await loginApi(
          state.userLogin
        );
        if (cancelled) {
          log("{__loginEffect}", "(authenticate)", "cancelled");
        }

        log("{__loginEffect}", "(authenticate)", "success");
        setState({
          ...state,
          authenticationProps: authenticationProps,
          isAuthenticating: false,
          isAuthenticated: true,
          authenticationError: null,
        });
      } catch (error) {
        if (cancelled || !error) {
          return;
        }

        log("{__loginEffect}", "(authenticate)", error);
        setState({
          ...state,
          authenticationProps: undefined,
          isAuthenticating: false,
          isAuthenticated: false,
          authenticationError: error,
        });
      }
    }
  }
};

export default AuthenticationProvider;
