import React, { useEffect, useState, useRef } from 'react';
import { request } from '../../helpers/axios_helper';
import './ConsultantPage.css';
import AddConsultationModal from '../Modal/AddConsultationModal'
import AddDocumentModal from '../Modal/AddDocumentModal'

const TABS = [
  { key: 'services', label: 'Услуги' },
  { key: 'reviews', label: 'Отзывы' },
  { key: 'documents', label: 'Документы' },
  //{ key: 'cases', label: 'Кейсы' },
];

export default function ConsultantPage({ consultantId }) {
  
  const [modalActive, setModalActive] = useState(false)
  const [modalDocumentActive, setModalDocumentActive] = useState(false)
  const [consultant, setConsultant] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({});
  const [tab, setTab] = useState('services');
  const [services, setServices] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [documents, setDocuments] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const [specializations, setSpecializations] = useState([]);
  const [newSpecialization, setNewSpecialization] = useState("");
  const [allSpecializations, setAllSpecializations] = useState([]);
  const [selectedSpecialization, setSelectedSpecialization] = useState("");
  const [specInput, setSpecInput] = useState("");
  const [specSuggestions, setSpecSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const specInputRef = useRef(null);

  const handleServiceAdded = (newService) => {
    setServices(prev => [...prev, newService]);
  };

  const handleDocumentAdded = (newDocument) => {
    setDocuments(prev => [...prev, newDocument]);
  };

  useEffect(() => {
    request('GET', `/consultant/${consultantId}`).then(r => {
      console.log('Loaded consultant data:', r.data);
      setConsultant(r.data);
      setEditForm({
        city: r.data.city || '',
        experienceYears: r.data.experienceYears || 0,
        about: r.data.about || '',
        profession: r.data.profession || ''
      });
    });
    request('GET', `/consultant/${consultantId}/services`).then(r => setServices(r.data));
    request('GET', `/consultant/${consultantId}/reviews`).then(r => setReviews(r.data));
    request('GET', `/consultant/${consultantId}/documents`).then(r => setDocuments(r.data));
    request('GET', `/consultant/${consultantId}/specializations`).then(r => setSpecializations(r.data));
    request('GET', '/consultant/specializations').then(r => setAllSpecializations(r.data));
  }, [consultantId]);

  useEffect(() => {
    if (consultant) {
      const avatarUrl = consultant.avatarUrl ? `http://localhost:8081${consultant.avatarUrl}` : '/default-avatar.png';
      console.log('Avatar URL:', avatarUrl);
      console.log('Consultant avatarUrl from DB:', consultant.avatarUrl);
    }
  }, [consultant, consultantId]);

  // Автодополнение специализаций
  useEffect(() => {
    if (specInput.trim().length === 0) {
      setSpecSuggestions([]);
      return;
    }
    const timeout = setTimeout(() => {
      request('GET', `/consultant/specializations/search?query=${encodeURIComponent(specInput)}`)
        .then(r => setSpecSuggestions(r.data));
    }, 200);
    return () => clearTimeout(timeout);
  }, [specInput]);

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleCancel = () => {
    setIsEditing(false);
    setEditForm({
      city: consultant.city || '',
      experienceYears: consultant.experienceYears || 0,
      about: consultant.about || '',
      profession: consultant.profession || ''
    });
    setSelectedFile(null);
    setPreviewUrl(null);
  };

  const handleSave = async () => {
    try {
      const response = await request('PUT', `/consultant/${consultantId}/profile`, editForm);
      setConsultant(response.data);
      setIsEditing(false);
      alert('Данные успешно обновлены!');
    } catch (error) {
      alert('Ошибка при обновлении данных');
      console.error('Error updating consultant:', error);
    }
  };

  const handleInputChange = (field, value) => {
    setEditForm(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleFileSelect = (event) => {
    const file = event.target.files[0];
    if (file) {
      if (!file.type.startsWith('image/')) {
        alert('Пожалуйста, выберите изображение');
        return;
      }
      
      if (file.size > 2 * 1024 * 1024) {
        alert('Размер файла не должен превышать 2MB');
        return;
      }

      setSelectedFile(file);
      
      const reader = new FileReader();
      reader.onload = (e) => {
        setPreviewUrl(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleAvatarUpload = async () => {
    if (!selectedFile) {
      alert('Пожалуйста, выберите файл');
      return;
    }

    setIsUploading(true);
    try {
      const formData = new FormData();
      formData.append('file', selectedFile);

      console.log('Uploading avatar for consultant:', consultantId);
      console.log('Selected file:', selectedFile.name, selectedFile.size, selectedFile.type);

      const response = await fetch(`http://localhost:8081/consultant/${consultantId}/avatar`, {
        method: 'POST',
        body: formData,
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
        }
      });

      console.log('Upload response status:', response.status);
      const responseText = await response.text();
      console.log('Upload response:', responseText);

      if (response.ok) {
        const consultantResponse = await request('GET', `/consultant/${consultantId}`);
        console.log('Updated consultant data:', consultantResponse.data);
        setConsultant(consultantResponse.data);
        setSelectedFile(null);
        setPreviewUrl(null);
        alert('Аватар успешно загружен!');
      } else {
        alert('Ошибка при загрузке аватара');
      }
    } catch (error) {
      console.error('Error uploading avatar:', error);
      alert('Ошибка при загрузке аватара');
    } finally {
      setIsUploading(false);
    }
  };

  const handleAddSpecialization = async (name) => {
    if (!name) return;
    if (specializations.some(s => s.name.toLowerCase() === name.toLowerCase())) {
      alert('Эта специализация уже добавлена');
      return;
    }
    try {
      const response = await request('POST', `/consultant/${consultantId}/specializations`, { name });
      setSpecializations(prev => [...prev, response.data]);
      setSpecInput("");
      setShowSuggestions(false);
    } catch (error) {
      alert('Ошибка при добавлении специализации');
    }
  };

  const handleToProfile = (id) => {
    const consultantId = localStorage.getItem('consultantId');
    if (consultantId) {
      window.location.href = `/consultant/${consultantId}`;
    } else {
      window.location.href = `/consultant/${id}`;
    }
  };

  if (!consultant) return <div>Загрузка...</div>;


  return (
    
    <div className="consultant-page">
      {console.log('Modal active state:', modalActive)}
      <AddConsultationModal 
                active={modalActive}
                setActive={setModalActive}
                consultantId={consultantId}
                onServiceAdded={handleServiceAdded}
      />
      <AddDocumentModal
        active={modalDocumentActive}
        setActive={setModalDocumentActive}
        consultantId={consultantId}
        onServiceAdded={handleDocumentAdded}
      />
      <div className="consultant-header">
        <div className="avatar-section">
          <img 
            className="avatar" 
            src={previewUrl || (consultant.avatarUrl ? `http://localhost:8081${consultant.avatarUrl}` : '/default-avatar.png')} 
            alt="avatar" 
            onLoad={() => console.log('Avatar loaded successfully')}
            onError={(e) => {
              console.error('Error loading avatar:', e.target.src);
              console.error('Error details:', e);
            }}
          />
          {isEditing && (
            <div className="avatar-upload">
              <input
                type="file"
                accept="image/*"
                onChange={handleFileSelect}
                id="avatar-input"
                style={{ display: 'none' }}
              />
              <label htmlFor="avatar-input" className="upload-btn">
                Выбрать фото
              </label>
              {selectedFile && (
                <button 
                  onClick={handleAvatarUpload} 
                  disabled={isUploading}
                  className="upload-submit-btn"
                >
                  {isUploading ? 'Загрузка...' : 'Загрузить'}
                </button>
              )}
            </div>
          )}
        </div>
        <div className="info">
          <h2>{(consultant.firstName || consultant.lastName) ? `${consultant.firstName || ''} ${consultant.lastName || ''}`.trim() : (consultant.name || 'Имя не указано')}</h2>
          
          {isEditing ? (
            <div className="edit-form">
              <div>
                <label>Город:</label>
                <input 
                  type="text" 
                  value={editForm.city} 
                  onChange={(e) => handleInputChange('city', e.target.value)}
                  placeholder="Введите город"
                />
              </div>
              <div>
                <label>Опыт (лет):</label>
                <input 
                  type="number" 
                  value={editForm.experienceYears} 
                  onChange={(e) => handleInputChange('experienceYears', parseInt(e.target.value) || 0)}
                  min="0"
                  max="50"
                />
              </div>
              <div>
                <label>Специализации:</label>
                <ul style={{display: 'inline', paddingLeft: 0, margin: 0}}>
                  {specializations.map(s => <li key={s.id} style={{display: 'inline', marginRight: 8, listStyle: 'none', background: '#f0f0f0', borderRadius: 4, padding: '2px 8px'}}>{s.name}</li>)}
                </ul>
                <div className="specialization-input" style={{marginTop: 8, display: 'flex', gap: 8, alignItems: 'center', position: 'relative'}}>
                  <input
                    ref={specInputRef}
                    type="text"
                    value={specInput}
                    onChange={e => { setSpecInput(e.target.value); setShowSuggestions(true); }}
                    onFocus={() => setShowSuggestions(true)}
                    onBlur={() => setTimeout(() => setShowSuggestions(false), 150)}
                    placeholder="Введите специализацию..."
                    style={{padding: 8, borderRadius: 4, border: '1px solid #ccc', flex: 1}}
                  />
                  <button
                    onClick={() => handleAddSpecialization(specInput)}
                    className="add-spec-btn"
                    style={{padding: '4px 12px'}}
                    disabled={!specInput.trim()}
                  >Добавить</button>
                  {showSuggestions && specSuggestions.length > 0 && (
                    <ul style={{position: 'absolute', top: 38, left: 0, right: 0, background: '#fff', border: '1px solid #ccc', borderRadius: 4, zIndex: 10, maxHeight: 150, overflowY: 'auto', margin: 0, padding: 0, listStyle: 'none'}}>
                      {specSuggestions.map(s => (
                        <li
                          key={s.id}
                          style={{padding: 8, cursor: 'pointer'}}
                          onMouseDown={() => { handleAddSpecialization(s.name); }}
                        >{s.name}</li>
                      ))}
                    </ul>
                  )}
                </div>
              </div>
              <div>
                <label>Профессия:</label>
                <input 
                  type="text" 
                  value={editForm.profession}
                  onChange={(e) => handleInputChange('profession', e.target.value)}
                  placeholder="Введите профессию"
                />
              </div>
              <div className="edit-buttons">
                <button onClick={handleSave} className="save-btn">Сохранить</button>
                <button onClick={handleCancel} className="cancel-btn">Отмена</button>
              </div>
            </div>
          ) : (
            <div>
              <div>Город: {consultant.city || '-'}</div>
              <div>Опыт: {consultant.experienceYears || 0} лет</div>
              <div>Специализации: {
                <ul style={{display: 'inline', paddingLeft: 0, margin: 0}}>
                  {specializations.map(s => <li key={s.id} style={{display: 'inline', marginRight: 8, listStyle: 'none', background: '#f0f0f0', borderRadius: 4, padding: '2px 8px'}}>{s.name}</li>)}
                </ul>
              }
              </div>
              <div>Профессия: {consultant.profession || '-'}</div>
              <div>Консультаций: Проведено 0 консультаций</div>
              <div>Рейтинг: {/* TODO: rating */}</div>
              <button onClick={handleEdit} className="edit-btn">Редактировать</button>
            </div>
          )}
        </div>
      </div>

      <div className="about-block">
        <h3>О себе</h3>
        {isEditing ? (
          <div>
            <textarea 
              value={editForm.about} 
              onChange={(e) => handleInputChange('about', e.target.value)}
              placeholder="Расскажите о себе..."
              rows="4"
              cols="50"
            />
          </div>
        ) : (
          <div>{consultant.about || '—'}</div>
        )}
      </div>
      
      <div className="tabs">
        {TABS.map(t => (
          <button key={t.key} className={tab === t.key ? 'active' : ''} onClick={() => setTab(t.key)}>{t.label}</button>
        ))}
      </div>
      <div className="tab-content">
        {tab === 'services' && (
          <div>
            <ul>
              {services.map(s => <li key={s.id}>{s.name} — {s.price}₽<br/>{s.description}</li>)}
            </ul>
            {isEditing && (
              <div>
                <button 
                  onClick={() => {
                    console.log('Button clicked, setting modalActive to true');
                    setModalActive(true);
                  }} 
                  className="save-btn"
                >
                  Добавить консультацию
                </button>
              </div>
            )}
          </div>
        )}
        
        {tab === 'reviews' && (
          <ul>
            {reviews.map(r => <li key={r.id}>{r.rating}★ — {r.text}</li>)}
          </ul>
        )}
        {tab === 'documents' && (
          <div>
            <ul>
              {documents.map(d => {
                const fileName = d.fileUrl.split('/').pop();
                return (
                  <li key={d.id}>
                    <a
                      href={`http://localhost:8081${d.fileUrl}`}
                      download={fileName}
                    >
                      {d.name || fileName}
                    </a>
                    {' — '}
                    {d.description}
                  </li>
                );
              })}
            </ul>
            {isEditing && (
              <div>
                <button 
                  onClick={() => setModalDocumentActive(true)}
                  className="save-btn"
                >
                  Добавить документ
                </button>
              </div>
            )}
          </div>
        )}
        {/* {tab === 'cases' && (
          <div>Кейсы пока не реализованы</div>
        )} */}
      </div>
    </div>
  );
} 