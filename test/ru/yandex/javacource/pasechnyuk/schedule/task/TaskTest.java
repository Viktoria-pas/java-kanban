package ru.yandex.javacource.pasechnyuk.schedule.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.pasechnyuk.schedule.manager.Managers;
import ru.yandex.javacource.pasechnyuk.schedule.manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    TaskManager inMemoryTaskManager;
    Task task1;

    @BeforeEach
    void setUp() {
        inMemoryTaskManager = Managers.getDefault();
        task1 = inMemoryTaskManager.createTask(new Task("Сделать чай", "Необходимо сделать зеленый чай",
                1, LocalDateTime.of(2025, 5, 2, 10, 0), Duration.ofMinutes(60)));
    }


    @Test
    void createdTaskIsNotNull() {
        assertNotNull(task1, "Задача не создана");
    }

    @Test
    void tasksIdEquals() {
        assertEquals(1, task1.getId(), "ID не совпадает с заданным");
    }

    @Test
    void tasksAreEqualIfIdsAreEqual() {
        Task task2 = new Task("Сделать кофе",
                "Необходимо сделать черный кофе", 1,
                LocalDateTime.of(2025, 5, 2, 10, 0), Duration.ofMinutes(60));
        assertEquals(task1, task2, "Задачи с одинаковыми ID должны быть равны");
    }


    @Test
    void difrentTaskIds() {
        Task task2 = inMemoryTaskManager.createTask(new Task("Сделать кофе",
                "Необходимо сделать черный кофе", 2,
                LocalDateTime.of(2025, 8, 18, 10, 0), Duration.ofMinutes(60)));
        assertNotEquals(task1.getId(), task2.getId(), "ID разных задач совпадают");
    }

    @Test
    void defaultTaskStatusIsNew() {
        assertEquals(TaskStatus.NEW, task1.getStatus(), "Статус новой задачи не NEW");
    }

    @Test
    void getTaskById() {
        assertEquals(task1, inMemoryTaskManager.getTaskById(1), "Метод не возвращает задачу по её ID");
    }

    @Test
    void cleanTasks() {
        inMemoryTaskManager.clearTasks();
        assertTrue(inMemoryTaskManager.getTasks().isEmpty(), "Список не очистился");
    }

    @Test
    void deleteTaskById() {
        Task task2 = inMemoryTaskManager.createTask(new Task("Съездить за покупками",
                "Купить подарки к НГ", 2,
                LocalDateTime.of(2025, 12, 15, 10, 0), Duration.ofMinutes(60)));
        inMemoryTaskManager.deleteTaskById(1);
        assertNull(inMemoryTaskManager.getTaskById(1), "Задача с ID 1 не была удалена");
        assertNotNull(inMemoryTaskManager.getTaskById(2), "Задача с ID 2 была удалена");
    }

    @Test
    void setStatusTask() {
        task1.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task1.getStatus(), "Статус задачи не обновился");
    }

    @Test
    void setStartTimeTask() {
        task1.setStartTime(LocalDateTime.of(2025, 9, 3, 18, 54));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        assertEquals("03.09.2025 18:54", task1.getStartTime().format(formatter), "Время задачи не обновилось");
    }

    @Test
    void setDurationTask() {
        task1.setDuration(Duration.ofMinutes(15));
        assertEquals(15L, task1.getDuration().toMinutes(), "Продолжительность задачи не обновилась");
    }


    @Test
    void updateTaskDescription() {
        task1.setDescription("Сделать травяной чай");
        assertEquals("Сделать травяной чай", task1.getDescription(), "Описание задачи не обновилось");
    }

    @Test
    void updateTaskStatus() {
        task1.setName("Сделать кофе");
        assertEquals("Сделать кофе", task1.getName(), "Название задачи не обновилось");
    }
}