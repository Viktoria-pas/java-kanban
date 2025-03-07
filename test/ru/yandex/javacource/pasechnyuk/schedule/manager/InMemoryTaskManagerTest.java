package ru.yandex.javacource.pasechnyuk.schedule.manager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.pasechnyuk.schedule.task.Epic;
import ru.yandex.javacource.pasechnyuk.schedule.task.Subtask;
import ru.yandex.javacource.pasechnyuk.schedule.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }


    TaskManager inMemoryTaskManager = new InMemoryTaskManager();


    @Test
    void addDifferentTypesOfTasks() {
        Task task1 = inMemoryTaskManager.createTask(new Task("Сделать чай",
                "Необходимо сделать зеленый чай", 1,
                LocalDateTime.of(2025, 5, 2, 10, 0), Duration.ofMinutes(34)));
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Переезд в новый дом",
                "Спланировать переезд в новый дом", 2));
        Subtask subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Упаковка вещи",
                "Упаковать вещи в коробки, хрупкие вещи в пленку", 1, epic1.getId(),
                LocalDateTime.of(2025, 2, 16, 23, 0), Duration.ofMinutes(12)));
        assertEquals(task1, inMemoryTaskManager.getTaskById(task1.getId()));
        assertEquals(epic1, inMemoryTaskManager.getEpicById(epic1.getId()));
        assertEquals(subtask1, inMemoryTaskManager.getSubtaskById(subtask1.getId()));
    }

    @Test
    void taskWithNonConflictingId_CreatesSuccessfully() {
        Task task1 = new Task("Первая задача", "Описание", 1,
                LocalDateTime.of(2025, 5, 2, 10, 0), Duration.ofMinutes(34));
        Task task2 = new Task("Вторая задача", "Описание", 2,
                LocalDateTime.of(2025, 6, 2, 10, 0), Duration.ofMinutes(34));

        inMemoryTaskManager.createTask(task1);
        Task createdTask = inMemoryTaskManager.createTask(task2);

        assertNotNull(createdTask, "Задача не создана");
        assertEquals(2, createdTask.getId(), "ID не совпадает с заданным");
    }

    @Test
    void noChangeEtemsAfterCreated() {
        Task originalTask = new Task("Сделать чай",
                "Необходимо сделать зеленый чай", 1,
                LocalDateTime.of(2025, 5, 2, 10, 0), Duration.ofMinutes(34));
        InMemoryTaskManager inMemoryTaskManager1 = new InMemoryTaskManager();

        Task createdTask = inMemoryTaskManager1.createTask(originalTask);

        assertEquals(originalTask.getId(), createdTask.getId(),
                "ID задачи изменился при добавлении");
        assertEquals(originalTask.getName(), createdTask.getName(),
                "Название задачи изменилось при добавлении");
        assertEquals(originalTask.getDescription(), createdTask.getDescription(),
                "Описание задачи изменилось при добавлении");
        assertEquals(originalTask.getStatus(), createdTask.getStatus(),
                "Статус задачи изменился при добавлении");
    }

    @Test
    public void testTaskWithoutStartTime() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Task 1", "Description 1", 1, null, Duration.ofMinutes(30));
        assertDoesNotThrow(() -> manager.createTask(task1));

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(prioritizedTasks.isEmpty());

        Task task2 = new Task("Task 2", "Description 2", 2,
                LocalDateTime.of(2023, 10, 1, 10, 0), Duration.ofMinutes(30));
        assertDoesNotThrow(() -> manager.createTask(task2));

        prioritizedTasks = manager.getPrioritizedTasks();
        assertEquals(1, prioritizedTasks.size());
        assertEquals(task2, prioritizedTasks.get(0));
    }


    @Test
    public void testTasksCanNotCross() {
        InMemoryTaskManager manager = new InMemoryTaskManager();


        Task task1 = new Task("Task 1", "Description 1", 1,
                LocalDateTime.of(2023, 10, 1, 10, 0), Duration.ofMinutes(30));
        assertDoesNotThrow(() -> manager.createTask(task1));

        Task task2 = new Task("Task 2", "Description 2", 2,
                LocalDateTime.of(2023, 10, 1, 10, 15), Duration.ofMinutes(30));

        assertThrows(IllegalArgumentException.class, () -> manager.createTask(task2));
    }


}