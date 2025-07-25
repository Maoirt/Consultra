import React, { useEffect, useState, useRef } from 'react';
import { request } from '../../helpers/axios_helper';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { useParams } from 'react-router-dom';
import { v4 as uuidv4 } from 'uuid';

const WS_URL = 'http://localhost:8081/ws-chat';

function getChatId(userId, consultantId) {
  return [userId, consultantId].sort().join('-');
}

function parseChatId(chatId, userId) {
  if (!chatId || chatId.length < 73) return null;
  const id1 = chatId.slice(0, 36);
  const id2 = chatId.slice(37, 73);
  if (userId === id1) return id2;
  if (userId === id2) return id1;
  return id2;
}
function isValidUUID(uuid) {
  return typeof uuid === 'string' && /^[0-9a-fA-F-]{36}$/.test(uuid);
}

export default function ChatPage({ chatId: propChatId }) {
  const params = useParams();
  const chatIdFromUrl = propChatId || params.chatId;
  const userId = localStorage.getItem('userId');
  const initialConsultantId = chatIdFromUrl && userId ? parseChatId(chatIdFromUrl, userId) : null;
  const [selectedConsultantId, setSelectedConsultantId] = useState(initialConsultantId);
  const [chats, setChats] = useState([]);
  const [messagesByChat, setMessagesByChat] = useState({}); // chatId -> messages[]
  const [input, setInput] = useState('');
  const [consultantMap, setConsultantMap] = useState({});
  const [singleConsultant, setSingleConsultant] = useState(null);
  const stompClient = useRef(null);
  const messagesEndRef = useRef(null);
  const [isConnected, setIsConnected] = useState(false);
  const [loadedChats, setLoadedChats] = useState({}); // chatId -> true/false

  useEffect(() => {
    if (!userId) return;
    request('GET', `/user/${userId}/chats`).then(r => {
      setChats(r.data);
      if (r.data.length > 0 && !selectedConsultantId) {
        setSelectedConsultantId(r.data[0]);
      }
      Promise.all(r.data.map(cid => request('GET', `/consultant/${cid}`)))
        .then(resArr => {
          const map = {};
          resArr.forEach(res => { map[res.data.id] = res.data; });
          setConsultantMap(map);
        });
    });
  }, [userId]);

  useEffect(() => {
    if (selectedConsultantId) {
      console.log('selectedConsultantId updated:', selectedConsultantId);
    }
  }, [selectedConsultantId]);

  useEffect(() => {
    if (!selectedConsultantId && chats.length > 0) {
      setSelectedConsultantId(chats[0]);
      console.log('Fallback: setSelectedConsultantId from chats:', chats[0]);
    }
  }, [chats, selectedConsultantId]);

  useEffect(() => {
    if (chatIdFromUrl && userId) {
      const consultantId = parseChatId(chatIdFromUrl, userId);
      if (consultantId && consultantId !== selectedConsultantId) {
        setSelectedConsultantId(consultantId);
      }
    }
  }, [chatIdFromUrl, userId]);

  useEffect(() => {
    if (!isValidUUID(userId)) {
      alert('Ошибка: userId невалидный!');
      return;
    }
    console.log('userId:', userId, 'selectedConsultantId:', selectedConsultantId);
    console.log('WebSocket subscribe to:', `/user/${userId}/queue/messages`);
    const socket = new SockJS(WS_URL);
    const client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      connectHeaders: {
        Authorization: 'Bearer ' + localStorage.getItem('auth_token')
      }
    });
    client.onConnect = () => {
      setIsConnected(true);
      client.subscribe(`/user/${userId}/queue/messages`, (msg) => {
        const message = JSON.parse(msg.body);
        const chatId = message.chatId;
        setMessagesByChat(prev => {
          const existingMessages = prev[chatId] || [];
          // Проверяем, нет ли дубликатов сообщения
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
    client.onDisconnect = () => setIsConnected(false);
    client.onStompError = () => setIsConnected(false);
    client.activate();
    stompClient.current = client;
    return () => client.deactivate();
  }, [userId]);

  useEffect(() => {
    if (!userId || !selectedConsultantId) return;
    const chatId = getChatId(userId, selectedConsultantId);
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
  }, [selectedConsultantId, userId]);

  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [selectedConsultantId, messagesByChat]);

  const sendMessage = () => {
    if (!input.trim() || !isValidUUID(userId) || !isValidUUID(selectedConsultantId)) {
      alert('Ошибка: некорректный userId или consultantId!');
      alert(userId)
      alert(selectedConsultantId)
      return;
    }
    if (!isConnected) {
      return;
    }
    const chatId = getChatId(userId, selectedConsultantId);
    const msg = {
      fromId: userId,
      toId: selectedConsultantId,
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

  const currentConsultant = selectedConsultantId
    ? (consultantMap[selectedConsultantId] || singleConsultant)
    : null;

  const currentChatId = userId && selectedConsultantId ? getChatId(userId, selectedConsultantId) : null;
  const filteredMessages = currentChatId && messagesByChat[currentChatId] ? messagesByChat[currentChatId] : [];

  return (
    <div style={{ display: 'flex', height: '80vh', background: '#f4f6fa' }}>
      <aside style={{ width: 300, borderRight: '1px solid #e0e0e0', padding: 24, background: '#fff', boxShadow: '2px 0 8px #f0f0f0' }}>
        <h2 style={{ marginBottom: 16, fontWeight: 700, fontSize: 22 }}>Мои чаты</h2>
        {chats.length === 0 ? (
          <div style={{ color: '#888', marginTop: 32, textAlign: 'center' }}>
            <div style={{ fontSize: 48, marginBottom: 8 }}>💬</div>
            <div>У вас пока нет чатов.<br/>Начните диалог с консультантом из поиска.</div>
          </div>
        ) : (
          <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
            {chats.map(cid => (
              <li key={cid} style={{ marginBottom: 8 }}>
                <button
                  onClick={() => setSelectedConsultantId(cid)}
                  style={{
                    width: '100%',
                    background: selectedConsultantId === cid ? '#e3f2fd' : '#fff',
                    border: selectedConsultantId === cid ? '2px solid #1976d2' : '1px solid #e0e0e0',
                    borderRadius: 8,
                    padding: '10px 12px',
                    fontWeight: 500,
                    color: '#222',
                    cursor: 'pointer',
                    transition: 'all 0.2s',
                  }}
                >
                  <span style={{ fontWeight: 600 }}>{consultantMap[cid]?.firstName || 'Консультант'} {consultantMap[cid]?.lastName || ''}</span>
                </button>
              </li>
            ))}
          </ul>
        )}
      </aside>
      <main style={{ flex: 1, display: 'flex', flexDirection: 'column', background: '#f4f6fa' }}>
        {!selectedConsultantId ? (
          <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#888', fontSize: 20 }}>
            {chats.length === 0
              ? 'Нет чатов для отображения.'
              : 'Выберите чат слева, чтобы начать общение.'}
          </div>
        ) : (
          <>
            <div style={{ padding: 24, borderBottom: '1px solid #eee', display: 'flex', alignItems: 'center', gap: 16 }}>
              {currentConsultant && (
                <>
                  <img src={currentConsultant.avatarUrl ? `http://localhost:8081${currentConsultant.avatarUrl}` : '/default-avatar.png'} alt="avatar" style={{ width: 48, height: 48, borderRadius: '50%' }} />
                  <div>
                    <div style={{ fontWeight: 700, fontSize: 18 }}>{(currentConsultant.firstName || currentConsultant.lastName) ? `${currentConsultant.firstName || ''} ${currentConsultant.lastName || ''}`.trim() : (currentConsultant.name || 'Имя не указано')}</div>
                    <div style={{ color: '#888', fontSize: 14 }}>{currentConsultant.profession || ''}</div>
                  </div>
                </>
              )}
            </div>
            <div style={{ flex: 1, overflowY: 'auto', padding: 24, background: '#f9f9f9', borderBottom: '1px solid #e0e0e0' }}>
              {!loadedChats[currentChatId] && filteredMessages.length === 0 && (
                <div style={{ color: '#bbb', textAlign: 'center', marginTop: 40 }}>Загрузка истории сообщений...</div>
              )}
              {filteredMessages.length === 0 ? (
                <div style={{ color: '#bbb', textAlign: 'center', marginTop: 40 }}>Нет сообщений в этом чате.</div>
              ) : (
                filteredMessages.map((m, i) => (
                  <div key={m.tempId || m.id || i} style={{ textAlign: m.fromId === userId ? 'right' : 'left', margin: '8px 0' }}>
                    <span style={{ background: m.fromId === userId ? '#d1e7dd' : '#fff', padding: 10, borderRadius: 12, display: 'inline-block', maxWidth: 400, wordBreak: 'break-word', boxShadow: '0 1px 4px #eee' }}>{m.content}</span>
                  </div>
                ))
              )}
              <div ref={messagesEndRef} />
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
                disabled={!input.trim() || !isConnected}
                style={{ background: isConnected ? '#1976d2' : '#aaa', color: '#fff', border: 'none', borderRadius: 8, padding: '10px 24px', fontWeight: 600, fontSize: 16, cursor: input.trim() && isConnected ? 'pointer' : 'not-allowed' }}
              >
                Отправить
              </button>
              {!isConnected && (
                <span style={{ color: '#888', marginLeft: 12, fontSize: 14 }}>Подключение...</span>
              )}
            </div>
          </>
        )}
      </main>
    </div>
  );
} 