package com.example.spring_problem.database;

import com.example.spring_problem.exception.MissedDataException;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Класс, создающий хранилище типа ключ-значение
 */
@Component
public class Database {
    /**
     * Константа, задаёт частоту обновления хранилища
     */
    private static final long PERIOD_REFRESH = 5000L;
    /**
     * хранилище данных, в качестве ключа объект, хранящий его численное значение и время жизни,
     * в качестве данных строка
     */
    private volatile ConcurrentHashMap<Key,String> databaseMap = new ConcurrentHashMap<>();

    /**
     * Объявляем шедулер, чтобы потом он с заданной частотой повторял обновление хранилища
     */
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    });

    /**
     * Через период {@link Database#PERIOD_REFRESH} хранилище проверяется на актуальность
     * времени жизни объектов и очищается
     */
    public Database() {
        scheduler.scheduleAtFixedRate(() -> {
            long current = System.currentTimeMillis();

            for (Key k : databaseMap.keySet()) {
                if (!k.isAlive(current)) {
                    databaseMap.remove(k);
                }
            }
        }, 1, PERIOD_REFRESH, TimeUnit.MILLISECONDS);
    }

    /**
     * Метод вставки данных по ключу в хранилище, время жизни устанавливается по умолчанию {@link Key#DEFAULT_TTL}
     * @param key ключ для объекта в хранилище
     * @param data данные, хранящиеся в хранилище
     */
    public void put(int key, String data) {
        Key tempKey = new Key(key);

        if(databaseMap.containsKey(tempKey)) {
            databaseMap.put(tempKey,data);

            for(Key k : databaseMap.keySet()) {
                if(k.equals(tempKey)) {
                    k.setDefaultTtl();
                }
            }
        } else {
            databaseMap.put(tempKey,data);
        }
    }

    /**
     * Метод вставки данных по ключу в хранилище, устанавливается заданное время жизни
     * @param key ключ для объекта в хранилище
     * @param data данные, хранящиеся в хранилище
     * @param ttl время жизни данных в хранилище
     */
//    public void put(int key, String data, long ttl) {
//        Key tempKey = new Key(key, ttl);
//
//        databaseMap.put(tempKey,data);
//
//    }
    public void put(int key, String data, long ttl) {
        Key tempKey = new Key(key, ttl);

        if(databaseMap.containsKey(tempKey)) {
            databaseMap.put(tempKey,data);

            for(Key k : databaseMap.keySet()) {
                if(k.equals(tempKey)) {
                    k.setTtl(ttl);
                }
            }
        } else {
            databaseMap.put(tempKey,data);
        }
    }

    /**
     * Метод получения данных по значению ключа
     * @param key ключ для хранилища
     * @return возвращает данные, хранящиеся по переданному ключу
     * @throws MissedDataException если заданный ключ отсутствует, то выбрасывается исключение
     */
    public String get(int key) throws MissedDataException {
        Key tempKey = new Key(key);

        if(databaseMap.containsKey(tempKey)) {
            return databaseMap.get(tempKey);
        } else {
            throw new MissedDataException();
        }
    }

    /**
     * Метод удаления данных по ключу. Данные передадутся, а затем удалятся из хранилища
     * @param key ключ для хранилища
     * @return возвращает данные, хранящиеся по переданному ключу
     * @throws MissedDataException если заданный ключ отсутствует, то выбрасывается исключение
     */
    public String remove(int key) throws MissedDataException {
        Key tempKey = new Key(key);

        if(databaseMap.containsKey(tempKey)) {
            String response = databaseMap.get(tempKey);
            databaseMap.remove(tempKey);
            return response;
        } else {
            throw new MissedDataException();
        }
    }

    /**Хранилище очищается, устанавливается версия
     * устанавливается время жизни исходя из текущего и
     * @param loadHashMap загруженное из файла хранилище с данными
     */
    public void load(ConcurrentHashMap<Key,String> loadHashMap) {
        databaseMap.clear();
        for(Key k : loadHashMap.keySet()) {
            k.setTtl(k.getTtl());
            databaseMap.put(k, loadHashMap.get(k));
        }
    }

    /**
     * Копирует хранилище данных, в качестве времени жизни указывается рассчитанное
     * с помощью {@link Key#prepareKeyForDump(Key)}, то есть оставшееся число миллисекунд
     * @return возвращает скопированное хранилище
     */
    public ConcurrentHashMap<Key,String> dump() {
        ConcurrentHashMap<Key,String> dumpHashMap = new ConcurrentHashMap<>();

        for(Key k : databaseMap.keySet()) {
            dumpHashMap.put(Key.prepareKeyForDump(k), databaseMap.get(k));
        }

        return dumpHashMap;
    }

    /**
     * Обновляет хранилище, удаляя элементы с истекшим временем жизни
     */
    public void update() {
        long current = System.currentTimeMillis();

        for (Key k : databaseMap.keySet()) {
            if (!k.isAlive(current)) {
                databaseMap.remove(k);
            }
        }
    }

    /**
     * Класс - ключ для хранилища. Содержит два поля, в одном хранится численное значения ключа от
     * хранилища для данных, в другом хранится значение либо момента времени, когда время жизни объекта истечёт,
     * либо значение времени его жизни в милисекундах
     */
    public static class Key implements Serializable{
        /**
         * Численное значение ключа от хранилища для данных
         */
        private final int key;
        /**
         * Значение либо момента времени, когда время жизни объекта истечёт, либо значение времени
         * его жизни в милисекундах
         */
        private long ttl;

        /**
         * {@value #DEFAULT_TTL} время жизни объекта по умолчанию
         */
        private static final long DEFAULT_TTL = 1800000L;

        /**
         * Создаёт ключ с заданным временем жизни, которое прибавляется к текущему,
         * во времени жизни хранится момент времени в системе, в который время жизни объекта закончится
         * @param key значение ключа для хранилища
         * @param ttl время жизни объекта
         */
        public Key(int key, long ttl) {
            this.key = key;
            this.ttl = System.currentTimeMillis() + ttl;
        }

        /**
         * Создаёт ключ с временем жизни по умолчанию {@link #DEFAULT_TTL}, которое прибавляется к текущему,
         * во времени жизни хранится момент времени в системе, в который время жизни объекта закончится
         * @param key значение ключа для хранилища
         */
        public Key(int key) {
            this.key = key;
            this.ttl = System.currentTimeMillis() + DEFAULT_TTL;
        }

        /**
         * Метод, изменяющий ключ хранилища для сохранения данных,
         * во времени жизни хранится момент времени в системе, в который время жизни объекта закончится
         * @param k объект - ключ, хранящий ключ для хранилища и время жизни
         * @return возвращает изменённый ключ
         */
        public static Key prepareKeyForDump(Key k) {
            Key tempKey = new Key(k.getKey(),k.getTtl() - 2 *System.currentTimeMillis());
            return tempKey;
        }

        /**
         * Метод, возвращающий численное значение ключа
         * @return возвращает численное значение ключа хранилища
         */
        public int getKey() {
            return key;
        }

        /**
         * Метод, возвращающий установленное время жизни объекта
         * @return время жизни объекта
         */
        public long getTtl() {
            return ttl;
        }

        /**
         * Устанавливает время жизни как переданный параметр + текущее время в системе. Таким образом,
         * время жизни объекта кончится, когда время в системе станет больше этого значения
         * @param ttl время жизни данных в хранилище
         */
        public void setTtl(long ttl) {
            this.ttl = System.currentTimeMillis() + ttl;
        }

        /**
         * Устанавливает время жизни как {@link #DEFAULT_TTL} + текущее время в системе. Таким образом,
         * время жизни объекта кончится, когда время в системе станет больше этого значения
         */
        public void setDefaultTtl() {
            this.ttl = System.currentTimeMillis() + DEFAULT_TTL;
        }

        /**
         * Проверяет, больше ли текущее время в системе, чем время, в которое время жизни объекта истечёт
         * @param currentTimeMillis текущее значение времени в системе
         * @return правдивость утверждения о том, что время истечения жизни объекта больше текущего времени
         */
        public boolean isAlive(long currentTimeMillis) {
            return currentTimeMillis < ttl;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key1 = (Key) o;
            return key == key1.key;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }
}
