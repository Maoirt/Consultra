import React, { useEffect, useState, useRef } from 'react';
import { request } from '../../helpers/axios_helper';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { v4 as uuidv4 } from 'uuid';

const WS_URL = 'http://localhost:8080/ws-chat';

function getChatId(userId, consultantId) {
  return [userId, consultantId].sort().join('-');
}

function isValidUUID(uuid) {
  // проверяем формат UUID
  return typeof uuid === 'string' && /^[0-9a-fA-F-]{36}$/.test(uuid);
}

export default function ConsultantChatsPage({ consultantId }) {
  const [chats, setChats] = useState([]);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [messagesByChat, setMessagesByChat] = useState({});
  const [input, setInput] = useState('');
  const stompClient = useRef(null);
  const [userMap, setUserMap] = useState({});
  const [loadedChats, setLoadedChats] = useState({});

  useEffect(() => {
    request('GET', `/consultant/${consultantId}/chats`).then(r => {

      const chatIds = r.data.chatIds || [];
      setChats(chatIds);
      Promise.all(chatIds.map(uid => request('GET', `/api/users/${uid}`)))
        .then(users => {
          const map = {};
          users.forEach(u => { map[u.data.id] = u.data; });
          setUserMap(map);
        })
        .catch(error => {
          console.error('Error loading user data:', error);
        });
    }).catch(error => {
      console.error('Error loading consultant chats:', error);
      setChats([]);
    });
  }, [consultantId]);

  useEffect(() => {
    if (!isValidUUID(consultantId)) {
      alert('Ошибка: consultantId невалидный!');
      return;
    }
    const socket = new SockJS(WS_URL);
    const client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      connectHeaders: {
        Authorization: 'Bearer ' + localStorage.getItem('auth_token')
      }
    });
    client.onConnect = () => {
      client.subscribe(`/user/${consultantId}/queue/messages`, (msg) => {
        const message = JSON.parse(msg.body);
        const chatId = message.chatId;
        setMessagesByChat(prev => {
          const existingMessages = prev[chatId] || [];

          // проверяем дубликаты
          const isDuplicate = existingMessages.some(m => m.id === message.id || m.tempId === message.tempId);
          if (!prev[chatId]) {
            setLoadedChats(loaded => ({ ...loaded, [chatId]: true }));
          }
          if (isDuplicate) return prev;
          return {
            ...prev,
            [chatId]: [...existingMessages, message]
          };
        });
      });
    };
    client.activate();
    stompClient.current = client;
    return () => client.deactivate();
  }, [consultantId]);

  useEffect(() => {
    if (!selectedUserId || !consultantId) return;
    const chatId = getChatId(selectedUserId, consultantId);
    if (!loadedChats[chatId]) {
      request('GET', `/chat/${chatId}`)
        .then(r => {
          setMessagesByChat(prev => ({
            ...prev,
            [chatId]: r.data
          }));
          setLoadedChats(prev => ({ ...prev, [chatId]: true }));
        })
        .catch(error => {
          console.error('Ошибка загрузки истории чата:', error);
          setLoadedChats(prev => ({ ...prev, [chatId]: true }));
        });
    }
  }, [selectedUserId, consultantId]);

  const sendMessage = () => {
    if (!input.trim() || !isValidUUID(selectedUserId) || !isValidUUID(consultantId)) {
      alert('Ошибка: некорректный userId или consultantId!');
      return;
    }
    const chatId = getChatId(selectedUserId, consultantId);
    const msg = {
      fromId: consultantId,
      toId: selectedUserId,
      content: input,
      chatId: chatId,
      tempId: uuidv4(),
      timestamp: new Date().toISOString()
    };
    setMessagesByChat(prev => ({
      ...prev,
      [chatId]: [...(prev[chatId] || []), msg]
    }));
    setInput('');
    try {
      stompClient.current.publish({ destination: '/app/chat.sendMessage', body: JSON.stringify(msg) });
    } catch (e) {
      alert('Ошибка отправки сообщения. Попробуйте ещё раз.');
    }
  };

  const currentChatId = selectedUserId && consultantId ? getChatId(selectedUserId, consultantId) : null;
  const filteredMessages = currentChatId && messagesByChat[currentChatId] ? messagesByChat[currentChatId] : [];

  return (
    <div style={{ display: 'flex', height: '80vh', background: '#f4f6fa' }}>
      <aside style={{ width: 300, borderRight: '1px solid #e0e0e0', padding: 24, background: '#fff', boxShadow: '2px 0 8px #f0f0f0' }}>
        <h2 style={{ marginBottom: 16, fontWeight: 700, fontSize: 22 }}>Мои чаты</h2>
        {chats.length === 0 ? (
          <div style={{ color: '#888', marginTop: 32, textAlign: 'center' }}>
            <div style={{ fontSize: 48, marginBottom: 8 }}>💬</div>
            <div>У вас пока нет чатов.<br/>Пользователи смогут написать вам из профиля.</div>
          </div>
        ) : (
          <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
            {chats.map(uid => (
              <li key={uid} style={{ marginBottom: 8 }}>
                <button
                  onClick={() => setSelectedUserId(uid)}
                  style={{
                    width: '100%',
                    background: selectedUserId === uid ? '#e3f2fd' : '#fff',
                    border: selectedUserId === uid ? '2px solid #1976d2' : '1px solid #e0e0e0',
                    borderRadius: 8,
                    padding: '10px 12px',
                    fontWeight: 500,
                    color: '#222',
                    cursor: 'pointer',
                    transition: 'all 0.2s',
                  }}
                >
                  <span style={{ fontWeight: 600 }}>
                    {userMap[uid]?.firstName || userMap[uid]?.lastName
                      ? `${userMap[uid]?.firstName || ''} ${userMap[uid]?.lastName || ''}`.trim()
                      : userMap[uid]?.email
                        ? userMap[uid].email
                        : userMap[uid]?.username
                          ? userMap[uid].username
                          : 'Пользователь'}
                  </span>
                </button>
              </li>
            ))}
          </ul>
        )}
      </aside>
      <main style={{ flex: 1, display: 'flex', flexDirection: 'column', background: '#f4f6fa' }}>
        {!selectedUserId ? (
          <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#888', fontSize: 20 }}>
            {chats.length === 0
              ? 'Нет чатов для отображения.'
              : 'Выберите чат слева, чтобы начать общение.'}
          </div>
        ) : (
          <>
            <div style={{ flex: 1, overflowY: 'auto', padding: 24, background: '#f9f9f9', borderBottom: '1px solid #e0e0e0' }}>
              {!loadedChats[currentChatId] && filteredMessages.length === 0 && (
                <div style={{ color: '#bbb', textAlign: 'center', marginTop: 40 }}>Загрузка истории сообщений...</div>
              )}
              {filteredMessages.length === 0 ? (
                <div style={{ color: '#bbb', textAlign: 'center', marginTop: 40 }}>Нет сообщений в этом чате.</div>
              ) : (
                filteredMessages.map((m, i) => (
                  <div key={m.tempId || m.id || i} style={{ textAlign: m.fromId === consultantId ? 'right' : 'left', margin: '8px 0' }}>
                    <span style={{ background: m.fromId === consultantId ? '#d1e7dd' : '#fff', padding: 10, borderRadius: 12, display: 'inline-block', maxWidth: 400, wordBreak: 'break-word', boxShadow: '0 1px 4px #eee' }}>{m.content}</span>
                  </div>
                ))
              )}
            </div>
            <div style={{ padding: 20, borderTop: '1px solid #e0e0e0', display: 'flex', gap: 8, background: '#fff' }}>
              <input
                value={input}
                onChange={e => setInput(e.target.value)}
                style={{ flex: 1, borderRadius: 8, border: '1px solid #ccc', padding: 10, fontSize: 16 }}
                placeholder="Введите сообщение..."
                onKeyDown={e => { if (e.key === 'Enter') sendMessage(); }}
              />
              <button
                onClick={sendMessage}
                style={{ background: '#1976d2', color: '#fff', border: 'none', borderRadius: 8, padding: '10px 24px', fontWeight: 600, fontSize: 16, cursor: 'pointer' }}
              >
                Отправить
              </button>
            </div>
          </>
        )}
      </main>
    </div>
  );
} 