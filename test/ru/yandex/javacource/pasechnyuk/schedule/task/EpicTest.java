package ru.yandex.javacource.pasechnyuk.schedule.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.pasechnyuk.schedule.manager.Managers;
import ru.yandex.javacource.pasechnyuk.schedule.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager inMemoryTaskManager;
    Epic epic1;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    void setUp() {
        inMemoryTaskManager = Managers.getDefault();
        epic1 = inMemoryTaskManager.createEpic(new Epic("Переезд в новый дом",
                "Спланировать переезд в новый дом", 1));
        assertNotNull(epic1, "Эпик не был создан");
        subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Упаковка вещи",
                "Упаковать вещи в коробки, хрупкие вещи в пленку", 5, epic1.getId()));
        assertNotNull(subtask1, "Эпик не был создан");
        subtask2 = inMemoryTaskManager.createSubtask(new Subtask("Арендовать грузовик для перевозки вещей",
                "Позвонить в транспортные компании, узнать цены, заказать машину", 6, epic1.getId()));
        assertNotNull(subtask2, "Эпик не был создан");
    }


    @Test
    void createdEpicIsNotNull() {
        assertNotNull(epic1, "Эпик не создался");
    }

    @Test
    void epicIdEquals() {
        assertEquals(1, epic1.getId(), "ID не совпадает с заданным");
    }

    @Test
    void getEpicById() {
        assertEquals(epic1, inMemoryTaskManager.getEpicById(1),
                "Вызван неверный эпик по ID");
    }

    @Test
    void epicsAreEqualIfIdsAreEqual() {
        Epic epic2 = new Epic("Приготовить торт", "Приготовить торт по рецепту.", 1);
        assertEquals(epic1, epic2, "Задачи с одинаковыми ID должны быть равны");
    }

    @Test
    void cleanEpic() {
        inMemoryTaskManager.clearEpics();
        assertTrue(inMemoryTaskManager.getEpics().isEmpty(),
                "Список эпиков не очистился");
    }

    @Test
    void deleteEpicById() {
        Epic epic2 = inMemoryTaskManager.createEpic(new Epic("Приготовить торт",
                "Приготовить торт по рецепту.", 2));
        inMemoryTaskManager.deleteEpicById(1);
        assertNull(inMemoryTaskManager.getEpicById(1),
                "Эпик 1 не был удалён");
        assertNotNull(inMemoryTaskManager.getEpicById(epic2.getId()),
                "Эпик 2 удалён");
    }

    @Test
    void updateEpic() {
        epic1.setName("Переезд в новую квартиру");
        inMemoryTaskManager.updateEpic(epic1);
        assertEquals("Переезд в новую квартиру", epic1.getName(),
                "Эпик не был обновлен");
    }

    @Test
    void addSubtasksToEpic() {
        assertEquals(2, epic1.getSubtaskIds().size(),
                "Количество подзадач в эпике неверно");
        assertTrue(epic1.getSubtaskIds().contains(subtask1.getId()),
                "Позадача не добавлена в эпик");
    }

    @Test
    void changeStatusEpicAllSubtasksDone() {
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(subtask1);
        inMemoryTaskManager.updateSubtask(subtask2);

        assertEquals(TaskStatus.DONE, epic1.getStatus(),
                "Статус эпика должен быть DONE");
    }

    @Test
    void changeStatusEpicInProgress() {
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask1);
        inMemoryTaskManager.updateSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(),
                "Статус эпика должен быть IN_PROGRESS");
    }

    @Test
    void changeStatusEpicNew() {
        subtask1.setStatus(TaskStatus.NEW);
        subtask2.setStatus(TaskStatus.NEW);
        inMemoryTaskManager.updateSubtask(subtask1);
        inMemoryTaskManager.updateSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic1.getStatus(),
                "Статус эпика должен быть NEW");
    }

    @Test
    void epicCannotAddItselfAsSubtask() {
        epic1.addSubtaskId(epic1.getId());
        assertFalse(epic1.getSubtaskIds().contains(epic1.getId()),
                "Эпик не должен содержать свой собственный ID в списке подзадач");
    }
    @Test
    void epicDoesNotHasSubtaskIdWhichWasDeleted() {
        epic1.removeSubtaskId(subtask1.getId());
        assertFalse(epic1.getSubtaskIds().contains(subtask1.getId()),
                "Эпик не должен содержать  ID удаленой подзадачи в списке подзадач");
    }

    @Test
    void deletedSubtuskFromEpicIsNotInManager() {
        epic1.removeSubtaskId(subtask1.getId());
        Task removedSubtask = inMemoryTaskManager.getTaskById(subtask1.getId());
        assertNull(removedSubtask);

    }

}