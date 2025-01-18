package ru.yandex.javacource.pasechnyuk.schedule.manager;

import ru.yandex.javacource.pasechnyuk.schedule.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager<T>{

    private static final int HISTORY_LIMIT = 10;
    private final List<T> history = new ArrayList<>();

    @Override
    public void add(T task) {
        if (history.size() >= HISTORY_LIMIT) {
            history.remove(0);
        }
        T newTask = task;
        history.add(newTask);
    }


    @Override
    public List<T> getHistory() {
        return new ArrayList<>(history);
    }

}
