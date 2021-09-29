package com.example.spring_problem.dao;

import java.io.File;
import java.io.InputStream;

/**
 * Интерфейс доступа к хранилищу {@link com.example.spring_problem.database.Database}
 */
public interface DataDAO {
    /**
     * Возвращает значение, хранящееся в хранилище по заданному ключу
     * @see DataDAOImpl#get(int)
     * @param key ключ для хранилища
     * @return возвращает данные, хранящиеся по заданному ключу
     */
    String get(int key);

    /**
     * Помещает в хранилище данные по заданному ключу с заданным временем жизни
     * @see DataDAOImpl#put(int, String, long)
     * @param key ключ для хранилища
     * @param data данные для хранилища
     * @param ttl время жизни данных
     */
    void put(int key, String data, long ttl);

    /**
     * Помещает в хранилище данные по заданному ключу с временем жизни по умолчанию
     * @see DataDAOImpl#put(int, String)
     * @param key ключ для хранилища
     * @param data данные для хранилища
     */
    void put(int key, String data);

    /**
     * Удаляет данные из хранилища и возвращает их в виде строки
     * @see DataDAOImpl#remove(int)
     * @param key ключ для хранилища
     * @return возвращает данные, хранящиеся по заданному ключу
     */
    String remove(int key);

    /**
     * Сериализует хранилище и записывает в файл
     * @see DataDAOImpl#dump()
     * @return возвращает файл, в котором хранится сериализованное хранилище
     */
    File dump();

    /**
     * Передаёт входной поток, в котором содержится объект, для десериализации и загрузки в хранилище
     * @see DataDAOImpl#load(InputStream)
     * @param loadInputStream входной поток данных
     */
    void load(InputStream loadInputStream);
}
