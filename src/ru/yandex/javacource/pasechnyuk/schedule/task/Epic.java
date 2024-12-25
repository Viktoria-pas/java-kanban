package ru.yandex.javacource.pasechnyuk.schedule.task;

import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Integer> subtaskIds;

    public Epic(String name, String taskInfo, int id) {
        super(name, taskInfo, id);
        subtaskIds = new ArrayList<>();
    }

    public void addSubtask(int subtaskId) {
        subtaskIds.add(subtaskId);
    }


    @Override
    public String toString() {
        return "Epic{" + "name=" + getName() + ", taskInfo=" + getTaskInfo() + ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
