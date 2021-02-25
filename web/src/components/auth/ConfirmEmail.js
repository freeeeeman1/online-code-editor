import React, {Component} from "react";
import {clientWebsocket} from "./Login";
import "./Login.css";

export default class ConfirmEmail extends Component {
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
        const {code} = this.state;

        clientWebsocket.onmessage = (message) => {
            console.log(message.data);
            const response = JSON.parse(message.data);


            switch (response.code) {
                case "SUCCESSFUL_CODE":
                    this.props.history.push('/');
                    break;
                case "SUCCESSFUL_CONFIRM_EMAIL":
                    this.props.setCode(code);
                    this.props.history.push('/change-password');
                    break;
                default:
                    this.setState({
                        responseCode: response.code
                    })
            }
        };

        if (this.props.emailForResetPassword === "") {
            clientWebsocket.send(JSON.stringify({
                operation: "confirm-email-registration",
                code: code,
                login: this.props.usernameForConfirmEmail
            }))
        } else {
            clientWebsocket.send(JSON.stringify({
                operation: "confirm-email-reset",
                code: code,
                email: this.props.emailForResetPassword
            }))
        }
        event.preventDefault();
    }

    render() {
        return (
            <div className="Login">
                <form onSubmit={this.handleSubmit}>
                    <div className="header">Confirm Email</div>
                    <div className="content">
                        <div className="form">
                            <div className="form-group">
                                <label htmlFor="code">Code</label>
                                <input type="text"
                                       name="code"
                                       placeholder="code"
                                       value={this.state.code}
                                       onChange={this.handleChange}
                                       required
                                />
                            </div>
                        </div>
                    </div>
                    <div>{this.state.responseCode}</div>
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
