import * as React from 'react';
import "./modal.css"
import { useState } from 'react';
import { request } from '../../helpers/axios_helper';

const Modal = ({ active, setActive, consultantId, onServiceAdded }) => {

    const [formData, setFormData] = useState({
        name: '',
        price: '',
        description: ''
    });
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleInputChange = (e) =>{
        const {name, value} = e.target;
        setFormData(prev =>({
            ...prev,
            [name]:value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError(null);

        try{
            const response = await request(
                'POST', 
                `/consultant/${consultantId}/services`,
                {
                    name: formData.name,
                    price: Number(formData.price),
                    description: formData.description
                }
            );

            if(onServiceAdded){
                onServiceAdded(response.data);
            }

            setActive(false);
            
            setFormData({
                name:'',
                price:'',
                description:''
            });
        } catch (err) {
            setError("Ошибка при добавлении услуги");
        } finally{
            setIsLoading(false);
        }
    }


    return (
        <div 
            className={active ? "modal active" : "modal"} 
            onClick={() => setActive(false)}
        >
            <div className='modal__content' onClick={e => e.stopPropagation()}>
                <h2>Добавить консультацию</h2>

                {error && <div className="error-message">{error}</div>}
                
                <form className="consultation-form" onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Название услуги</label>
                        <input 
                            type="text" 
                            name='name'
                            className="form-input"
                            placeholder="Введите название услуги"
                            value={formData.name}
                            onChange={handleInputChange}
                            required
                        />
                    </div>
                    
                    <div className="form-group">
                        <label>Цена (руб.)</label>
                        <input 
                            type="number" 
                            name='price'
                            className="form-input"
                            placeholder="Введите цену"
                            value={formData.price}
                            onChange={handleInputChange}
                            required
                        />
                    </div>
                    
                    <div className="form-group">
                        <label>Описание</label>
                        <textarea 
                            className="form-textarea"
                            name='description'
                            placeholder="Подробное описание услуги..."
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