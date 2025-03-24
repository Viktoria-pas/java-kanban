package ru.yandex.javacource.pasechnyuk.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.pasechnyuk.schedule.task.*;


import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = createTaskManager();
    }

    @Test
    public void testCreateTask() {
        Task task = new Task("Task 1", "Description 1", 1, LocalDateTime.now(), Duration.ofMinutes(30));
        Task createdTask = taskManager.createTask(task);

        assertNotNull(createdTask);
        assertEquals(task, createdTask);
        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    public void testCreateEpic() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        Epic createdEpic = taskManager.createEpic(epic);

        assertNotNull(createdEpic);
        assertEquals(epic, createdEpic);
        assertEquals(1, taskManager.getEpics().size());
    }

    @Test
    public void testCreateSubtask() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", 2, epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(30));
        Subtask createdSubtask = taskManager.createSubtask(subtask);

        assertNotNull(createdSubtask);
        assertEquals(subtask, createdSubtask);
        assertEquals(1, taskManager.getSubtask().size());
    }

    @Test
    public void testGetTasks() {
        Task task = new Task("Task 1", "Description 1", 1, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createTask(task);

        List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.getFirst());
    }

    @Test
    public void testGetEpics() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        taskManager.createEpic(epic);

        List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size());
        assertEquals(epic, epics.getFirst());
    }

    @Test
    public void testGetSubtask() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", 2, 1, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask);

        List<Subtask> subtasks = taskManager.getSubtask();
        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.getFirst());
    }

    @Test
    public void testClearTasks() {
        Task task = new Task("Task 1", "Description 1", 1, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createTask(task);

        taskManager.clearTasks();
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void testClearEpics() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        taskManager.createEpic(epic);

        taskManager.clearEpics();
        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    public void testClearSubtasks() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", 2, 1, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask);

        taskManager.clearSubtasks();
        assertEquals(0, taskManager.getSubtask().size());
    }

    @Test
    public void testGetTaskById() {
        Task task = new Task("Task 1", "Description 1", 1, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createTask(task);

        Task retrievedTask = taskManager.getTaskById(1);
        assertEquals(task, retrievedTask);
    }

    @Test
    public void testGetEpicById() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        taskManager.createEpic(epic);

        Epic retrievedEpic = taskManager.getEpicById(1);
        assertEquals(epic, retrievedEpic);
    }

    @Test
    public void testGetSubtaskById() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", 2, 1, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask);

        Subtask retrievedSubtask = taskManager.getSubtaskById(2);
        assertEquals(subtask, retrievedSubtask);
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Task 1", "Description 1", 1, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createTask(task);

        Task updatedTask = new Task("Updated Task 1", "Updated Description 1", 1, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.updateTask(updatedTask);

        Task retrievedTask = taskManager.getTaskById(1);
        assertEquals(updatedTask.getName(), retrievedTask.getName());
        assertEquals(updatedTask.getDescription(), retrievedTask.getDescription());
    }

    @Test
    public void testUpdateEpic() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        taskManager.createEpic(epic);

        Epic updatedEpic = new Epic("Updated Epic 1", "Updated Description 1", 1);
        taskManager.updateEpic(updatedEpic);

        Epic retrievedEpic = taskManager.getEpicById(1);
        assertEquals(updatedEpic.getName(), retrievedEpic.getName());
        assertEquals(updatedEpic.getDescription(), retrievedEpic.getDescription());
    }

    @Test
    public void testUpdateSubtask() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", 2, 1, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask);

        Subtask updatedSubtask = new Subtask("Updated Subtask 1", "Updated Description 1", 2, 1, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.updateSubtask(updatedSubtask);

        Subtask retrievedSubtask = taskManager.getSubtaskById(2);
        assertEquals(updatedSubtask.getName(), retrievedSubtask.getName());
        assertEquals(updatedSubtask.getDescription(), retrievedSubtask.getDescription());
    }

    @Test
    public void testDeleteTaskById() {
        Task task = new Task("Task 1", "Description 1", 1, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createTask(task);
        taskManager.deleteTaskById(1);
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    public void testDeleteEpicById() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        taskManager.createEpic(epic);

        taskManager.deleteEpicById(1);
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    public void testDeleteSubtaskById() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", 2, 1, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask);

        taskManager.deleteSubtaskById(2);
        assertTrue(taskManager.getSubtask().isEmpty());
    }

    @Test
    public void testGetEpicSubtasks() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", 2, 1, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask);

        List<Subtask> subtasks = taskManager.getEpicSubtasks(1);
        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.getFirst());
    }

    @Test
    public void testGetHistory() {
        Task task = new Task("Task 1", "Description 1", 1, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createTask(task);

        taskManager.getTaskById(1);
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.getFirst());
    }

    @Test
    public void testGetPrioritizedTasks() {
        Task task1 = new Task("Task 1", "Description 1", 1, LocalDateTime.now(), Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Description 2", 2, LocalDateTime.now().plusHours(1), Duration.ofMinutes(30));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritizedTasks.size());
        assertEquals(task1, prioritizedTasks.get(0));
        assertEquals(task2, prioritizedTasks.get(1));
    }
}