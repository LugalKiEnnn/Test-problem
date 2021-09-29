package com.example.spring_problem.service;

import com.example.spring_problem.dao.DataDAO;
import com.example.spring_problem.exception.MissedDataException;
import com.example.spring_problem.exception.TimeToLeaveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

/**
 * Сервис для связи с хранилищем, реализующий интерфейс {@link DataService}
 */
@Service
public class DataServiceImpl implements DataService {
    /**
     * Поле, содержащее интерфейс DataDAO
     */
    private DataDAO dataDAO;

    /**
     * Конструктор, инициализирует поле DAO, который связывается с хранилищем, с использованием Spring
     * @param dataDAO экземпляр {@link DataDAO}
     */
    @Autowired
    public DataServiceImpl(DataDAO dataDAO) {
        this.dataDAO = dataDAO;
    }

    /**
     * Возвращает данные, которые содержатся в хранилище по переданному ключу
     * @param key значение ключа для хранилища
     * @return возвращает строковые данные
     * @throws MissedDataException если заданный ключ отсутствует, то выбрасывается исключение
     */
    @Override
    public String get(int key) throws MissedDataException {
        return dataDAO.get(key);
    }

    /**
     * Записывает в хранилище данные по переданному ключу с заданным временем жизни
     * @param key значение ключа для хранилища
     * @param data данные для хранилища
     * @param ttl время жизни данных
     * @throws TimeToLeaveException если переданное время жизни не положительно, то выбрасывается исключение
     */
    @Override
    public void put(int key, String data, long ttl) throws TimeToLeaveException  {
        dataDAO.put(key,data,ttl);
    }

    /**
     * Записывает в хранилище данные по переданному ключу с временем жизни по умолчанию
     * @param key значение ключа для хранилища
     * @param data данные для хранилища
     */
    @Override
    public void put(int key, String data) {
        dataDAO.put(key,data);
    }

    /**
     * Удаляет данные, содержащиеся в хранилище, по переданному ключу и возвращает эти данные
     * @param key значение ключа для хранилища
     * @return возвращает удалённые данные
     * @throws MissedDataException если заданный ключ отсутствует, то выбрасывается исключение
     */
    @Override
    public String remove(int key) throws MissedDataException {
        return dataDAO.remove(key);
    }

    /**
     * Возвращает сохранённое состояние хранилища в виде .dat файла
     * @return возвращает сохранённое состояние хранилища в виде .dat файла
     */
    @Override
    public File dump() {
        return dataDAO.dump();
    }

    /**
     * Загружает состояние хранилища из входного потока данных
     * @param loadInputStream входной поток данных
     */
    @Override
    public void load(InputStream loadInputStream) {
        dataDAO.load(loadInputStream);
    }
}
