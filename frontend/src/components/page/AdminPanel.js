import React, { useEffect, useState } from 'react';
import { request } from '../../helpers/axios_helper';

const AdminPanel = () => {
  const [users, setUsers] = useState([]);
  const [consultants, setConsultants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const usersRes = await request('get', '/api/admin/users');
      const consultantsRes = await request('get', '/api/admin/consultants');
      setUsers(usersRes.data);
      setConsultants(consultantsRes.data);
    } catch (e) {
      setError('Ошибка загрузки данных');
    }
    setLoading(false);
  };

  const handleDeleteUser = async (id) => {
    await request('delete', `/api/admin/users/${id}`);
    fetchData();
  };

  const handleBlockUser = async (id, block) => {
    await request('put', `/api/admin/users/${id}/${block ? 'block' : 'unblock'}`);
    fetchData();
  };

  const handleChangeRole = async (id, role) => {
    await request('put', `/api/admin/users/${id}/role?role=${role}`);
    fetchData();
  };

  const handleDeleteConsultant = async (id) => {
    await request('delete', `/api/admin/consultants/${id}`);
    fetchData();
  };

  const handleBlockConsultant = async (id, block) => {
    await request('put', `/api/admin/consultants/${id}/${block ? 'block' : 'unblock'}`);
    fetchData();
  };

  if (loading) return <div>Загрузка...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div>
      <h2>Пользователи</h2>
      <table>
        <thead>
          <tr>
            <th>Email</th>
            <th>Имя</th>
            <th>Роль</th>
            <th>Заблокирован</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          {users.map(user => (
            <tr key={user.id}>
              <td>{user.email}</td>
              <td>{user.firstName} {user.lastName}</td>
              <td>{user.role}</td>
              <td>{user.isBlocked ? 'Да' : 'Нет'}</td>
              <td>
                <button onClick={() => handleDeleteUser(user.id)}>Удалить</button>
                <button onClick={() => handleBlockUser(user.id, !user.isBlocked)}>
                  {user.isBlocked ? 'Разблокировать' : 'Заблокировать'}
                </button>
                <select onChange={e => handleChangeRole(user.id, e.target.value)} value={user.role}>
                  <option value="USER">USER</option>
                  <option value="CONSULTANT">CONSULTANT</option>
                  <option value="ADMIN">ADMIN</option>
                </select>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <h2>Консультанты</h2>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>UserID</th>
            <th>Профессия</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          {consultants.map(consultant => (
            <tr key={consultant.id}>
              <td>{consultant.id}</td>
              <td>{consultant.userId}</td>
              <td>{consultant.profession}</td>
              <td>
                <button onClick={() => handleDeleteConsultant(consultant.id)}>Удалить</button>
                <button onClick={() => handleBlockConsultant(consultant.id, true)}>Заблокировать</button>
                <button onClick={() => handleBlockConsultant(consultant.id, false)}>Разблокировать</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AdminPanel; 