import * as React from 'react';

import { request, setAuthHeader } from '../helpers/axios_helper';
import { Link } from 'react-router-dom';

export default class AuthContent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            data: []
        }
    };

    componentDidMount() {
        request(
            "GET",
            "/messages",
            {}).then(
            (response) => {
                this.setState({data: response.data})
            }).catch(
            (error) => {
                if (error.response.status === 401) {
                    setAuthHeader(null);
                } else {
                    this.setState({data: error.response.code})
                }

            }
        );
    };

  render() {
    return (
        <div className="row justify-content-md-center" style={{margin:"25px", textAlign:'center'}}>
                <div className="card" style={{width: "80rem", height:"15rem"}}>
                    <div className="card-body">
                        <h5 className="card-title display-8">Мы рады, что Вы зарегистрировались в нашем приложении!</h5>
                        <p className="card-text lead" style={{marginTop:5}}>Рекомендуем начать с короткого ознакомительного видео – это поможет вам быстрее освоить платформу и использовать все её преимущества.</p>
                        <div className='container' style={{marginTop:-5}}>
                        <Link to="/search"  className="btn btn-dark me-2">Найти консультанта</Link>
                        </div>
                    </div>
                </div>
            </div>
    );
  };
}