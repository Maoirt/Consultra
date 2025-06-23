import React, { useEffect, useState } from 'react';
import { request } from '../helpers/axios_helper';
import './ConsultantPage.css';

const TABS = [
  { key: 'services', label: 'Услуги' },
  { key: 'reviews', label: 'Отзывы' },
  { key: 'documents', label: 'Документы' },
  { key: 'cases', label: 'Кейсы' },
];

export default function ConsultantPage({ consultantId }) {
  const [consultant, setConsultant] = useState(null);
  const [tab, setTab] = useState('services');
  const [services, setServices] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [documents, setDocuments] = useState([]);

  useEffect(() => {
    request('GET', `/consultant/${consultantId}`).then(r => setConsultant(r.data));
    request('GET', `/consultant/${consultantId}/services`).then(r => setServices(r.data));
    request('GET', `/consultant/${consultantId}/reviews`).then(r => setReviews(r.data));
    request('GET', `/consultant/${consultantId}/documents`).then(r => setDocuments(r.data));
  }, [consultantId]);

  if (!consultant) return <div>Загрузка...</div>;

  return (
    <div className="consultant-page">
      <div className="consultant-header">
        <img className="avatar" src={consultant.avatarUrl || '/default-avatar.png'} alt="avatar" />
        <div className="info">
          <h2>{consultant.name || 'Имя не указано'}</h2>
          <div>Город: {consultant.city || '-'}</div>
          <div>Опыт: {consultant.experienceYears || 0} лет</div>
          <div>Специализации: {/* TODO: specialization names */}</div>
          <div>Консультаций: {/* TODO: consultations count */}</div>
          <div>Рейтинг: {/* TODO: rating */}</div>
        </div>
      </div>
      <div className="about-block">
        <h3>О себе</h3>
        <div>{consultant.about || '—'}</div>
      </div>
      <div className="tabs">
        {TABS.map(t => (
          <button key={t.key} className={tab === t.key ? 'active' : ''} onClick={() => setTab(t.key)}>{t.label}</button>
        ))}
      </div>
      <div className="tab-content">
        {tab === 'services' && (
          <ul>
            {services.map(s => <li key={s.id}>{s.name} — {s.price}₽<br/>{s.description}</li>)}
          </ul>
        )}
        {tab === 'reviews' && (
          <ul>
            {reviews.map(r => <li key={r.id}>{r.rating}★ — {r.text}</li>)}
          </ul>
        )}
        {tab === 'documents' && (
          <ul>
            {documents.map(d => <li key={d.id}><a href={d.fileUrl} target="_blank" rel="noopener noreferrer">{d.type}</a> — {d.description}</li>)}
          </ul>
        )}
        {tab === 'cases' && (
          <div>Кейсы пока не реализованы</div>
        )}
      </div>
    </div>
  );
} 