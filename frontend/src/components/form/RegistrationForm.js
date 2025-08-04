import * as React from 'react';
import classNames from 'classnames';
import 'mdb-react-ui-kit/dist/css/mdb.min.css';
import './Login.css';
import { Link } from 'react-router-dom';

export default class RegistrationForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isAuthenticated: false,
      isDisabled: true,
      active: "registrationUser",
      email: "",
      firstName: "",
      lastName: "",
      phone: "",
      password: "",
      verificationCode: "",
      isEmailVerified: false,
      showModal: false,
      onRegister: props.onRegister
    };
  }

  onChangeHandler = (event) => {
    let name = event.target.name;
    let value = event.target.value;
    this.setState({ [name]: value });
  };

  onSubmitRegistrationUser = (e) => {
    this.state.onRegister(e, this.state.email, this.state.firstName, this.state.lastName, this.state.phone, this.state.password, 'USER');
  };

  onSubmitRegistrationConsultant = (e) => {
    this.state.onRegister(e, this.state.email, this.state.firstName, this.state.lastName, this.state.phone, this.state.password, 'CONSULTANT');
  };

  handleEmailChange = (event) => {
    const email = event.target.value;
    this.setState({ email });
    if (email && email.includes('@gmail.com')) {
      fetch('http://localhost:8080/send-verification-code', {
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
      fetch('http://localhost:8080/verify-code', {
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
        <p className="lead" style={{marginTop:20}}>Зарегистрируйтесь для работы в приложении.</p>
      <div className='AuthForm'>
        <div className="row justify-content-center margin">
          <div className="col-4">
          <ul className="nav nav-pills nav-justified mb-3" id="ex1" role="tablist">
          <li className="nav-item" role="presentation">
            <button 
              className={classNames(
                "nav-link", 
                this.state.active === "registrationUser" ? "active bg-secondary text-white" : "bg-light text-dark"
              )} 
              id="tab-registrationUser"
              onClick={() => this.setState({ active: "registrationUser" })}
            >
              Пользователь
            </button>
          </li>
          <li className="nav-item" role="presentation">
            <button 
              className={classNames(
                "nav-link", 
                this.state.active === "registrationConsultant" ? "active bg-secondary text-white" : "bg-light text-dark"
              )} 
              id="tab-registrationConsultant"
              onClick={() => this.setState({ active: "registrationConsultant" })}
            >
              Консультант
            </button>
          </li>
        </ul>

            <div className="tab-content">
              <div className={classNames("tab-pane", "fade", this.state.active === "registrationUser" ? "show active" : "")} id="pills-registrationUser" >
                <form onSubmit={this.onSubmitRegistrationUser}>
                <div className="form-outline mb-4">
                    <input 
                      type="email" 
                      id="registerEmail" 
                      name="email" 
                      className="form-control" 
                      onChange={this.handleEmailChange}
                      value={this.state.email}
                    />
                    <label className="form-label" htmlFor="registerEmail">Почта</label>
                  </div>

                  {this.state.email && this.state.email.includes('@') && (
                    <div className="form-outline mb-4">
                      <input 
                        type="text" 
                        id="verificationCode" 
                        name="verificationCode" 
                        className="form-control" 
                        onChange={this.handleVerificationCodeChange}
                        value={this.state.verificationCode}
                        placeholder="Введите код подтверждения"
                        maxLength={6}
                      />
                      <label className="form-label" htmlFor="verificationCode">Код подтверждения</label>
                    </div>
                  )}

                  <div className="form-outline mb-4">
                    <input type="text" id="registerFirstName" name="firstName" className="form-control" onChange={this.onChangeHandler} />
                    <label className="form-label" htmlFor="registerFirstName">Имя</label>
                  </div>
                  <div className="form-outline mb-4">
                    <input type="text" id="registerLastName" name="lastName" className="form-control" onChange={this.onChangeHandler} />
                    <label className="form-label" htmlFor="registerLastName">Фамилия</label>
                  </div>
                  <div className="form-outline mb-4">
                    <input type="text" id="registerPhone" name="phone" className="form-control" onChange={this.onChangeHandler} />
                    <label className="form-label" htmlFor="registerPhone">Телефон</label>
                  </div>
                  <div className="form-outline mb-4">
                    <input type="password" id="registerPassword" name="password" className="form-control" onChange={this.onChangeHandler} />
                    <label className="form-label" htmlFor="registerPassword">Пароль</label>
                  </div>
                  <div className="text-center" style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                    <button 
                      type="submit" 
                      className="btn btn-dark btn-block mb-2"
                      disabled={!this.state.isEmailVerified}
                    >
                      Зарегистрироваться
                    </button>
                  </div>
                </form>
              </div>
              <div className={classNames("tab-pane", "fade", this.state.active === "registrationConsultant" ? "show active" : "")} id="pills-registrationConsultant" >
                <form onSubmit={this.onSubmitRegistrationConsultant}>
                  <div className="form-outline mb-4">
                    <input 
                      type="email" 
                      id="registerEmail" 
                      name="email" 
                      className="form-control" 
                      onChange={this.handleEmailChange}
                      value={this.state.email}
                    />
                    <label className="form-label" htmlFor="registerEmail">Почта</label>
                  </div>

                  {this.state.email && this.state.email.includes('@') && (
                    <div className="form-outline mb-4">
                      <input 
                        type="text" 
                        id="verificationCode" 
                        name="verificationCode" 
                        className="form-control" 
                        onChange={this.handleVerificationCodeChange}
                        value={this.state.verificationCode}
                        placeholder="Введите код подтверждения"
                        maxLength={6}
                      />
                      <label className="form-label" htmlFor="verificationCode">Код подтверждения</label>
                    </div>
                  )}

                  <div className="form-outline mb-4">
                    <input type="text" id="registerFirstName" name="firstName" className="form-control" onChange={this.onChangeHandler} />
                    <label className="form-label" htmlFor="registerFirstName">Имя</label>
                  </div>
                  <div className="form-outline mb-4">
                    <input type="text" id="registerLastName" name="lastName" className="form-control" onChange={this.onChangeHandler} />
                    <label className="form-label" htmlFor="registerLastName">Фамилия</label>
                  </div>
                  <div className="form-outline mb-4">
                    <input type="text" id="registerPhone" name="phone" className="form-control" onChange={this.onChangeHandler} />
                    <label className="form-label" htmlFor="registerPhone">Телефон</label>
                  </div>
                  <div className="form-outline mb-4">
                    <input type="password" id="registerPassword" name="password" className="form-control" onChange={this.onChangeHandler} />
                    <label className="form-label" htmlFor="registerPassword">Пароль</label>
                  </div>
            
                  <div className="text-center" style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                    <button 
                      type="submit" 
                      className="btn btn-dark btn-block mb-2"
                      disabled={!this.state.isEmailVerified}
                    >
                      Зарегистрироваться
                    </button>
                    {/* {!this.state.isEmailVerified ? ("Для регистрации нужно подтвердить почту") : (" ")} */}
                  </div>
                </form>
              </div>
            </div>
            <Link 
            to="/login" 
            className="lead" 
            style={{ 
                fontSize: 20, 
                color: 'inherit',
                textDecoration: 'none'
            }}
            >
            Есть аккаунта? Нажмите, чтобы войти
            </Link>
          </div>
        </div>
      </div>
      
      </div>
    );
  };

}