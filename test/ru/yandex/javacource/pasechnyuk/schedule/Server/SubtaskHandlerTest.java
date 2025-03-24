package ru.yandex.javacource.pasechnyuk.schedule.Server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.pasechnyuk.schedule.manager.InMemoryTaskManager;
import ru.yandex.javacource.pasechnyuk.schedule.manager.NotFoundException;
import ru.yandex.javacource.pasechnyuk.schedule.manager.TaskManager;
import ru.yandex.javacource.pasechnyuk.schedule.task.DurationTypeAdapter;
import ru.yandex.javacource.pasechnyuk.schedule.task.Epic;
import ru.yandex.javacource.pasechnyuk.schedule.task.LocalDateTimeTypeAdapter;
import ru.yandex.javacource.pasechnyuk.schedule.task.Subtask;


import java.io.IOException;
import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
class SubtaskHandlerTest {
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
    public void testCreateSubtask() throws IOException, InterruptedException {

        Epic epic = new Epic("Эпик", "Описание эпика", 0);
        String epicJson = gson.toJson(epic);


        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> createEpicResponse = httpClient.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());


        assertEquals(201, createEpicResponse.statusCode(), "Неверный статус ответа при создании эпика");

        Epic createdEpic = gson.fromJson(createEpicResponse.body(), Epic.class);
        assertNotNull(createdEpic, "Эпик не был создан");
        assertEquals("Эпик", createdEpic.getName(), "Название эпика не совпадает");


        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 0, createdEpic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(30));
        String subtaskJson = gson.toJson(subtask);


        HttpRequest createSubtaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> createSubtaskResponse = httpClient.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, createSubtaskResponse.statusCode(), "Неверный статус ответа при создании подзадачи");

        Subtask createdSubtask = gson.fromJson(createSubtaskResponse.body(), Subtask.class);
        assertNotNull(createdSubtask, "Подзадача не была создана");
        assertEquals("Подзадача", createdSubtask.getName(), "Название подзадачи не совпадает");
        assertEquals(createdEpic.getId(), createdSubtask.getEpicId(), "ID эпика в подзадаче не совпадает");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {

        Epic epic = new Epic("Эпик", "Описание эпика", 1);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 2, epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtask/" + subtask.getId()))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа при получении подзадачи");

        Subtask retrievedSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtask.getId(), retrievedSubtask.getId(), "ID подзадачи не совпадает");
        assertEquals("Подзадача", retrievedSubtask.getName(), "Название подзадачи не совпадает");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {

        Epic epic = new Epic("Эпик", "Описание эпика", 1);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 2, epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask);

        subtask.setDescription("Новое описание");
        String updatedSubtaskJson = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный статус ответа при обновлении подзадачи");

        Subtask updatedSubtask = taskManager.getSubtaskById(2);
        assertEquals("Новое описание", updatedSubtask.getDescription(), "Описание подзадачи не обновилось");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        // Создаем эпик и подзадачу
        Epic epic = new Epic("Эпик", "Описание эпика", 1);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 2, epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask);

        // Отправляем DELETE-запрос для удаления подзадачи
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtask/2"))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode(), "Неверный статус ответа при удалении подзадачи");

        // Проверяем, что подзадача удалена
        try {
            taskManager.getSubtaskById(2);
            fail("Ожидалось исключение NotFoundException, так как подзадача должна быть удалена");
        } catch (NotFoundException e) {
            // Исключение ожидаемо, подзадача удалена
            assertTrue(true, "Подзадача успешно удалена, исключение NotFoundException выброшено");
        }
    }
  
}