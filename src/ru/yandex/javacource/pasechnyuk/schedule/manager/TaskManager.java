package ru.yandex.javacource.pasechnyuk.schedule.manager;

import ru.yandex.javacource.pasechnyuk.schedule.task.Epic;
import ru.yandex.javacource.pasechnyuk.schedule.task.Subtask;
import ru.yandex.javacource.pasechnyuk.schedule.task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager<T extends Task> {
    T createTask(T task);

    ArrayList<T> getTasks(Class<? extends Task> taskType);

    void clearTasks(Class<? extends Task> taskType);

    T getTaskById(int id);

    void updateTask(T task);

    void deleteTaskById(int id);

    ArrayList<Subtask> getEpicSubtasks(int epicId);

    void updateEpicStatus(Epic epic);

    List<Task> getHistory();
}




