import * as React from 'react';
import {
  MDBDropdown,
  MDBDropdownMenu,
  MDBDropdownToggle,
  MDBDropdownItem,
  MDBIcon
} from 'mdb-react-ui-kit';
import { Link } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';

export default function Header(props) {
    const navigate = useNavigate();

    const handleLogin = () => {
        navigate('/login');
    };

    const handleRegistration = () => {
        navigate('/registration');
    };

    return (
        <header className="App-header d-flex align-items-center">
            <Link to="/" className="App-title ms-5" 
            style={{ 
              fontSize: 36, 
              color: 'inherit',
              textDecoration: 'none' 
            }}>{props.pageTitle}</Link>
            <div className="ms-auto">
                {props.isAuthenticated ? (
                    <MDBDropdown>
                        <MDBDropdownToggle tag="button" className="btn btn-dark">
                            <MDBIcon icon="user" className="me-2" />
                            {'Меню'}
                        </MDBDropdownToggle>
                        <MDBDropdownMenu>
                            <MDBDropdownItem>
                                <Link to="/search" className="dropdown-item">Поиск</Link>
                                <Link to="/settings" className="dropdown-item">Настройка</Link>
                            </MDBDropdownItem>
                        </MDBDropdownMenu>
                    </MDBDropdown>
                ) : (
                    <div>
                    <button onClick={handleRegistration} className="btn btn-dark me-2">Стать консультантом</button>
                    <button onClick={handleLogin} className="btn btn-light me-2">Войти</button>
                    </div>
                )}
            </div>
            <div>
                {props.isAuthenticated ? (
                    <button onClick={props.logout} className="btn btn-dark me-2" style={{ margin: '10px' }}>Выйти</button>
                ) : (" ")}
            </div>
        </header>
    );
}