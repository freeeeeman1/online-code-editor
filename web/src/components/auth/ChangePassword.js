import React, {Component} from "react";
import {clientWebsocket} from "./Login";
import "./Login.css";

export default class ChangePassword extends Component {
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
        const {password} = this.state;

        clientWebsocket.onmessage = (message) => {
            console.log(message.data);
            const response = JSON.parse(message.data);

            if (response.code === "SUCCESSFUL_UPDATE") {
                this.props.history.push("/");
            } else {
                this.setState({
                    code: response.code
                })
            }
        };

        clientWebsocket.send(JSON.stringify({
            operation: "update-password",
            password: password,
            email: this.props.emailForResetPassword,
            code: this.props.codeForChange
        }))

        event.preventDefault();
    }

    render() {
        return (
            <div className="Login">
                <form onSubmit={this.handleSubmit}>
                    <div className="header">Reset Password</div>
                    <div className="content">
                        <div className="form">
                            <div className="form-group">
                                <label htmlFor="password">New password</label>
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
                            Submit
                        </button>
                    </div>
                </form>
            </div>
        );
    }
}
