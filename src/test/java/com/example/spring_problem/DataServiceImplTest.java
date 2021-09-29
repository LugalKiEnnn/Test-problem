package com.example.spring_problem;

import com.example.spring_problem.dao.DataDAOImpl;
import com.example.spring_problem.database.Database;
import com.example.spring_problem.exception.MissedDataException;
import com.example.spring_problem.service.DataServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;


public class DataServiceImplTest {

    private DataServiceImpl dataService;

    @Before
    public void createDatabase() {
        dataService = new DataServiceImpl(new DataDAOImpl(new Database()));
    }

    /**
     * Тест проверяет, что если в хранилище с заданным ключом содержатся данные, то при запросе
     * по этому ключу будут получены эти данные
     */
    @Test
    public void getDataByKeyShouldReturnData() {
        int key = 1;
        String data = "data";
        dataService.put(key,data);
        Assert.assertEquals(data,dataService.get(key));
    }

    /**
     * Тест проверяет, что если попытаться получить данные по отсутствующему ключу, то
     * будет выброшено исключение
     */
    @Test(expected = MissedDataException.class)
    public void getMissedKeyShouldThrowException() {
        int key = 1;
        dataService.get(key);
    }

    /**
     * Тест проверяет, что русский текст хранится и возвращается корректно
     */
    @Test
    public void getDataByKeyShouldReturnCorrectData() {
        int key = 1;
        String data = "данные";
        dataService.put(key,data);
        Assert.assertEquals(data,dataService.get(key));
    }

    /**
     * Тест проверяет, что данные помещаются в хранилище нормально
     */
    @Test
    public void setDataShouldWorkCorrectly() {
        int key = 1;
        String data = "data";
        dataService.put(key,data);
        Assert.assertEquals(data,dataService.get(key));
    }

    /**
     * Тест проверяет, что данные помещаются в хранилище нормально
     */
    @Test
    public void setDataAndTtlShouldWorkCorrectly() {
        int key = 1;
        String data = "data";
        long ttl = 1000000L;
        dataService.put(key,data,ttl);
        Assert.assertEquals(data,dataService.get(key));
    }

    /**
     * Тест проверяет, что через заданное время жизни данные удаляются из хранилища
     */
    @Test(expected = MissedDataException.class)
    public void setDataShouldBeDeletedAfterTtl() {
        int key = 1;
        String data = "data";
        long ttl = 1000L;
        dataService.put(key,data,ttl);
        try {
            Thread.sleep(ttl);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dataService.get(key);
    }

    /**
     * Тест проверяет, что если по одному ключу поместить разные данные, то в хранилище будут
     * храниться те, что были переданы последними
     */
    @Test
    public void setDataWithSameKeyShouldReplaceEachOther() {
        int key1 = 1;
        String data1 = "data";
        long ttl1 = 10000L;
        dataService.put(key1,data1,ttl1);
        String data2 = "another data";
        long ttl2 = 10000L;
        dataService.put(key1,data2,ttl2);
        Assert.assertEquals(data2,dataService.get(key1));
    }

    /**
     * Тест проверяет, что если поместить в хранилище по существующему ключу данные второй раз, то
     * его время жизни обновится
     */
    @Test
    public void setDataTwiceShouldUpdateTtl() {
        int key = 1;
        String data = "data";
        long ttl = 2000L;
        dataService.put(key,data,ttl);
        try {
            Thread.sleep(1500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dataService.put(key,data,ttl);
        try {
            Thread.sleep(1500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dataService.get(key);
    }

    /**
     * Тест проверяет, что при удалении из хранилища метод remove возвращает эти данные
     */
    @Test
    public void removeDataShouldReturnData() {
        int key = 1;
        String data = "data";
        dataService.put(key,data);
        Assert.assertEquals(data,dataService.remove(key));
    }

    /**
     * Тест проверяет, что при попытке удалить из хранилища объект по отсутствующему ключу, то выбросится
     * исключение
     */
    @Test(expected = MissedDataException.class)
    public void removeMissedKeyShouldThrowException() {
        int key = 1;
        dataService.remove(key);
    }

    /**
     * Тест проверяет, что после операции remove данные удаляются из хранилища и по данному ключу
     * невозможно получить данные
     */
    @Test(expected = MissedDataException.class)
    public void removeKeyTwiceShouldThrowException() {
        int key = 1;
        String data = "data";
        dataService.put(key,data);
        dataService.remove(key);
        dataService.remove(key);
    }

    /**
     * Тест проверяет, что при вызове метода dump() возвращается объект типа File
     */
    @Test
    public void dumpShouldReturnFile() {
        int key1 = 1;
        String data1 = "data";
        long ttl1 = 10000L;
        dataService.put(key1,data1,ttl1);
        int key2 = 2;
        String data2 = "another data";
        long ttl2 = 10000L;
        dataService.put(key2,data2,ttl2);
        Assert.assertTrue(dataService.dump().isFile());
    }

    @Test
    public void loadFileShouldLoadNormally() throws IOException {
        File file = new File("data.dat");
        InputStream inputStream = new FileInputStream(file);
        dataService.load(inputStream);
    }

    /**
     * Тест проверяет, что при вызове на хранилище dump(), а затем load() в хранилище загружаются
     * сохранённые объекты, время жизни которых уже истекло
     * @throws FileNotFoundException
     */
    @Test
    public void dumpAndLoadShouldKeepDatabase() throws FileNotFoundException {
        int key1 = 1;
        String data1 = "data";
        long ttl1 = 1000L;
        dataService.put(key1,data1,ttl1);
        int key2 = 2;
        String data2 = "another data";
        long ttl2 = 1000L;
        dataService.put(key2,data2,ttl2);

        File file = dataService.dump();
        InputStream inputStream = new FileInputStream(file);

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dataService.load(inputStream);

        Assert.assertEquals(data1,dataService.get(key1));
        Assert.assertEquals(data2,dataService.get(key2));

    }

    /**
     * Тест проверяет, что если после сохранения хранилища в нём произойдут изменения, то
     * когда сохранённая версия будет загружена, они пропадут, хранилище будет в том же
     * состоянии, что в момент сохранения
     * @throws FileNotFoundException
     */
    @Test(expected = MissedDataException.class)
    public void dumpAndLoadShouldDeleteNewData() throws FileNotFoundException {
        int key1 = 1;
        String data1 = "data";
        long ttl1 = 1000L;
        dataService.put(key1,data1,ttl1);
        int key2 = 2;
        String data2 = "another data";
        long ttl2 = 1000L;

        File file = dataService.dump();
        InputStream inputStream = new FileInputStream(file);

        dataService.put(key2,data2,ttl2);

        dataService.load(inputStream);

        dataService.get(2);

    }


}
