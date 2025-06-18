import * as React from 'react';
import classNames from 'classnames';
import 'mdb-react-ui-kit/dist/css/mdb.min.css';
import './Login.css';
import { Link } from 'react-router-dom';

export default class LoginForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isAuthenticated: false,
      isDisabled: true,
      active: "loginUser",
      email: "",
      firstName: "",
      lastName: "",
      phone: "",
      password: "",
      verificationCode: "",
      isEmailVerified: false,
      showModal: false,
      onLogin: props.onLogin
    };
  }

  onChangeHandler = (event) => {
    let name = event.target.name;
    let value = event.target.value;
    this.setState({ [name]: value });
  };

  onSubmitloginUser = (e) => {
      e.preventDefault();
    this.state.onLogin(e, this.state.email, this.state.password);
  };

  onSubmitloginConsult = (e) => {
    e.preventDefault();
  this.state.onLogin(e, this.state.email, this.state.password);
};

  loginGoogle = () => {
    window.location.href = "http://localhost:8081/oauth2/authorization/google"
  }

  loginGithub = () => {
    window.location.href = "http://localhost:8081/oauth2/authorization/github"
  }

  toggleModal = () => {
    this.setState(prevState => ({ showModal: !prevState.showModal }));
  }

  handleResetPassword = () => {
    const { email } = this.state;
    this.toggleModal();
    window.location.href = `http://localhost:8081/send-reset-link?email=${encodeURIComponent(email)}`;
  }

  handleEmailChange = (event) => {
    const email = event.target.value;
    this.setState({ email });
    alert(email)
    if (email && email.includes('@')) {
      fetch('http://localhost:8081/send-verification-code', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email })
      })
      .then(response => response.json())
      .then(data => {
        if (data.success) {
          alert('Код подтверждения отправлен на вашу почту');
        }
      })
      .catch(error => {
        console.error('Error:', error);
        alert('Ошибка при отправке кода подтверждения');
      });
    }
  };

  handleVerificationCodeChange = (event) => {
    const verificationCode = event.target.value;
    this.setState({ verificationCode });
    
    if (verificationCode.length === 6) {
      fetch('http://localhost:8081/verify-code', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: this.state.email,
          code: verificationCode
        })
      })
      .then(response => response.json())
      .then(data => {
        if (data.verified) {
          this.setState({ isEmailVerified: true });
          alert('Email успешно подтвержден');
        } else {
          this.setState({ isEmailVerified: false });
          alert('Неверный код подтверждения');
        }
      })
      .catch(error => {
        console.error('Error:', error);
        this.setState({ isEmailVerified: false });
      });
    }
  };

  render() {
    return (
      <div className='form'>
        <h3 className="display-4" style={{fontWeight:500}}>Консультра</h3>
        <h3 className="display-6" style={{fontWeight:500}}>Добро пожаловать!</h3>
        <p className="lead" style={{marginTop:20}}>Войдите в свой аккаунт.</p>
      <div className='AuthForm'>
        <div className="row justify-content-center margin">
          <div className="col-4">
          <ul className="nav nav-pills nav-justified mb-3" id="ex1" role="tablist">
          <li className="nav-item" role="presentation">
            <button 
              className={classNames(
                "nav-link", 
                this.state.active === "loginUser" ? "active bg-secondary text-white" : "bg-light text-dark"
              )} 
              id="tab-loginUser"
              onClick={() => this.setState({ active: "loginUser" })}
            >
              Пользователь
            </button>
          </li>
          <li className="nav-item" role="presentation">
            <button 
              className={classNames(
                "nav-link", 
                this.state.active === "loginConsult" ? "active bg-secondary text-white" : "bg-light text-dark"
              )} 
              id="tab-loginConsult"
              onClick={() => this.setState({ active: "loginConsult" })}
            >
              Консультант
            </button>
          </li>
        </ul>

            <div className="tab-content">
              <div className={classNames("tab-pane", "fade", this.state.active === "loginUser" ? "show active" : "")} id="pills-loginUser" >
                <form onSubmit={this.onSubmitloginUser}>
                <div data-mdb-input-init className="form-outline mb-4">
                  <input type="email" id="typeEmail" name='email' onChange={this.onChangeHandler} className="form-control my-3" />
                  <label className="form-label" htmlFor="typeEmail">Email</label>
                </div>
                  <div className="form-outline mb-4">
                    <input type="password" id="loginPassword" name="password" className="form-control" onChange={this.onChangeHandler} />
                    <label className="form-label" htmlFor="loginPassword">Пароль</label>
                  </div>
                  <div className="text-center" style={{ display: 'flex', flexDirection: 'column', gap: '5px'}}>
                    <button type="submit" className="btn btn-dark btn-block mb-4">Войти</button>
                  </div>
                </form>
              </div>
              <div className={classNames("tab-pane", "fade", this.state.active === "loginConsult" ? "show active" : "")} id="pills-loginConsult" >
                <form onSubmit={this.onSubmitloginConsult}>
                <div data-mdb-input-init className="form-outline mb-4">
                  <input type="email" id="typeEmail" name='email' onChange={this.onChangeHandler} className="form-control my-3" />
                  <label className="form-label" htmlFor="typeEmail">Email</label>
                </div>
                  <div className="form-outline mb-4">
                    <input type="password" id="loginPassword" name="password" className="form-control" onChange={this.onChangeHandler} />
                    <label className="form-label" htmlFor="loginPassword">Пароль</label>
                  </div>
                  <div className="text-center" style={{ display: 'flex', flexDirection: 'column', gap: '5px'}}>
                    <button type="submit" className="btn btn-dark btn-block mb-4">Войти</button>
                  </div>
                </form>
              </div>
            </div>
            <Link 
            to="/registration" 
            className="lead" 
            style={{ 
              fontSize: 20, 
              color: 'inherit',
              textDecoration: 'none' 
            }}
          >
            Нет аккаунта? Нажмите, чтобы Зарегистрироваться
          </Link>
          </div>
        </div>
      </div>  
      </div>
    );
  };
}