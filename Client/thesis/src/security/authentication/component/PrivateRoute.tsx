import PropTypes from 'prop-types';
import React, { useContext } from "react";
import { Redirect, Route } from "react-router";
import { AuthenticationContext } from "../authentication-provider";


export interface PrivateRouteProps {
    component: PropTypes.ReactNodeLike;
    path: string;
    exact?: boolean;
};

const PrivateRoute: React.FC<PrivateRouteProps> = ({ component: Component, ...rest }) => {
    const { isAuthenticated } = useContext(AuthenticationContext);
    return (
        <Route {...rest} render={props => {
            if (isAuthenticated) {
                // @ts-ignore
                return <Component {...props} />
            }

            return <Redirect to={{ pathname: '/login' }} />
        }} />
    );
};

export default PrivateRoute;
