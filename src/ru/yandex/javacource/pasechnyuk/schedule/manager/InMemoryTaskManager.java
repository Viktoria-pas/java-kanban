package ru.yandex.javacource.pasechnyuk.schedule.manager;

import ru.yandex.javacource.pasechnyuk.schedule.task.Epic;
import ru.yandex.javacource.pasechnyuk.schedule.task.Subtask;
import ru.yandex.javacource.pasechnyuk.schedule.task.Task;
import ru.yandex.javacource.pasechnyuk.schedule.task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager<T extends Task> implements TaskManager<T> {

    private int currentId = 0;
    private Map<Integer, T> tasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();


    @Override
    public T createTask(T task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            Epic epic = (Epic) tasks.get(subtask.getEpicId());
            if (subtask.getId() == subtask.getEpicId()) {
                return null;
            }
            if (epic == null) {
                return null;
            }

            int id = generateId();
            task.setId(id);
            tasks.put(id, task);
            epic.addSubtaskId(id);
            updateEpicStatus(epic);
            return task;
        }

        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }


    @Override
    public ArrayList<T> getTasks(Class<? extends Task> taskType) {
        ArrayList<T> filteredTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (taskType.isInstance(task)) {
                filteredTasks.add((T) task);
            }
        }
        return filteredTasks;
    }


    @Override
    public void clearTasks(Class<? extends Task> taskType) {
        if (taskType == Subtask.class) {
            for (Task task : new ArrayList<>(tasks.values())) {
                if (task instanceof Subtask subtask) {
                    Epic epic = (Epic) tasks.get(subtask.getEpicId());
                    if (epic != null) {
                        epic.removeSubtaskId(subtask.getId());
                    }
                    tasks.remove(subtask.getId());
                }
            }
        } else if (taskType == Epic.class) {
            for (Task task : new ArrayList<>(tasks.values())) {
                if (task instanceof Epic epic) {
                    for (Integer subtaskId : new ArrayList<>(epic.getSubtaskIds())) {
                        tasks.remove(subtaskId);
                    }
                    tasks.remove(epic.getId());
                }
            }
        } else {
            tasks.values().removeIf(taskType::isInstance);
        }
    }


    @Override
    public T getTaskById(int id) {
        T task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void updateTask(T task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            if (task instanceof Subtask) {
                Subtask subtask = (Subtask) task;
                Epic epic = (Epic) tasks.get(subtask.getEpicId());
                if (epic != null) {
                    updateEpicStatus(epic);
                }
            }
        } else {
            System.out.println("Задача с таким ID не найдена.");
        }
    }

    @Override
    public void deleteTaskById(int id) {
        T task = tasks.remove(id);

        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            Epic epic = (Epic) tasks.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
            }
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            for (int subtaskId : new ArrayList<>(epic.getSubtaskIds())) {
                tasks.remove(subtaskId);
            }
            epic.getSubtaskIds().clear();
        }
    }


    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        T task = tasks.get(epicId);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.add((Subtask) tasks.get(subtaskId));
            }
        }
        return subtasks;
    }


    @Override
    public void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasks = getEpicSubtasks(epic.getId());

        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private int generateId() {
        return ++currentId;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
