package ru.yandex.javacource.pasechnyuk.schedule.Server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.pasechnyuk.schedule.manager.TaskManager;
import ru.yandex.javacource.pasechnyuk.schedule.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathOfUrl = path.split("/");


        try {
            switch (method) {
                case "GET":
                    handleGetRequest(exchange, pathOfUrl); // GET запросы обрабатываются здесь
                    break;
                case "POST":
                    handlePostRequest(exchange, pathOfUrl); // POST запросы обрабатываются здесь
                    break;
                case "DELETE":
                    handleDeleteRequest(exchange, pathOfUrl);
                    break;
                default:
                    sendText(exchange, "Вы использовали какой-то другой метод!", 400);
            }
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }


    private void handleGetRequest(HttpExchange exchange, String[] pathParts) throws IOException {

        if (pathParts.length == 2) {
            List<Task> tasks = taskManager.getTasks();
            String response = gson.toJson(tasks);
            sendText(exchange, response, 200);
        } else if (pathParts.length == 3) {
            int id = Integer.parseInt(pathParts[2]);
            Task task = taskManager.getTaskById(id);
            String response = gson.toJson(task);
            sendText(exchange, response, 200);
        } else {
            sendNotFound(exchange);
        }
    }


    private void handlePostRequest(HttpExchange exchange, String[] pathParts) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);

        if (pathParts.length == 2) {
            Task createdTask = taskManager.createTask(task);
            String response = gson.toJson(createdTask);
            sendText(exchange, response, 201);
        } else if (pathParts.length == 3) {
            taskManager.updateTask(task);
            sendText(exchange, "Задача обновлена", 200); // 200 OK

        } else {
            sendText(exchange, "Некорректный запрос", 400); // 400 Bad Request
        }

    }

    private void handleDeleteRequest(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3) {
            int id = Integer.parseInt(pathParts[2]);
            taskManager.deleteTaskById(id);
            sendText(exchange, "Задача удалена", 200);
        } else {
            sendText(exchange, "Некорректный запрос", 400);
        }
    }

}





