package ru.yandex.javacource.pasechnyuk.schedule.Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.pasechnyuk.schedule.manager.InMemoryTaskManager;
import ru.yandex.javacource.pasechnyuk.schedule.manager.TaskManager;
import ru.yandex.javacource.pasechnyuk.schedule.task.DurationTypeAdapter;
import ru.yandex.javacource.pasechnyuk.schedule.task.LocalDateTimeTypeAdapter;
import ru.yandex.javacource.pasechnyuk.schedule.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest {
    protected TaskManager taskManager;
    protected HttpTaskServer httpTaskServer;
    protected HttpClient httpClient;
    protected Gson gson;

    @BeforeEach
    public void start() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
        httpClient = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    @AfterEach
    public void stop() {
        httpTaskServer.stop();
    }
    @Test
    public void testGetHistory() throws IOException, InterruptedException {

        Task task1 = new Task("Задача 1", "Описание задачи 1", 1,
                LocalDateTime.of(2025, 5, 2, 10, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Задача 2", "Описание задачи 2", 2,
                LocalDateTime.of(2025, 6, 2, 10, 0), Duration.ofMinutes(45));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, response.statusCode(), "Неверный статус ответа при получении истории");


        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, history.length, "Количество задач в истории не совпадает");

        assertEquals(task1.getId(), history[0].getId(), "ID первой задачи в истории не совпадает");
        assertEquals(task2.getId(), history[1].getId(), "ID второй задачи в истории не совпадает");
    }

    @Test
    public void testGetEmptyHistory() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа при получении пустой истории");

        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertEquals(0, history.length, "История должна быть пустой");
    }

}