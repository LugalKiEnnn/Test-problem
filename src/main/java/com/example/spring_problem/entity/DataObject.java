package com.example.spring_problem.entity;

/**
 * Класс объекта, в который записываются передаваемые в put-запросе данные
 * @see com.example.spring_problem.controller.DataControllers#setData(Integer, Long, DataObject)
 */
public class DataObject {
    /**
     * Поле, хранящее значение ключа для базы данных
     */
    private int key;
    /**
     * Поле, хранящее данные, которые будут переданы в базу данных
     */
    private String data;
    /**
     * Поле, хранящее переданное время жизни данных в базе данных
     */
    private long ttl;

    /**
     * Пустой конструктор
     */
    public DataObject() {

    }

    /**
     * Конструктор создания нового объекта класса
     * @param key значение ключа
     * @param data данные для хранилища
     * @param ttl время жизни данных
     */
    public DataObject(int key, String data, long ttl) {
        this.key = key;
        this.data = data;
        this.ttl = ttl;
    }

    /**
     * Метод, возвращающий значение ключа для хранилища
     * @return возвращает значение ключа
     */
    public int getKey() {
        return key;
    }

    /**
     * Метод, устанавливающий новое значение ключа
     * @param key значение ключа
     */
    public void setKey(int key) {
        this.key = key;
    }

    /**
     * Метод, возвращающий хранящиеся в объекте данные
     * @return возвращает данные для хранилища
     */
    public String getData() {
        return data;
    }

    /**
     * Метод, устанавливающий новое значение данных для хранилища
     * @param data данные для хранилища
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Метод, возвращающий время жизни данных
     * @return возвращает время жизни данных
     */
    public long getTtl() {
        return ttl;
    }

    /**
     * Метод, устанавливающий новое значение времени жизни данных
     * @param ttl время жизни данных
     */
    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    @Override
    public String toString() {
        return "DataObject{" +
                "key=" + key +
                ", data='" + data + '\'' +
                ", ttl=" + ttl +
                '}';
    }
}
