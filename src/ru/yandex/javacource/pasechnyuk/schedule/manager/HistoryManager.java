package ru.yandex.javacource.pasechnyuk.schedule.manager;

import ru.yandex.javacource.pasechnyuk.schedule.task.Task;

import java.util.List;

public interface HistoryManager<T extends Task> {

    void add(T task);

    void remove(int id);

    List<T> getHistory();
}
