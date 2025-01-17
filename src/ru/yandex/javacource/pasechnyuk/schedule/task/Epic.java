package ru.yandex.javacource.pasechnyuk.schedule.task;

import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Integer> subtaskIds;

    public Epic(String name, String description, int id) {
        super(name, description, id);
        subtaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskId != getId()) {
            subtaskIds.add(subtaskId);
        }
    }

    public void cleanSubtask() {
        subtaskIds.clear();
    }

    public void removeSubtaskId(int id) {
        subtaskIds.remove((Integer) id);
    }

    @Override
    public String toString() {
        return "Epic{" + "name=" + getName() + ", description=" + getDescription() + ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
