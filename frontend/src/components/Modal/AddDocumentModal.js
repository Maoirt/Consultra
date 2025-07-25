import * as React from 'react';
import "./modal.css"
import { useState } from 'react';
import { request } from '../../helpers/axios_helper';



const Modal = ({ active, setActive, consultantId, onServiceAdded }) => {
    console.log('Modal component - active:', active);

    const [formData, setFormData] = useState({
        name: '',
        fileUrl: '',
        type: '',
        description: ''
    });
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const [selectedFile, setSelectedFile] = useState(null);

    const handleInputChange = (e) =>{
        const {name, value} = e.target;
        setFormData(prev =>({
            ...prev,
            [name]:value
        }));
    };

    const handleFileSelect = (e) => {
        setSelectedFile(e.target.files[0]);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError(null);

        if (!selectedFile) {
            setError("Пожалуйста, выберите файл");
            setIsLoading(false);
            return;
        }

        try {
            const formDataToSend = new FormData();
            formDataToSend.append('file', selectedFile);
            formDataToSend.append('name', selectedFile.name)
            formDataToSend.append('type', selectedFile.type);
            formDataToSend.append('description', formData.description);

            const response = await request(
                'POST',
                `/consultant/${consultantId}/documents`,
                formDataToSend
                
            );

            if (onServiceAdded) {
                onServiceAdded(response.data);
            }

            setActive(false);
            setFormData({
                fileUrl: '',
                type: '',
                description: ''
            });
            setSelectedFile(null);
        } catch (err) {
            setError("Ошибка при добавлении документа");
        } finally {
            setIsLoading(false);
        }
    };


    return (
        <div 
            className={active ? "modal active" : "modal"} 
            onClick={() => setActive(false)}
        >
            <div className='modal__content' onClick={e => e.stopPropagation()}>
                <h2>Добавить документ</h2>

                {error && <div className="error-message">{error}</div>}
                
                <form className="consultation-form" onSubmit={handleSubmit} encType="multipart/form-data">
                    <div className="form-group">
                        <input
                            type="file"
                            accept=".pdf,.doc,.docx,.txt,image/*"
                            onChange={handleFileSelect}
                            id="file-input"
                            style={{ display: 'none' }}
                        />
                        <label htmlFor="file-input" className="upload-btn">
                            {selectedFile ? selectedFile.name : 'Выбрать файл'}
                        </label>
                    </div>
                
                    <div className="form-group">
                        <label>Описание</label>
                        <textarea 
                            className="form-textarea"
                            name='description'
                            placeholder="Подробное описание документа..."
                            rows="5"
                            value={formData.description}
                            onChange={handleInputChange}
                            required
                        />
                    </div>
                    
                    <div className="form-actions">
                        <button 
                            type="button" 
                            className="cancel-btn" 
                            onClick={() => setActive(false)}
                            disabled={isLoading}
                        >
                            Закрыть
                        </button>
                        <button 
                            type="submit" 
                            className="submit-btn"
                            disabled={isLoading}
                        >
                            {isLoading ? 'Сохранение...' : 'Сохранить'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default Modal;