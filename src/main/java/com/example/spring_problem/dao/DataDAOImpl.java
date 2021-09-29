package com.example.spring_problem.dao;

import com.example.spring_problem.database.Database;
import com.example.spring_problem.exception.MissedDataException;
import com.example.spring_problem.exception.TimeToLeaveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс, реализующий интерфейс {@link DataDAO}
 */
@Repository
public class DataDAOImpl implements DataDAO {
    /**
     * Поле, хранилище данных
     */
    private Database database;

    /**
     * Создаёт и инициализирует поле хранилищем {@link Database} с помощью Spring
     * @param database хранилище, которое инициализирует Spring
     */
    @Autowired
    public DataDAOImpl(Database database) {
        this.database = database;
    }

    /**
     * Метод обновляет хранилище и озвращает значение, хранящееся в хранилище по переданному ключу
     * @param key ключ для хранилища
     * @return возвращает значение, хранящееся в хранилище по заданному ключу
     * @throws MissedDataException если заданный ключ отсутствует, то выбрасывается исключение
     */
    @Override
    public String get(int key) throws MissedDataException {
        database.update();
        return database.get(key);
    }

    /**
     * Метод обновляет хранилище и кладёт по ключу данные с заданной продолжительностью жизни
     * @param key значение ключа
     * @param data данные для хранилища
     * @param ttl время жизни данных
     * @throws TimeToLeaveException если переданное время жизни не положительно, то выбрасывается исключение
     */
    @Override
    public void put(int key, String data, long ttl) throws TimeToLeaveException {
        database.update();
        if(ttl > 0) {
            database.put(key, data, ttl);
        } else {
            throw new TimeToLeaveException();
        }
    }

    /**
     * Метод обновляет хранилище и кладёт по ключу данные с продолжительностью жизни по умолчанию
     * @param key значение ключа
     * @param data данные для хранилища
     */
    @Override
    public void put(int key, String data) {
        database.update();
        database.put(key, data);
    }

    /**
     * Метод обновляет хранилище и удаляет данные по ключу, при этом возвращая их
     * @param key значение ключа
     * @return возвращает данные, которые были удалены в хранилище
     * @throws MissedDataException если заданный ключ отсутствует, то выбрасывается исключение
     */
    @Override
    public String remove(int key) throws MissedDataException {
        database.update();
        return database.remove(key);
    }

    /**
     * Метод обновляет хранилище и получает объект ConcurrentHashMap, который сериализует и возвращает
     * @return возвращает файл, в котором хранится сериализованное хранилище
     */
    @Override
    public File dump() {
        database.update();
        ConcurrentHashMap<Database.Key,String> dumpHashMap = database.dump();

        File file = new File("data.dat");

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(dumpHashMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Метод обновляет хранилище, получает входной поток, который десериализует в объект ConcurrentHashMap
     * @see ConcurrentHashMap
     * @param loadInputStream входной поток данных
     */
    @Override
    public void load(InputStream loadInputStream) {
        ConcurrentHashMap<Database.Key,String> loadHashMap = new ConcurrentHashMap<>();
        database.update();

        try(ObjectInputStream ois = new ObjectInputStream(loadInputStream)) {
            loadHashMap = (ConcurrentHashMap<Database.Key, String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        database.load(loadHashMap);
    }
}
