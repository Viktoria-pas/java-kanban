package ru.yandex.javacource.pasechnyuk.schedule.Server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.pasechnyuk.schedule.manager.TaskManager;
import ru.yandex.javacource.pasechnyuk.schedule.task.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathOfUrl = path.split("/");

        try {
            if (method.equals("GET")) {
                handleGetRequest(exchange, pathOfUrl);
            } else {
                sendText(exchange, "Вы использовали какой-то другой метод!", 400);
            }
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    private void handleGetRequest(HttpExchange exchange, String[] pathParts) throws IOException {

        if (pathParts.length == 2) {
            List<Task> history = taskManager.getHistory();
            String response = gson.toJson(history);
            sendText(exchange, response, 200);
        } else {
            sendNotFound(exchange);
        }
    }
}

