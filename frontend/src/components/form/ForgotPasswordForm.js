import * as React from 'react';
import classNames from 'classnames';
import 'mdb-react-ui-kit/dist/css/mdb.min.css';
import './Login.css';
import { Link } from 'react-router-dom';
import { request } from '../../helpers/axios_helper';

export default class ForgotPasswordForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      email: "",
      message: "",
      success: false,
      isLoading: false
    };
  }

  onChangeHandler = (event) => {
    let name = event.target.name;
    let value = event.target.value;
    this.setState({ [name]: value });
  };

  onSubmitForgotPassword = (e) => {
    e.preventDefault();
    this.setState({ isLoading: true, message: "" });
    
    request('POST', '/forgot-password', { email: this.state.email })
      .then(response => {
        this.setState({ 
          message: response.data.message, 
          success: response.data.success, 
          isLoading: false 
        });
      })
      .catch(error => {
        console.error('Error:', error);
        this.setState({ 
          message: 'Ошибка при отправке запроса на сброс пароля', 
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
                <h3 className="card-title text-center mb-4">Забыли пароль?</h3>
                
                {this.state.message && (
                  <div className={`alert ${this.state.success ? 'alert-success' : 'alert-danger'} mb-3`}>
                    {this.state.message}
                  </div>
                )}
                
                <form onSubmit={this.onSubmitForgotPassword}>
                  <div className="form-group mb-3">
                    <label htmlFor="email" className="form-label">Email</label>
                    <input
                      type="email"
                      className="form-control"
                      id="email"
                      name="email"
                      value={this.state.email}
                      onChange={this.onChangeHandler}
                      required
                      placeholder="Введите ваш email"
                    />
                  </div>
                  
                  <div className="d-grid gap-2">
                    <button
                      type="submit"
                      className="btn btn-primary"
                      disabled={this.state.isLoading}
                    >
                      {this.state.isLoading ? 'Отправка...' : 'Отправить письмо для сброса пароля'}
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