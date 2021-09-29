package com.example.spring_problem.service;

import java.io.File;
import java.io.InputStream;

/**
 * Интерфейс сервиса, который передаёт хранилищу команды и получает ответы
 * через {@link com.example.spring_problem.dao.DataDAO}
 */
public interface DataService {
    /**
     * Возвращает значение, хранящееся в хранилище по заданному ключу
     * @param key ключ для хранилища
     * @return возвращает данные, хранящиеся по заданному ключу
     */
    String get(int key);

    /**
     * Помещает в хранилище данные по заданному ключу с заданным временем жизни
     * @param key ключ для хранилища
     * @param data данные для хранилища
     * @param ttl время жизни данных
     */
    void put(int key, String data, long ttl);

    /**
     * Помещает в хранилище данные по заданному ключу с временем жизни по умолчанию
     * @param key ключ для хранилища
     * @param data данные для хранилища
     */
    void put(int key, String data);

    /**
     * Удаляет данные из хранилища и возвращает их в виде строки
     * @param key ключ для хранилища
     * @return возвращает данные, хранящиеся по заданному ключу
     */
    String remove(int key);

    /**
     * Возвращает сохранённое состояние хранилища в виде .dat файла
     * @return возвращает файл, в котором хранится сериализованное хранилище
     */
    File dump();

    /**
     * Загружает состояние хранилища из входного потока данных
     * @param loadInputStream входной поток данных
     */
    void load(InputStream loadInputStream);
}
