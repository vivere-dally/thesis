import React, { useCallback, useState } from "react";
import { ReactNodeLikeProps, newLogger } from "../../core/utils";
import { UserSignup } from "./signup";
import { signupApi } from "./signup-api";

const log = newLogger('security/signup/signup-provider');
interface SignupState {
    executing: boolean;
    signup?: (userSignup: UserSignup) => Promise<any>;
};

const signupInitialState: SignupState = {
    executing: false
};
export const SignupContext = React.createContext<SignupState>(signupInitialState);
const SignupProvider: React.FC<ReactNodeLikeProps> = ({ children }) => {
    const [state, setState] = useState<SignupState>(signupInitialState);

    const signup = useCallback<(userSignup: UserSignup) => Promise<any>>(__signup, []);

    const value = { ...state, signup };
    return (
        <SignupContext.Provider value={value}>
            {children}
        </SignupContext.Provider>
    );

    async function __signup(userSignup: UserSignup): Promise<any> {
        try {
            setState({ ...state, executing: true });
            const result = await signupApi(userSignup);
            return result;
        } catch (error) {
            log('{__signup}', JSON.stringify(error));
            return error.response.data;
        } finally {
            setState({ ...state, executing: false });
        }
    }
}

export default SignupProvider;
