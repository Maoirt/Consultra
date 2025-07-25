import React, { useState } from 'react';
import { request } from '../../helpers/axios_helper';

const AdminRegister = () => {
  const [form, setForm] = useState({
    email: '',
    firstName: '',
    lastName: '',
    phone: '',
    password: ''
  });
  const [message, setMessage] = useState('');

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async e => {
    e.preventDefault();
    try {
      await request('post', '/register', { ...form, role: 'ADMIN' });
      setMessage('Админ успешно зарегистрирован! Теперь войдите через форму входа.');
    } catch (err) {
      setMessage('Ошибка регистрации');
    }
  };

  return (
    <div>
      <h2>Регистрация администратора</h2>
      <form onSubmit={handleSubmit}>
        <input name="email" placeholder="Email" value={form.email} onChange={handleChange} required />
        <input name="firstName" placeholder="Имя" value={form.firstName} onChange={handleChange} required />
        <input name="lastName" placeholder="Фамилия" value={form.lastName} onChange={handleChange} required />
        <input name="phone" placeholder="Телефон" value={form.phone} onChange={handleChange} required />
        <input name="password" type="password" placeholder="Пароль" value={form.password} onChange={handleChange} required />
        <button type="submit">Зарегистрировать админа</button>
      </form>
      {message && <div>{message}</div>}
    </div>
  );
};

export default AdminRegister; 