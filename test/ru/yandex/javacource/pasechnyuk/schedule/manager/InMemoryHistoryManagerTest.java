package ru.yandex.javacource.pasechnyuk.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.pasechnyuk.schedule.task.Epic;
import ru.yandex.javacource.pasechnyuk.schedule.task.Subtask;
import ru.yandex.javacource.pasechnyuk.schedule.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {

    TaskManager inMemoryTaskManager;
    HistoryManager inMemoryHistoryManager;

    @BeforeEach
    void setUp() {
        inMemoryTaskManager = Managers.getDefault();
        inMemoryHistoryManager = Managers.getDefaultHistory();
    }

    @Test
    void cheakTaskWasAddedToHistory() {
        Task task1 = inMemoryTaskManager.createTask(new Task("Сделать чай",
                "Необходимо сделать зеленый чай", 1,
                LocalDateTime.of(2025, 7, 1, 13, 20), Duration.ofMinutes(5)));
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Переезд в новый дом",
                "Спланировать переезд в новый дом", 2));
        Subtask subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Упаковка вещи",
                "Упаковать вещи в коробки, хрупкие вещи в пленку", 1, epic1.getId(),
                LocalDateTime.of(2025, 2, 16, 23, 0), Duration.ofMinutes(12)));
        inMemoryTaskManager.getTaskById(task1.getId());
        inMemoryTaskManager.getEpicById(epic1.getId());
        inMemoryTaskManager.getSubtaskById(subtask1.getId());
        assertTrue(inMemoryTaskManager.getHistory().contains(task1));
        assertTrue(inMemoryTaskManager.getHistory().contains(epic1));
        assertTrue(inMemoryTaskManager.getHistory().contains(subtask1));
    }

    @Test
    void checkTaskHistoryPreservesPreviousState() {
        Task task1 = inMemoryTaskManager.createTask(new Task("Сделать чай",
                "Необходимо сделать зеленый чай", 1,
                LocalDateTime.of(2025, 7, 1, 13, 20), Duration.ofMinutes(5)));
        inMemoryTaskManager.getTaskById(task1.getId());

        Task originalTask = new Task("Приготовить кофе", "Сварить кофе в турке", 1,
                LocalDateTime.of(2025, 11, 30, 7, 0), Duration.ofMinutes(9));

        inMemoryTaskManager.createTask(originalTask);

        inMemoryTaskManager.updateTask(originalTask);

        Task historyTask = inMemoryTaskManager.getHistory().get(0);
        assertEquals(task1.getName(), historyTask.getName(), "Название задачи в истории изменилось");
        assertEquals(task1.getDescription(), historyTask.getDescription(), "Описание задачи в истории изменилось");
        assertEquals(task1.getStatus(), historyTask.getStatus(), "Статус задачи в истории изменился");
        assertEquals(task1.getId(), historyTask.getId(), "ID задачи в истории изменился");
    }

    @Test
    void fistTaskWillbeDeletedIfItWillGetSecondTime() {
        Task task1 = inMemoryTaskManager.createTask(new Task("Сделать чай", "Необходимо сделать зеленый чай", 1,
                LocalDateTime.of(2025, 02, 16, 23, 0), Duration.ofMinutes(12)));
        Task task2 = inMemoryTaskManager.createTask(new Task("Съездить за покупками", "Купить подарки к НГ", 2,
                LocalDateTime.of(2025, 7, 1, 13, 20), Duration.ofMinutes(84)));
        Task task3 = inMemoryTaskManager.createTask(new Task("Приготовить обед", "Сварить суп", 3,
                LocalDateTime.of(2025, 11, 30, 7, 0), Duration.ofMinutes(9)));

        inMemoryTaskManager.getTaskById(task1.getId());
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getTaskById(task3.getId());
        int size1 = inMemoryTaskManager.getHistory().size();
        inMemoryTaskManager.getTaskById(task2.getId());
        int size2 = inMemoryTaskManager.getHistory().size();

        System.out.println(inMemoryTaskManager.getHistory());
        assertTrue(size1 == size2);
    }

    @Test
    void TaskWhichWasGetSecondTimeWasAddedTotheEnd() {
        Task task1 = inMemoryTaskManager.createTask(new Task("Сделать чай", "Необходимо сделать зеленый чай", 1,
                LocalDateTime.of(2025, 02, 16, 23, 0), Duration.ofMinutes(12)));
        Task task2 = inMemoryTaskManager.createTask(new Task("Съездить за покупками", "Купить подарки к НГ", 2,
                LocalDateTime.of(2025, 7, 1, 13, 20), Duration.ofMinutes(84)));
        Task task3 = inMemoryTaskManager.createTask(new Task("Приготовить обед", "Сварить суп", 3,
                LocalDateTime.of(2025, 11, 30, 7, 0), Duration.ofMinutes(9)));

        inMemoryTaskManager.getTaskById(task1.getId());
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getTaskById(task3.getId());
        inMemoryTaskManager.getTaskById(task1.getId());

        System.out.println(inMemoryTaskManager.getHistory());
        assertEquals(inMemoryTaskManager.getTaskById(task1.getId()), inMemoryTaskManager.getHistory().getLast());

    }

    @Test
    void cheakTaskWasDeletedFromHistory() {
        Task task1 = inMemoryTaskManager.createTask(new Task("Сделать чай",
                "Необходимо сделать зеленый чай", 1,
                LocalDateTime.of(2025, 7, 1, 13, 20), Duration.ofMinutes(5)));
        Task task2 = inMemoryTaskManager.createTask(new Task("Сделать чай",
                "Необходимо сделать зеленый чай", 2,
                LocalDateTime.of(2025, 9, 1, 13, 20), Duration.ofMinutes(5)));
        Task task3 = inMemoryTaskManager.createTask(new Task("Сделать чай",
                "Необходимо сделать зеленый чай", 3,
                LocalDateTime.of(2025, 2, 16, 23, 0), Duration.ofMinutes(12)));
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);
        assertEquals(task3, inMemoryHistoryManager.getHistory().getLast());
        inMemoryHistoryManager.remove(task3.getId());
        assertEquals(task2, inMemoryHistoryManager.getHistory().getLast());


    }


}