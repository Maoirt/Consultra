import React, { useState, useEffect } from 'react';
import './SearchPage.css';
import { request } from '../../helpers/axios_helper';
// Mock data based on the image
const consultants = [
  {
    id: 1,
    avatar: 'https://via.placeholder.com/80', 
    name: 'Анна Петрова',
    verified: true,
    specialization: 'Бизнес-консультирование',
    rating: 4.9,
    reviews: 127,
    location: 'Москва',
    tags: ['Стратегия', 'Маркетинг', 'Управление'],
    price: 3500,
  },
  {
    id: 2,
    avatar: 'https://via.placeholder.com/80',
    name: 'Михаил Сидоров',
    verified: true,
    specialization: 'IT-консультации',
    rating: 4.8,
    reviews: 89,
    location: 'Санкт-Петербург',
    tags: ['Разработка', 'DevOps', 'Архитектура'],
    price: 4500,
  },
  {
    id: 3,
    avatar: 'https://via.placeholder.com/80',
    name: 'Елена Козлова',
    verified: true,
    specialization: 'Психология',
    rating: 5,
    reviews: 156,
    location: 'Екатеринбург',
    tags: ['Семейная терапия', 'Тревожность', 'Депрессия'],
    price: 2800,
  },
];

function SearchPage() {
  const [searchTerm, setSearchTerm] = useState('');
  const [profession, setProfession] = useState('');
  const [specialization, setSpecialization] = useState('');
  const [professions, setProfessions] = useState([]);
  const [specializations, setSpecializations] = useState([]);
  const [priceRange, setPriceRange] = useState(10000);
  const [minPrice, setMinPrice] = useState(1000);
  const [maxPrice, setMaxPrice] = useState(10000);
  const [consultants, setConsultants] = useState([]);
  const [minRating, setMinRating] = useState(0);

  useEffect(() => {
    request('GET', '/consultant/professions').then(r => setProfessions(r.data));
    request('GET', '/consultant/specializations').then(r => setSpecializations(r.data));
    fetchConsultants();
  }, []);

  const fetchConsultants = (filters = {}) => {
    const params = new URLSearchParams();
    if (filters.profession) params.append('profession', filters.profession);
    if (filters.specialization) params.append('specializationId', filters.specialization);
    if (filters.minPrice) params.append('minPrice', filters.minPrice);
    if (filters.maxPrice) params.append('maxPrice', filters.maxPrice);
    request('GET', `/consultant/search?${params.toString()}`)
      .then(r => setConsultants(r.data));
  };

  const handleApplyFilters = () => {
    fetchConsultants({
      profession,
      specialization,
      minPrice,
      maxPrice: priceRange
    });
  };

  const handleToProfile = (id) => {
    window.location.href = `/consultant/${id}`;
  };

  return (
    <div className="search-page-container">
      <aside className="filters-sidebar">
        <h3 className="filters-title">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" className="filter-icon"><path d="M3 4.5H21" stroke="#343a40" strokeWidth="2" strokeLinecap="round"/><path d="M6.375 9.75H17.625" stroke="#343a40" strokeWidth="2" strokeLinecap="round"/><path d="M9 15H15" stroke="#343a40" strokeWidth="2" strokeLinecap="round"/><path d="M11.25 20.25H12.75" stroke="#343a40" strokeWidth="2" strokeLinecap="round"/></svg>
          Фильтры
        </h3>
        
        <div className="filter-group">
          <label htmlFor="search-input">Поиск</label>
          <div className="search-input-wrapper">
            <input id="search-input" type="text" placeholder="Найти консультанта..." value={searchTerm} onChange={e => setSearchTerm(e.target.value)} />
          </div>
        </div>

        <div className="filter-group">
          <label htmlFor="profession-select">Профессия</label>
          <select id="profession-select" value={profession} onChange={e => setProfession(e.target.value)}>
            <option value="">Все профессии</option>
            {professions.map(p => (
              <option key={p} value={p}>{p}</option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label htmlFor="specialization-select">Специализация</label>
          <select id="specialization-select" value={specialization} onChange={e => setSpecialization(e.target.value)}>
            <option value="">Все специализации</option>
            {specializations.map(s => (
              <option key={s.id} value={s.id}>{s.name}</option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label htmlFor="price-slider">Цена за консультацию: {minPrice} - {priceRange} ₽</label>
          <input id="price-slider" type="range" min="1000" max="10000" value={priceRange} onChange={e => setPriceRange(Number(e.target.value))} className="price-slider"/>
          <input type="number" min="1000" max={priceRange} value={minPrice} onChange={e => setMinPrice(Number(e.target.value))} style={{width: 80, marginLeft: 8}} />
        </div>

        <div className="filter-group">
          <label htmlFor="rating-select">Минимальный рейтинг</label>
          <select id="rating-select" value={minRating} onChange={e => setMinRating(e.target.value)}>
            <option value="0">Любой рейтинг</option>
            <option value="4">4+ ⭐️</option>
            <option value="3">3+ ⭐️</option>
            <option value="2">2+ ⭐️</option>
          </select>
        </div>

        <button className="btn btn-dark btn-block mb-" onClick={handleApplyFilters}>Применить фильтры</button>
      </aside>

      <main className="results-main">
        <header className="results-header">
          <h2 className="results-count">Найдено консультантов: {consultants.length}</h2>
          <select className="sort-by-select">
            <option>Сортировать по</option>
            <option>Цене</option>
            <option>Рейтингу</option>
          </select>
        </header>

        <div className="consultant-list">
          {(Array.isArray(consultants) ? consultants : []).map(consultant => (
            <div key={consultant.id} className="consultant-card">
              <div className="card-main-content">
                <div className="card-left">
                  <img src={consultant.avatar} alt={consultant.name} className="consultant-avatar" />
                </div>
                <div className="card-center">
                  <div className="name-wrapper">
                    <span className="consultant-name">{(consultant.firstName || consultant.lastName)
                      ? `${consultant.firstName || ''} ${consultant.lastName || ''}`.trim()
                      : (consultant.name || 'Имя не указано')}</span>
                    {consultant.verified && <span className="verified-badge">Проверен</span>}
                  </div>
                  <div className="consultant-spec">{consultant.specialization}</div>
                  <div className="consultant-meta">
                    <span className="rating">⭐️ {consultant.rating} ({consultant.reviews} отзывов)</span>
                    <span className="location">📍 {consultant.location}</span>
                  </div>
                  <div className="consultant-tags">
                    {(Array.isArray(consultant.tags) ? consultant.tags : []).map(tag => <span key={tag} className="tag">{tag}</span>)}
                  </div>
                </div>
                <div className="card-right">
                  <div className="consultant-price">{consultant.price && consultant.price.toLocaleString ? consultant.price.toLocaleString() : consultant.price} ₽</div>
                  <div className="price-label">за консультацию</div>
                </div>
              </div>
              <div className="card-actions">
                <button className="btn btn-dark btn-block mb-" onClick={() => handleToProfile(consultant.id)}>Посмотреть профиль</button>
                <button className="write-message-btn">Написать</button>
              </div>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
}

export default SearchPage;