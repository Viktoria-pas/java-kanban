package ru.yandex.javacource.pasechnyuk.schedule.task;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(String name, String description, int id) {
        super(name, description, id, null, null);
        subtaskIds = new ArrayList<>();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public TaskType getTaskType() {
        return TaskType.EPIC;
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


    public LocalDateTime getEndTime() {
        return this.endTime;
    }


    @Override
    public String toString() {
        return "Epic{" + "name=" + getName() + ", description=" + getDescription() + ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
