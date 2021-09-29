package com.example.spring_problem.controller;

import com.example.spring_problem.entity.DataObject;
import com.example.spring_problem.exception.MissedDataException;
import com.example.spring_problem.exception.TimeToLeaveException;
import com.example.spring_problem.service.DataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;

/**
 * Класс, содержащий контроллеры для связи пользователя с хранилищем посредством запросов
 */
@RestController
public class DataControllers {
    /**
     * Поле, сервис для связи с хранилищем
     */
    DataServiceImpl dataService;

    /**
     * Констуктор, инициализирует поле сервиса с помощью Spring
     * @param dataService сервис для связи с хранилищем
     */
    @Autowired
    public DataControllers(DataServiceImpl dataService) {
        this.dataService = dataService;
    }

    /**
     * Метод вызывается get-запросом вида "/database/{key}" и
     * либо возвращает данные, хранящиеся по заданному ключу, либо выбрасывает исключение, если по
     * этому ключу в базе отсутствуют данные
     * @see com.example.spring_problem.exception.DatabaseExceptionHandler#handleDataException(MissedDataException)
     * @param key значение ключа для хранилища
     * @return возвращает либо данные, хранящиеся по заданному ключу, либо выбрасывает исключение с сообщением
     */
    @GetMapping(value = "/database/{key}")
    public String getData(@PathVariable int key) {
        try {
            return dataService.get(key);
        } catch (Exception e) {
            throw new MissedDataException();
        }
    }

    /**
     * Метод вызывается put-запросом либо вида "/database/{key}", либо "/database/{key}/{ttl}". В теле запроса
     * указывается параметр "date", параметр "ttl" же может отсутствовать. При запросе первого вида ttl
     * либо указывается в теле запроса, либо будет присвоен по умолчанию. При запросе второго вида ttl
     * будет взят из строки URL, но если указать его ещё и в теле запроса, то будет использован ttl из
     * тела. Возвращает HTTP статус, содержащий либо метку успешности, либо метку плохого запроса
     * @see com.example.spring_problem.exception.DatabaseExceptionHandler#handleDataException(TimeToLeaveException)
     * @param key значение ключа для хранилища
     * @param ttl время жизни данных
     * @param dataObject
     * @return возвращает HTTP статус
     */
    @PutMapping(value = { "/database/{key}", "/database/{key}/{ttl}" })
    public HttpStatus setData(@PathVariable Integer key,
                              @PathVariable(required = false) Long ttl,
                              @RequestBody DataObject dataObject) {
        try {
            dataObject.setKey(key);
            if (dataObject.getTtl() == 0) {
                if (ttl == null) {
                    dataService.put(dataObject.getKey(), dataObject.getData());
                } else {
                    dataObject.setTtl(ttl);
                    dataService.put(dataObject.getKey(), dataObject.getData(), dataObject.getTtl() * 1000);
                }
            } else {
                dataService.put(dataObject.getKey(), dataObject.getData(), dataObject.getTtl() * 1000);
            }

            return HttpStatus.OK;
        } catch (TimeToLeaveException e) {
            throw new TimeToLeaveException();
        }
    }

    /**
     * Метод вызывается delete-запросом вида "/database/{key}" и удаляет данные в хранилище по
     * заданному ключу. При этом он либо возвращает данные, хранящиеся по заданному ключу,
     * либо выбрасывает исключение, если по этому ключу в базе отсутствуют данные
     * @see com.example.spring_problem.exception.DatabaseExceptionHandler#handleDataException(MissedDataException)
     * @param key значение ключа для хранилища
     * @return возвращает либо данные, хранящиеся по заданному ключу, либо выбрасывает исключение с сообщением
     */
    @DeleteMapping(value = "/database/{key}")
    public String removeData(@PathVariable int key) {
        try {
            return dataService.remove(key);
        } catch (Exception e) {
            throw new MissedDataException();
        }
    }

    /**
     * Метод вызывается post-запросом вида "/database/load" и требует файла формата .dat, который
     * был создан ранее методом {@link #dumpFile()} и содержит сохранённое состояние хранилища. В
     * методе оно преобразуется в поток и загружается в хранилище
     * @param file файл, содержащий сохранённое состояние хранилища
     */
    @PostMapping(value = "/database/load")
    public void loadFile(@RequestParam("file") MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            dataService.load(inputStream);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Метод вызывается get-запросом вида "/database/dump" и вызывает сохранение текущего
     * состояния хранилища, которое сериализуется и упаковывается в .dat файл, который
     * передаётся пользователю
     * @return файл с сохранённым состоянием хранилища
     */
    @GetMapping(value = "/database/dump")
    public ResponseEntity<Object> dumpFile() {
        File file = dataService.dump();
        HttpHeaders headers = new HttpHeaders();
        InputStreamResource resource = null;

        try {
            resource = new InputStreamResource(new FileInputStream(file));

            headers.add("Content-Disposition",
                    String.format("attachment; filename=\"%s\"",file.getName()));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().headers(headers).contentLength(file.length()).
                contentType(MediaType.parseMediaType("application/txt")).body(resource);
    }
}
