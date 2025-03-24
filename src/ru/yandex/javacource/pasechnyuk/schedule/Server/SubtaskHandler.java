package ru.yandex.javacource.pasechnyuk.schedule.Server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.pasechnyuk.schedule.manager.TaskManager;
import ru.yandex.javacource.pasechnyuk.schedule.task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathOfUrl = path.split("/");

        try {
            switch (method) {
                case GET:
                    handleGetRequest(exchange, pathOfUrl); // GET запросы обрабатываются здесь
                    break;
                case POST:
                    handlePostRequest(exchange, pathOfUrl); // POST запросы обрабатываются здесь
                    break;
                case DELETE:
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
            List<Subtask> subtasks = taskManager.getSubtask();
            String response = gson.toJson(subtasks);
            sendText(exchange, response, 200);
        } else if (pathParts.length == 3) {
            int id = Integer.parseInt(pathParts[2]);
            Subtask subtask = taskManager.getSubtaskById(id);
            String response = gson.toJson(subtask);
            sendText(exchange, response, 200);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostRequest(HttpExchange exchange, String[] pathParts) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        if (pathParts.length == 2) {
            Subtask createdSubtask = taskManager.createSubtask(subtask);
            String response = gson.toJson(createdSubtask);
            sendText(exchange, response, 201);
        } else if (pathParts.length == 3) {
            taskManager.updateSubtask(subtask);
            sendText(exchange, "Задача обновлена", 201);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String[] pathParts) throws IOException {

        if (pathParts.length == 3) {
            int id = Integer.parseInt(pathParts[2]);
            taskManager.deleteSubtaskById(id);
            String response = "Задача удалена";
            sendText(exchange, response, 200);
        } else {
            sendNotFound(exchange);
        }
    }
}

