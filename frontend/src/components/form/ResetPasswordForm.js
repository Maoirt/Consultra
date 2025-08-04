import * as React from 'react';
import classNames from 'classnames';
import 'mdb-react-ui-kit/dist/css/mdb.min.css';
import './Login.css';
import { Link, useSearchParams } from 'react-router-dom';

export default class ResetPasswordForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      newPassword: "",
      confirmPassword: "",
      message: "",
      success: false,
      isLoading: false
    };
  }

  componentDidMount() {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    if (!token) {
      this.setState({ 
        message: 'Отсутствует токен для сброса пароля', 
        success: false 
      });
    }
  }

  onChangeHandler = (event) => {
    let name = event.target.name;
    let value = event.target.value;
    this.setState({ [name]: value });
  };

  onSubmitResetPassword = (e) => {
    e.preventDefault();
    
    if (this.state.newPassword !== this.state.confirmPassword) {
      this.setState({ 
        message: 'Пароли не совпадают', 
        success: false 
      });
      return;
    }

    if (this.state.newPassword.length < 6) {
      this.setState({ 
        message: 'Пароль должен содержать минимум 6 символов', 
        success: false 
      });
      return;
    }

    this.setState({ isLoading: true, message: "" });
    
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    
            fetch(`${process.env.FRONTEND_REACT_APP_API_URL || 'http://localhost:8080'}/reset-password`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ 
        token: token,
        newPassword: this.state.newPassword 
      })
    })
    .then(response => response.json())
    .then(data => {
      this.setState({ 
        message: data.message, 
        success: data.success, 
        isLoading: false 
      });
      
      if (data.success) {

        setTimeout(() => {
          window.location.href = '/login';
        }, 3000);
      }
    })
    .catch(error => {
      console.error('Error:', error);
      this.setState({ 
        message: 'Ошибка при сбросе пароля', 
        success: false, 
        isLoading: false 
      });
    });
  };

  render() {
    return (
      <div className="container-fluid">
        <div className="row">
          <div className="col-md-6 offset-md-3">
            <div className="card">
              <div className="card-body">
                <h3 className="card-title text-center mb-4">Установить новый пароль</h3>
                
                {this.state.message && (
                  <div className={`alert ${this.state.success ? 'alert-success' : 'alert-danger'} mb-3`}>
                    {this.state.message}
                  </div>
                )}
                
                <form onSubmit={this.onSubmitResetPassword}>
                  <div className="form-group mb-3">
                    <label htmlFor="newPassword" className="form-label">Новый пароль</label>
                    <input
                      type="password"
                      className="form-control"
                      id="newPassword"
                      name="newPassword"
                      value={this.state.newPassword}
                      onChange={this.onChangeHandler}
                      required
                      placeholder="Введите новый пароль"
                      minLength="6"
                    />
                  </div>
                  
                  <div className="form-group mb-3">
                    <label htmlFor="confirmPassword" className="form-label">Подтвердите пароль</label>
                    <input
                      type="password"
                      className="form-control"
                      id="confirmPassword"
                      name="confirmPassword"
                      value={this.state.confirmPassword}
                      onChange={this.onChangeHandler}
                      required
                      placeholder="Подтвердите новый пароль"
                      minLength="6"
                    />
                  </div>
                  
                  <div className="d-grid gap-2">
                    <button
                      type="submit"
                      className="btn btn-primary"
                      disabled={this.state.isLoading}
                    >
                      {this.state.isLoading ? 'Сохранение...' : 'Установить новый пароль'}
                    </button>
                  </div>
                </form>
                
                <div className="text-center mt-3">
                  <Link to="/login" className="text-decoration-none">
                    Вернуться к входу
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
} 