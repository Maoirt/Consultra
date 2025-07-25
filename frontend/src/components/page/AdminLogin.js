import React, { useState } from 'react';
import { request, setAuthHeader } from '../../helpers/axios_helper';

const AdminLogin = () => {
  const [form, setForm] = useState({ email: '', password: '' });
  const [message, setMessage] = useState('');

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async e => {
    e.preventDefault();
    try {
      const res = await request('post', '/login', form);
      if (res.data.role === 'ADMIN') {
        setAuthHeader(res.data.token);
        localStorage.setItem('user', JSON.stringify(res.data));
        localStorage.setItem('userId', res.data.id); // сохраняем userId
        setMessage('Вход выполнен. Добро пожаловать, админ!');
        window.location.href = '/admin';
      } else {
        setMessage('У вас нет прав администратора!');
      }
    } catch (err) {
      setMessage('Ошибка входа');
    }
  };

  return (
    <div>
      <h2>Вход администратора</h2>
      <form onSubmit={handleSubmit}>
        <input name="email" placeholder="Email" value={form.email} onChange={handleChange} required />
        <input name="password" type="password" placeholder="Пароль" value={form.password} onChange={handleChange} required />
        <button type="submit">Войти</button>
      </form>
      {message && <div>{message}</div>}
    </div>
  );
};

export default AdminLogin; 