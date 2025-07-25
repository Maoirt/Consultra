import * as React from 'react';
import { request, setAuthHeader } from '../helpers/axios_helper';
import Header from './Header';
import AuthContent from './AuthContent';
import logo from '../logo.svg';
import LoginForm from './form/LoginForm';
import WelcomeContent from './WelcomeContent';
import { BrowserRouter as Router, Routes, Route, Navigate, useParams, useNavigate, Redirect } from 'react-router-dom';
import RegistrationForm from './form/RegistrationForm';
import ConsultantPage from './page/ConsultantPage';
import SearchPage from './page/SearchPage';
import ConsultantChatPage from './page/ConsultantChatsPage';
import ConsultantChatsPage from './page/ConsultantChatsPage';
import ChatPage from './page/ChatPage';
import AdminPanel from './page/AdminPanel';
import AdminRegister from './page/AdminRegister';
import AdminLogin from './page/AdminLogin';

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
            // Исправление: поддержка userId и id, проверка на undefined
            const userId = response.data.id || response.data.userId;
            if (!userId) {
                alert('Ошибка: сервер не вернул id пользователя. Обратитесь в поддержку.');
                setAuthHeader(null);
                this.setState({ componentToShow: "welcome" });
                return;
            }
            localStorage.setItem('userId', userId);
            setAuthHeader(response.data.token);
            if (role === 'CONSULTANT' && userId) {
                request('GET', `/consultant/by-user/${userId}`)
                  .then(res => {
                    localStorage.setItem('consultantId', res.data.id);
                    window.location.href = `/consultant/${res.data.id}`;
                  });
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
            // Исправление: поддержка userId и id, проверка на undefined
            const userId = response.data.id || response.data.userId;
            if (!userId) {
                alert('Ошибка: сервер не вернул id пользователя. Обратитесь в поддержку.');
                setAuthHeader(null);
                this.setState({ componentToShow: "welcome" });
                return;
            }
            localStorage.setItem('userId', userId);
            setAuthHeader(response.data.token);
            if (role === 'CONSULTANT' && userId) {
                request('GET', `/consultant/by-user/${userId}`)
                  .then(res => {
                    localStorage.setItem('consultantId', res.data.id);
                    window.location.href = `/consultant/${res.data.id}`;
                  });
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
                    <Route path="/consultant/:id/chats" element={<ConsultantChatsPageWrapper />} />
                    <Route path="/chat/:chatId" element={<ChatPageWrapper />} />
                    <Route path="/admin" element={getUserRole() === 'ADMIN' ? <AdminPanel /> : <Navigate to="/" />} />
                    <Route path="/admin-register" element={<AdminRegister />} />
                    <Route path="/admin-login" element={<AdminLogin />} />
                </Routes>
            </Router>
        );
    }
}

function ConsultantPageWrapper() {
    let { id } = useParams();
    // Use consultantId from localStorage if available
    const consultantId = localStorage.getItem('consultantId');
    if (consultantId) id = consultantId;
    return <ConsultantPage consultantId={id} />;
}

function ConsultantChatsPageWrapper() {
    let { id } = useParams();
    // Use consultantId from localStorage if available
    const consultantId = localStorage.getItem('consultantId') || id;
    // Если пользователь, а не консультант, перенаправляем на первый чат
    const userId = localStorage.getItem('userId');
    if (!consultantId && userId) {
        // Получаем список чатов пользователя и редиректим на первый
        // (Можно реализовать через useEffect, но для простоты делаем window.location)
        request('GET', `/user/${userId}/chats`).then(r => {
            if (r.data && r.data.length > 0) {
                const chatId = [userId, r.data[0]].sort().join('-');
                window.location.href = `/chat/${chatId}`;
            }
        });
        return null;
    }
    return <ConsultantChatsPage consultantId={consultantId} />;
}

function ChatPageWrapper() {
    let { chatId } = useParams();
    return <ChatPage chatId={chatId} />;
}

const getUserRole = () => {
  const user = JSON.parse(localStorage.getItem('user'));
  return user && user.role;
};