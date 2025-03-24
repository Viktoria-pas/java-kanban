package ru.yandex.javacource.pasechnyuk.schedule.Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.pasechnyuk.schedule.manager.NotFoundException;
import ru.yandex.javacource.pasechnyuk.schedule.manager.TaskManager;
import ru.yandex.javacource.pasechnyuk.schedule.task.DurationTypeAdapter;
import ru.yandex.javacource.pasechnyuk.schedule.task.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;
    protected static final String GET = "GET";
    protected static final String POST = "POST";
    protected static final String DELETE = "DELETE";

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    public void sendText(HttpExchange exchange, String text, int codeOfResponse) throws IOException {

        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(codeOfResponse, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }

    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "Объект не найден.";
        sendText(exchange, response, 404);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String response = "Задача пересекается по времени с существующими.";
        sendText(exchange, response, 406);
    }

    protected void handleException(HttpExchange exchange, Exception e) throws IOException {
        if (e instanceof NotFoundException) {
            sendNotFound(exchange);
        } else if (e instanceof IllegalArgumentException) {
            sendHasInteractions(exchange);
        } else {
            sendText(exchange, "Ошибка при обработке запроса: " + e.getMessage(), 500);
        }
    }
}
