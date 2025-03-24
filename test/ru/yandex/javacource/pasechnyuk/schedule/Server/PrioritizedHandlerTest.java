package ru.yandex.javacource.pasechnyuk.schedule.Server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.pasechnyuk.schedule.manager.InMemoryTaskManager;
import ru.yandex.javacource.pasechnyuk.schedule.manager.TaskManager;
import ru.yandex.javacource.pasechnyuk.schedule.task.*;


import java.io.IOException;
import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedHandlerTest {
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
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {

        Task task1 = new Task("Задача 1", "Описание задачи 1", 1,
                LocalDateTime.of(2025, 5, 2, 10, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Задача 2", "Описание задачи 2", 2,
                LocalDateTime.of(2025, 6, 2, 10, 0), Duration.ofMinutes(45));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа при получении отсортированного списка задач");

        Task[] prioritizedTasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, prioritizedTasks.length, "Количество задач в списке не совпадает");

        assertTrue(prioritizedTasks[0].getStartTime().isBefore(prioritizedTasks[1].getStartTime()),
                "Задачи не отсортированы по времени начала");
    }

    @Test
    public void testGetEmptyPrioritizedTasks() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа при получении пустого списка задач");

        Task[] prioritizedTasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(0, prioritizedTasks.length, "Список задач должен быть пустым");
    }


}