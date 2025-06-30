import * as React from 'react';
import { request, setAuthHeader } from '../helpers/axios_helper';
import Header from './Header';
import AuthContent from './AuthContent';
import logo from '../logo.svg';
import LoginForm from './form/LoginForm';
import WelcomeContent from './WelcomeContent';
import { BrowserRouter as Router, Routes, Route, Navigate, useParams, useNavigate } from 'react-router-dom';
import RegistrationForm from './form/RegistrationForm';
import ConsultantPage from './page/ConsultantPage';
import SearchPage from './page/SearchPage';

export default class AppContent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            componentToShow: "welcome",
            isAuthenticated: false,
            email: ''
        };
    }

    componentDidMount() {
        const token = localStorage.getItem('auth_token');
        if (token) {
            setAuthHeader(token);
            this.setState({
                componentToShow: "messages",
                isAuthenticated: true
            });
        }
    }


    login = () => {
        this.setState({ componentToShow: "login" });
    };

    logout = () => {
        localStorage.removeItem('auth_token');
        this.setState({
            componentToShow: "welcome",
            isAuthenticated: false,
            email: ''
        });
        setAuthHeader(null);
    };

    onLogin = (e, email, password, role = 'USER') => {
        e.preventDefault();
        request("POST", "/login", {
            email: email,
            password: password
        }).then((response) => {
            localStorage.setItem('auth_token', response.data.token);
            setAuthHeader(response.data.token);
            if (role === 'CONSULTANT' && response.data.consultantId) {
                window.location.href = `/consultant/${response.data.consultantId}`;
            } else {
            this.setState({
                componentToShow: "search",
                isAuthenticated: true,
                email: email
            });
        }
        }).catch((error) => {
            setAuthHeader(null);
            this.setState({ componentToShow: "welcome" });
        });
    };

    onRegister = (e, email, firstName, lastName, phone, password, role = 'USER') => {
        e.preventDefault();
        request("POST", "/register", {
            email: email,
            firstName: firstName,
            lastName: lastName,
            phone: phone,
            password: password,
            role: role
        }).then((response) => {
            localStorage.setItem('auth_token', response.data.token);
            setAuthHeader(response.data.token);
            if (role === 'CONSULTANT' && response.data.consultantId) {
                window.location.href = `/consultant/${response.data.consultantId}`;
            } else {
                this.setState({
                    componentToShow: "search",
                    isAuthenticated: true,
                    email: email
                });
            }
        }).catch((error) => {
            setAuthHeader(null);
            this.setState({ componentToShow: "welcome" });
            console.error("Registration error:", error);
        });
    };

     onGoogleLogin = async () => {
        try {

            const response = await fetch("http://localhost:8081/oauth2/login/success", {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) {
                throw new Error("Ошибка аутентификации");
            }

            const data = await response.json();
            localStorage.setItem("token", data.token);
            setAuthHeader(data.token);
            this.setState({ componentToShow: "messages" });
              this.setState({
                       isAuthenticated: true,
                   });
        } catch (error) {
            console.error("Ошибка при входе через Google", error);
        }
    };
  render() {
        return (
            <Router>
                <Header
                    login={this.login}
                    isAuthenticated={this.state.isAuthenticated}
                    email={this.state.email}
                    logout={this.logout}
                    pageTitle="Консультра"
                    logoSrc={logo} />

                <Routes>
                    <Route path="/" element={
                        this.state.isAuthenticated ?
                            <Navigate to="/messages" /> :
                            <WelcomeContent />
                    } />

                    <Route path="/login" element={
                        this.state.isAuthenticated ?
                            <Navigate to="/search" /> :
                            <LoginForm onLogin={this.onLogin} />
                    } />
                    <Route path="/messages" element={
                        this.state.isAuthenticated ?
                            <AuthContent /> :
                            <Navigate to="/login" />
                    } />
                    <Route path="/registration" element={
                        this.state.isAuthenticated ?
                            <Navigate to="/messages" /> :
                            <RegistrationForm onRegister={this.onRegister} />
                    } />
                    <Route path="search" element={<SearchPage />} />
                    <Route path="/consultant/:id" element={<ConsultantPageWrapper />} />
                </Routes>
            </Router>
        );
    }
}

function ConsultantPageWrapper() {
    const { id } = useParams();
    return <ConsultantPage consultantId={id} />;
}