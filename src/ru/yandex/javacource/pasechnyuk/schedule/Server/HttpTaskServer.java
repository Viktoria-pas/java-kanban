package ru.yandex.javacource.pasechnyuk.schedule.Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacource.pasechnyuk.schedule.manager.Managers;
import ru.yandex.javacource.pasechnyuk.schedule.manager.TaskManager;
import ru.yandex.javacource.pasechnyuk.schedule.task.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = Managers.getDefault();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(taskManager, gson));
        server.createContext("/epic", new EpicHandler(taskManager, gson));
        server.createContext("/subtask", new SubtaskHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));

    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();

        Task task = new Task("Test Task", "Test Description", 1,
                LocalDateTime.of(2025, 6, 12, 12, 15), Duration.ofMinutes(30));
        taskManager.createTask(task);
        Epic epic = new Epic("Эпик", "Описание эпика", 0);
        taskManager.createEpic(epic);


        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 0, epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask);


        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
        System.out.println("Сервер запущен на порту 8080. Нажмите Enter для остановки.");
    }


}
