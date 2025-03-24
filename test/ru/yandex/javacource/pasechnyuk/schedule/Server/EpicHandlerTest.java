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

class EpicHandlerTest {
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
    public void testCreateEpic() throws IOException, InterruptedException {

        Epic epic = new Epic("Test Epic", "Test Description", 1);

        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Статус ответа должен быть 201");

        Epic savedEpic = taskManager.getEpicById(1);
        assertNotNull(savedEpic, "Эпик должен быть сохранен в менеджере");
        assertEquals("Test Epic", savedEpic.getName(), "Название эпика должно совпадать");
    }



    @Test
    public void testGetEpicById() throws IOException, InterruptedException {

        Epic epic = new Epic("Эпик", "Описание эпика", 1);
        taskManager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epic/1"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, response.statusCode(), "Неверный статус ответа при получении эпика");

        Epic retrievedEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic.getId(), retrievedEpic.getId(), "ID эпика не совпадает");
        assertEquals("Эпик", retrievedEpic.getName(), "Название эпика не совпадает");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {

        Epic epic = new Epic("Эпик", "Описание эпика", 1);
        taskManager.createEpic(epic);

        epic.setDescription("Новое описание");
        String updatedEpicJson = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(updatedEpicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(201, response.statusCode(), "Неверный статус ответа при обновлении эпика");


        Epic updatedEpic = taskManager.getEpicById(1);
        assertEquals("Новое описание", updatedEpic.getDescription(), "Описание эпика не обновилось");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {

        Epic epic = new Epic("Эпик", "Описание эпика", 1);
        taskManager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epic/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа при удалении эпика");

        assertTrue(taskManager.getEpics().isEmpty(), "Эпик должна быть удалена из менеджера");
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {

        Epic epic = new Epic("Эпик", "Описание эпика", 1);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", 2, epic.getId(),
                LocalDateTime.of(2025, 5, 2, 10, 0), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", 3, epic.getId(),
                LocalDateTime.of(2025, 6, 2, 10, 0), Duration.ofMinutes(45));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epic/" + epic.getId() + "/subtask"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа при получении подзадач эпика");

        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertEquals(2, subtasks.length, "Количество подзадач не совпадает");

        for (Subtask subtask : subtasks) {
            assertEquals(epic.getId(), subtask.getEpicId(), "Подзадача не принадлежит указанному эпику");
        }
    }

}