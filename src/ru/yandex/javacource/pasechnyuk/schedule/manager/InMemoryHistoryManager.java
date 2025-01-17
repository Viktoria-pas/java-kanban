package ru.yandex.javacource.pasechnyuk.schedule.manager;

import ru.yandex.javacource.pasechnyuk.schedule.task.Epic;
import ru.yandex.javacource.pasechnyuk.schedule.task.Subtask;
import ru.yandex.javacource.pasechnyuk.schedule.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager<T>{

    private static final int HISTORY_LIMIT = 10; // Ограничение на размер истории
    private final List<T> history = new ArrayList<>();

    @Override
    public void add(T task) {
     T copiedTask = cloneTask(task);
        if (history.size() >= HISTORY_LIMIT) {
            history.remove(0); // Удаляем самый старый элемент
        }
        history.add(copiedTask);
    }

    private T cloneTask(T task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return (T) new Subtask(subtask.getName(), subtask.getDescription(), subtask.getId(), subtask.getEpicId());
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            Epic copiedEpic = new Epic(epic.getName(), epic.getDescription(), epic.getId());
            copiedEpic.setSubtaskIds(new ArrayList<>(epic.getSubtaskIds()));
            return (T) copiedEpic;
        } else {
            return (T) new Task(task.getName(), task.getDescription(), task.getId());
        }
    }

    @Override
    public List<T> getHistory() {
        return new ArrayList<>(history);
    }

}
