import React, {Component} from "react";
import {w3cwebsocket as W3CWebSocket} from "websocket";
import {USER_COOKIE} from "../../App";
import "./Login.css";

export const clientWebsocket = new W3CWebSocket('ws://localhost:8080');

export default class Login extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleRegistration = this.handleRegistration.bind(this);
        this.handleResetPassword = this.handleResetPassword.bind(this);

        this.state = {
            code: ""
        };

        if (localStorage.length !== 0) {
            this.props.history.push("/dashboard");
        }
    }

    handleChange(event) {
        this.setState({
            [event.target.name]: event.target.value
        });
    }

    handleSubmit(event) {
        const {username, password} = this.state;

        clientWebsocket.onopen = () => {
            console.log('WebSocket Client Connected');
        };

        clientWebsocket.onmessage = (message) => {
            const response = JSON.parse(message.data);

            if (response.code === "SUCCESSFUL_LOGIN") {
                localStorage.setItem(USER_COOKIE, response.cookie);
                this.props.handleLogin(true, response.login);
                this.props.history.push("/dashboard");

            } else {
                console.log(message.data);
                this.setState({
                    code: response.code
                })
            }
        };

        clientWebsocket.send(JSON.stringify({
            operation: "login",
            login: username,
            password: password
        }))
        event.preventDefault();
    }

    handleRegistration(event) {
        this.props.history.push("/sign-up");
        event.preventDefault();
    }

    handleResetPassword(event) {
        this.props.history.push("/reset-password");
    }

    render() {
        return (
            <div className="Login">
                <form onSubmit={this.handleSubmit}>
                    <div className="header">Login</div>
                    <div className="content">
                        <div className="form">
                            <div className="form-group">
                                <label htmlFor="username">Username</label>
                                <input type="text"
                                       name="username"
                                       placeholder="username"
                                       value={this.state.username}
                                       onChange={this.handleChange}
                                       required/>
                            </div>

                            <div className="form-group">
                                <label htmlFor="password">Password</label>
                                <input type="password"
                                       name="password"
                                       placeholder="password"
                                       value={this.state.password}
                                       onChange={this.handleChange}
                                       required
                                />
                            </div>
                        </div>
                    </div>
                    <div>{this.state.code}</div>
                    <div className="footer">
                        <button type="submit" className="btn">
                            Login
                        </button>
                    </div>
                </form>

                <form onSubmit={this.handleRegistration}>
                    <div className="footer">
                        <button type="submit" className="btn">
                            Registration
                        </button>
                    </div>
                </form>

                <form onSubmit={this.handleResetPassword}>
                    <div className="footer">
                        <button type="submit" className="btn">
                            Reset password
                        </button>
                    </div>
                </form>
            </div>
        );
    }
}
