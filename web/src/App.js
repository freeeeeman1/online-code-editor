import React, {Component} from "react";
import {BrowserRouter, Route, Switch} from "react-router-dom";
import Dashboard from "./components/Dashboard";
import SignUp from "./components/auth/SignUp";
import ConfirmEmail from "./components/auth/ConfirmEmail";
import ResetPassword from "./components/auth/ResetPassword";
import ChangePassword from "./components/auth/ChangePassword";
import NavigationBar from "./components/auth/NavigationBar";
import Login, {clientWebsocket} from "./components/auth/Login";

export const USER_COOKIE = 'cookie';

export default class App extends Component {
    constructor(props) {
        super(props);

        this.state = {};

        this.handleLogin = this.handleLogin.bind(this);
        this.setUsername = this.setUsername.bind(this);
        this.setEmail = this.setEmail.bind(this);
        this.setCode = this.setCode.bind(this);

        if (localStorage.length !== 0) {
            let cookie = localStorage.getItem(USER_COOKIE);

            clientWebsocket.onopen = () => {
                clientWebsocket.send(JSON.stringify({
                    operation: USER_COOKIE,
                    cookie: cookie,
                }))
            };

            clientWebsocket.onmessage = (message) => {
                console.log(message.data);
                const response = JSON.parse(message.data);

                if (response.status === 'You are signed in') {
                    this.handleLogin(true, response.login);
                } else {
                    localStorage.clear();
                    window.location.reload();
                }
            }
        }
    }

    handleLogin(status, username) {
        this.setState({
            loginStatus: status,
            username: username
        })
    }

    setUsername(username) {
        this.setState({
            usernameForConfirmEmail: username,
            emailForResetPassword: ""
        })
    }

    setEmail(email) {
        this.setState({
            emailForResetPassword: email,
            usernameForConfirmEmail: "",
        })
    }

    setCode(code) {
        this.setState({
            codeForChange: code
        })
    }

    render() {
        return (
            <div className="app">
                {
                    this.state.loginStatus ? <NavigationBar/> : ""
                }
                <BrowserRouter>
                    <Switch>
                        <Route exact path={"/"} render={props => (
                                <Login {...props}
                                       handleLogin={this.handleLogin}
                                />
                            )}
                        />
                        <Route exact path={"/dashboard"} render={props => (
                                <Dashboard
                                    {...props}
                                    username={this.state.username}
                                    handleLogin={this.handleLogin}
                                />
                            )}
                        />
                        <Route exact path={"/sign-up"} render={props => (
                                <SignUp {...props}
                                        setUsername={this.setUsername}/>
                            )}
                        />
                        <Route exact path={"/confirm-email"} render={props => (
                                <ConfirmEmail
                                    {...props}
                                    usernameForConfirmEmail={this.state.usernameForConfirmEmail}
                                    emailForResetPassword={this.state.emailForResetPassword}
                                    setCode={this.setCode}
                                />
                            )}
                        />
                        <Route exact path={"/reset-password"} render={props => (
                                <ResetPassword
                                    {...props}
                                    setEmail={this.setEmail}
                                />
                            )}
                        />
                        <Route exact path={"/change-password"} render={props => (
                                <ChangePassword
                                    {...props}
                                    emailForResetPassword={this.state.emailForResetPassword}
                                    codeForChange={this.state.codeForChange}
                                />
                            )}
                        />
                    </Switch>
                </BrowserRouter>
            </div>
        );
    }
}
