import * as React from 'react';
import './App.css';
import { Link } from 'react-router-dom';


export default class WelcomeContent extends React.Component {

  render() {
    return (
            <div className="jumbotron jumbotron-fluid">
              <div className="container">
                <h1 className="display-4" style={{fontWeight:500}}>Найдите идеального консультанта</h1>
                <p className="lead" style={{marginTop:30}}>Платформа, которая объединяет экспертов и тех, кто ищет профессиональные консультации. Решайте задачи эффективно с проверенными специалистами.</p>
                <div className='buttons' style={{marginTop:30}}>
                    <Link to="/registration" className="btn btn-dark me-2">Найти консультанта</Link>
                    <Link to="/registration"  className="btn btn-light me-2">Начать консультировать</Link>
                </div>
              </div>

                <div className='advantages-container advantages-client' style={{marginTop:90, backgroundColor:"#fcfcfc"}}>
                <h3 className="display-6" style={{fontWeight:500}}>Преимущества для клиентов</h3>
                <p className="lead" style={{marginTop:20}}>Получите профессиональную помощь быстро и удобно</p>
                <div className='adv-container'>
                  <div className='adv-block'>
                  <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="currentColor" class="bi bi-people" viewBox="0 0 16 16"  style={{marginBottom:15}}>
                  <path d="M15 14s1 0 1-1-1-4-5-4-5 3-5 4 1 1 1 1zm-7.978-1L7 12.996c.001-.264.167-1.03.76-1.72C8.312 10.629 9.282 10 11 10c1.717 0 2.687.63 3.24 1.276.593.69.758 1.457.76 1.72l-.008.002-.014.002zM11 7a2 2 0 1 0 0-4 2 2 0 0 0 0 4m3-2a3 3 0 1 1-6 0 3 3 0 0 1 6 0M6.936 9.28a6 6 0 0 0-1.23-.247A7 7 0 0 0 5 9c-4 0-5 3-5 4q0 1 1 1h4.216A2.24 2.24 0 0 1 5 13c0-1.01.377-2.042 1.09-2.904.243-.294.526-.569.846-.816M4.92 10A5.5 5.5 0 0 0 4 13H1c0-.26.164-1.03.76-1.724.545-.636 1.492-1.256 3.16-1.275ZM1.5 5.5a3 3 0 1 1 6 0 3 3 0 0 1-6 0m3-2a2 2 0 1 0 0 4 2 2 0 0 0 0-4"/>
                </svg>
                  <h3 className="display-8" style={{fontWeight:500}}>Проверенные эксперты</h3>
                  <p className="lead" style={{marginTop:20}}>Все консультанты проходят тщательную проверку документов и квалификации</p>
                  </div>

                  <div className='adv-block'>
                  <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="currentColor" class="bi bi-clock" viewBox="0 0 16 16"  style={{marginBottom:15}}>
                    <path d="M8 3.5a.5.5 0 0 0-1 0V9a.5.5 0 0 0 .252.434l3.5 2a.5.5 0 0 0 .496-.868L8 8.71z"/>
                    <path d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16m7-8A7 7 0 1 1 1 8a7 7 0 0 1 14 0"/>
                  </svg>
                  <h3 className="display-8" style={{fontWeight:500}}>Удобное время</h3>
                  <p className="lead" style={{marginTop:20}}>Выберите удобное время для консультации</p>
                  </div>

                  <div className='adv-block'>
                  <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="currentColor" class="bi bi-shield" viewBox="0 0 16 16" style={{marginBottom:15}}>
                    <path d="M5.338 1.59a61 61 0 0 0-2.837.856.48.48 0 0 0-.328.39c-.554 4.157.726 7.19 2.253 9.188a10.7 10.7 0 0 0 2.287 2.233c.346.244.652.42.893.533q.18.085.293.118a1 1 0 0 0 .101.025 1 1 0 0 0 .1-.025q.114-.034.294-.118c.24-.113.547-.29.893-.533a10.7 10.7 0 0 0 2.287-2.233c1.527-1.997 2.807-5.031 2.253-9.188a.48.48 0 0 0-.328-.39c-.651-.213-1.75-.56-2.837-.855C9.552 1.29 8.531 1.067 8 1.067c-.53 0-1.552.223-2.662.524zM5.072.56C6.157.265 7.31 0 8 0s1.843.265 2.928.56c1.11.3 2.229.655 2.887.87a1.54 1.54 0 0 1 1.044 1.262c.596 4.477-.787 7.795-2.465 9.99a11.8 11.8 0 0 1-2.517 2.453 7 7 0 0 1-1.048.625c-.28.132-.581.24-.829.24s-.548-.108-.829-.24a7 7 0 0 1-1.048-.625 11.8 11.8 0 0 1-2.517-2.453C1.928 10.487.545 7.169 1.141 2.692A1.54 1.54 0 0 1 2.185 1.43 63 63 0 0 1 5.072.56"/>
                  </svg>
                  <h3 className="display-8" style={{fontWeight:500}}>Гарантия качества</h3>
                  <p className="lead" style={{marginTop:20}}>Возврат средств, если консультация не соответствует ожиданиям</p>
                  </div>
                </div>
              </div>
              

                <div className='advantages-container advantages-consultant'>
                <h3 className="display-6" style={{fontWeight:500}}>Преимущества для консультантов</h3>
                <p className="lead" style={{marginTop:20}}>Развивайте свой бизнес и помогайте людям.</p>
                <div className='adv-container'>
                  <div className='adv-block'>
                  <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="currentColor" class="bi bi-graph-up-arrow" viewBox="0 0 16 16"  style={{marginBottom:15}}>
                <path fill-rule="evenodd" d="M0 0h1v15h15v1H0zm10 3.5a.5.5 0 0 1 .5-.5h4a.5.5 0 0 1 .5.5v4a.5.5 0 0 1-1 0V4.9l-3.613 4.417a.5.5 0 0 1-.74.037L7.06 6.767l-3.656 5.027a.5.5 0 0 1-.808-.588l4-5.5a.5.5 0 0 1 .758-.06l2.609 2.61L13.445 4H10.5a.5.5 0 0 1-.5-.5"/>
              </svg>
                  <h3 className="display-8" style={{fontWeight:500}}>Высокий доход</h3>
                  <p className="lead" style={{marginTop:20}}>Устанавливайте свои цены и получайте до 95% от стоимости консультаций</p>
                  </div>

                  <div className='adv-block'>
                  <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="currentColor" class="bi bi-people" viewBox="0 0 16 16"  style={{marginBottom:15}}>
                  <path d="M15 14s1 0 1-1-1-4-5-4-5 3-5 4 1 1 1 1zm-7.978-1L7 12.996c.001-.264.167-1.03.76-1.72C8.312 10.629 9.282 10 11 10c1.717 0 2.687.63 3.24 1.276.593.69.758 1.457.76 1.72l-.008.002-.014.002zM11 7a2 2 0 1 0 0-4 2 2 0 0 0 0 4m3-2a3 3 0 1 1-6 0 3 3 0 0 1 6 0M6.936 9.28a6 6 0 0 0-1.23-.247A7 7 0 0 0 5 9c-4 0-5 3-5 4q0 1 1 1h4.216A2.24 2.24 0 0 1 5 13c0-1.01.377-2.042 1.09-2.904.243-.294.526-.569.846-.816M4.92 10A5.5 5.5 0 0 0 4 13H1c0-.26.164-1.03.76-1.724.545-.636 1.492-1.256 3.16-1.275ZM1.5 5.5a3 3 0 1 1 6 0 3 3 0 0 1-6 0m3-2a2 2 0 1 0 0 4 2 2 0 0 0 0-4"/>
                  </svg>
                  <h3 className="display-8" style={{fontWeight:500}}>Готовая аудитория</h3>
                  <p className="lead" style={{marginTop:20}}>Доступ к тысячам клиентов, которые ищут именно ваши услуги</p>
                  </div>

                  <div className='adv-block'>
                  <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="currentColor" class="bi bi-star" viewBox="0 0 16 16" style={{marginBottom:15}}>
                  <path d="M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.522-3.356c.33-.314.16-.888-.282-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.12l-4.898.696c-.441.062-.612.636-.283.95l3.523 3.356-.83 4.73zm4.905-2.767-3.686 1.894.694-3.957a.56.56 0 0 0-.163-.505L1.71 6.745l4.052-.576a.53.53 0 0 0 .393-.288L8 2.223l1.847 3.658a.53.53 0 0 0 .393.288l4.052.575-2.906 2.77a.56.56 0 0 0-.163.506l.694 3.957-3.686-1.894a.5.5 0 0 0-.461 0z"/>
                  </svg>
                  <h3 className="display-8" style={{fontWeight:500}}>Репутация</h3>
                  <p className="lead" style={{marginTop:20}}>Система рейтингов и отзывов поможет построить профессиональную репутацию</p>
                  </div>
                </div>
              </div>

              
              <div className='advantages-container advantages-consultant' style={{ backgroundColor:"#fcfcfc"}}>
                <h3 className="display-6" style={{fontWeight:500}}>Как это работает</h3>
                <div className='adv-container'>
                  <div className='borderNot adv-block'>
                  <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="currentColor" class="bi bi-1-circle-fill" viewBox="0 0 16 16"  style={{marginBottom:15}}>
                  <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0M9.283 4.002H7.971L6.072 5.385v1.271l1.834-1.318h.065V12h1.312z"/>
                </svg>
                  <h3 className="display-8" style={{fontWeight:500}}>Высокий доход</h3>
                  <p className="lead" style={{marginTop:20}}>Устанавливайте свои цены и получайте до 95% от стоимости консультаций</p>
                  </div>

                  <div className='borderNot adv-block'>
                  <svg xmlns="http://www.w3.org/2000/svg"  width="64" height="64"  fill="currentColor" class="bi bi-2-circle-fill" viewBox="0 0 16 16"  style={{marginBottom:15}}>
                  <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0M6.646 6.24c0-.691.493-1.306 1.336-1.306.756 0 1.313.492 1.313 1.236 0 .697-.469 1.23-.902 1.705l-2.971 3.293V12h5.344v-1.107H7.268v-.077l1.974-2.22.096-.107c.688-.763 1.287-1.428 1.287-2.43 0-1.266-1.031-2.215-2.613-2.215-1.758 0-2.637 1.19-2.637 2.402v.065h1.271v-.07Z"/>
                </svg>
                  <h3 className="display-8" style={{fontWeight:500}}>Готовая аудитория</h3>
                  <p className="lead" style={{marginTop:20}}>Доступ к тысячам клиентов, которые ищут именно ваши услуги</p>
                  </div>

                  <div className='borderNot adv-block'>
                  <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64"  fill="currentColor" class="bi bi-3-circle-fill" viewBox="0 0 16 16" style={{marginBottom:15}}>
                  <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0m-8.082.414c.92 0 1.535.54 1.541 1.318.012.791-.615 1.36-1.588 1.354-.861-.006-1.482-.469-1.54-1.066H5.104c.047 1.177 1.05 2.144 2.754 2.144 1.653 0 2.954-.937 2.93-2.396-.023-1.278-1.031-1.846-1.734-1.916v-.07c.597-.1 1.505-.739 1.482-1.876-.03-1.177-1.043-2.074-2.637-2.062-1.675.006-2.59.984-2.625 2.12h1.248c.036-.556.557-1.054 1.348-1.054.785 0 1.348.486 1.348 1.195.006.715-.563 1.237-1.342 1.237h-.838v1.072h.879Z"/>
                </svg>
                  <h3 className="display-8" style={{fontWeight:500}}>Репутация</h3>
                  <p className="lead" style={{marginTop:20}}>Система рейтингов и отзывов поможет построить профессиональную репутацию</p>
                  </div>
                </div>
              </div>


                <div className='advantages-container start' style={{marginTop:60, height:300, marginLeft:20}}>
                <h3 className="display-6" style={{fontWeight:500}}>Начните прямо сейчас</h3>
                <p className="lead" style={{marginTop:20}}>Присоединяйтесь к тысячам довольных пользователей нашей платформы</p>
                <div className='buttons' style={{marginTop:30}}>
                  <Link to="/registration" className="btn btn-dark me-2">Найти консультанта</Link>
                  <Link to="/registration"  className="btn btn-light me-2">Стать консультантом</Link>
                </div>
                </div>
              </div>
    );
  };
}