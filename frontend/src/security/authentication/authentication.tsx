export interface UserLogin {
    username: string;
    password: string;
};

export interface UserAuthenticated {
    readonly id: number;
    readonly username: string;
};

export interface AuthenticationProps {
    readonly user: UserAuthenticated;
    readonly tokenType: string;
    readonly accessToken: string;
    readonly refreshToken: string;
}

export function isAuthenticationPropsValid(authenticationProps?: AuthenticationProps): boolean {
    if (authenticationProps &&
        authenticationProps.user.id &&
        authenticationProps.user.username &&
        authenticationProps.tokenType &&
        authenticationProps.accessToken &&
        authenticationProps.refreshToken) {
        return true;
    }

    return false;
}
