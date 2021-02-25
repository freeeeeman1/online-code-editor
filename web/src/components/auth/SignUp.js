import React, {Component} from "react";
import {clientWebsocket} from "./Login";
import "./Login.css";

export default class SignUp extends Component {
    constructor(props) {
        super(props);

        this.state = {
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
    }

    handleChange(event) {
        this.setState({
            [event.target.name]: event.target.value
        });
    }

    handleSubmit(event) {
        const {username, password, email} = this.state;

        clientWebsocket.onmessage = (message) => {
            console.log(message.data);
            const response = JSON.parse(message.data);

            if (response.code === 'SUCCESSFUL_REGISTRATION') {
                this.props.setUsername(username);
                this.props.history.push("/confirm-email");
            } else {
                this.setState({
                    code: response.code
                })
            }
        };

        clientWebsocket.send(JSON.stringify({
            operation: "registration",
            login: username,
            password: password,
            email: email
        }))

        event.preventDefault();
    }

    render() {
        return (
            <div className="Login">
                <form onSubmit={this.handleSubmit}>
                    <div className="header">Sign Up Form</div>
                    <div className="content">
                        <div className="form">
                            <div className="form-group">
                                <label htmlFor="username">Username</label>
                                <input type="text"
                                       name="username"
                                       placeholder="username"
                                       value={this.state.username}
                                       onChange={this.handleChange}
                                       required
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="email">Email</label>
                                <input type="text"
                                       name="email"
                                       placeholder="email"
                                       value={this.state.email}
                                       onChange={this.handleChange}
                                       required
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="password">Password</label>
                                <input type="text"
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
                            Sign up
                        </button>
                    </div>
                </form>
            </div>
        );
    }
}
