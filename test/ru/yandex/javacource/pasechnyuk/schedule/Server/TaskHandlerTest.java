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
class TaskHandlerTest {
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
    public void testCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Первая задача", "Описание", 0,
                null, null);
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный статус ответа при создании задачи");
        assertNotNull(response.body(), "Тело ответа пустое");

        Task createdTask = taskManager.getTaskById(1);
        assertNotNull(createdTask, "Задача не была добавлена в менеджер");
        assertEquals("Первая задача", createdTask.getName(), "Название задачи не совпадает");

        Task responseTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getName(), responseTask.getName(), "Название задачи в ответе не совпадает");
        assertEquals(task.getDescription(), responseTask.getDescription(), "Описание задачи в ответе не совпадает");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {

        Task task = new Task("Первая задача", "Описание", 0, null, null);
        String taskJson = gson.toJson(task);

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, createResponse.statusCode(), "Неверный статус ответа при создании задачи");
        assertNotNull(createResponse.body(), "Тело ответа пустое");

        Task createdTask = gson.fromJson(createResponse.body(), Task.class);
        assertEquals(task.getName(), createdTask.getName(), "Название задачи в ответе не совпадает");
        assertEquals(task.getDescription(), createdTask.getDescription(), "Описание задачи в ответе не совпадает");

        createdTask.setDescription("Новое описание");
        String updatedTaskJson = gson.toJson(createdTask);


        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + createdTask.getId()))
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> updateResponse = httpClient.send(updateRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, updateResponse.statusCode(), "Неверный статус ответа при обновлении задачи");
        assertNotNull(updateResponse.body(), "Тело ответа пустое");

    }


    @Test
    public void testGetTaskById() throws IOException, InterruptedException {

        Task task = new Task("Первая задача", "Описание", 0,
                LocalDateTime.of(2025, 5, 2, 10, 0), Duration.ofMinutes(34));
        taskManager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа");
        assertNotNull(response.body(), "Тело ответа пустое");

        assertTrue(response.body().contains("Описание"), "Тело ответа не содержит название задачи");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        // Создаем задачу
        Task task = new Task("Test Task", "Test Description", 1, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус ответа должен быть 200 (OK)");

        assertTrue(taskManager.getTasks().isEmpty(), "Задача должна быть удалена из менеджера");
    }
  
}