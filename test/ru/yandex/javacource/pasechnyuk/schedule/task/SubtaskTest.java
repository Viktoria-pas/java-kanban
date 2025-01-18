package ru.yandex.javacource.pasechnyuk.schedule.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.pasechnyuk.schedule.manager.Managers;
import ru.yandex.javacource.pasechnyuk.schedule.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    TaskManager inMemoryTaskManager;
    Epic epic1;
    Subtask subtask1;

    @BeforeEach
    void setUp() {
        inMemoryTaskManager = Managers.getDefault();
        epic1 = inMemoryTaskManager.createEpic(new Epic("Переезд в новый дом",
                "Спланировать переезд в новый дом", 1));
        subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Упаковка вещи",
                "Упаковать вещи в коробки, хрупкие вещи в пленку", 5, epic1.getId()));

    }

    @Test
    void createdSubtaskIsNotNull() {
        assertNotNull(subtask1, "Подзадачи нет");
        System.out.println(subtask1.getId());
    }

    @Test
    void subtaskIdExistsInEpic() {
        assertTrue(epic1.getSubtaskIds().contains(subtask1.getId()),
                "Подзадача не была добавлена в эпик");
    }

    @Test
    void subtaskWasCreated() {
        Subtask subtask2 = inMemoryTaskManager.createSubtask(new Subtask("Арендовать грузовик",
                "Позвонить в транспортные компании", 2, epic1.getId()));
        assertNotNull(inMemoryTaskManager.getSubtask(),
                "Список подзадач пуст");
    }

    @Test
    void subtaskAreEqualIfIdsAreEqual() {
        Subtask subtask2 = new Subtask("Арендовать грузовик",
                "Позвонить в транспортные компании", 2, epic1.getId());
        assertEquals(subtask1, subtask2, "Задачи с одинаковыми ID должны быть равны");
    }

    @Test
    void getSubtaskById() {
        assertEquals(subtask1, inMemoryTaskManager.getSubtaskById(subtask1.getId()),
                "Подзадачи по данному ID нет");
    }

    @Test
    void cleanSubtask() {
        inMemoryTaskManager.clearSubtasks();
        assertTrue(epic1.getSubtaskIds().isEmpty(),
                "Список подзадач у эпика не очистился");
    }

    @Test
    void deleteSubtaskById() {
        inMemoryTaskManager.deleteTaskById(subtask1.getId());
        assertNull(inMemoryTaskManager.getTaskById(subtask1.getId()),
                "Подзадача не удалена по ID");
    }

    @Test
    void changeStatusSubtask() {
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask1);
        assertEquals(TaskStatus.IN_PROGRESS, subtask1.getStatus(),
                "Статус подзадачи не обновидся");
    }

    @Test
    void epicStatusUpdatesWithSubtasks() {
        Subtask subtask2 = inMemoryTaskManager.createSubtask(new Subtask("Арендовать грузовик",
                "Позвонить в транспортные компании", 2, epic1.getId()));

        assertEquals(TaskStatus.NEW, epic1.getStatus(), "Статус эпика должен быть NEW");

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(subtask1);
        inMemoryTaskManager.updateSubtask(subtask2);

        assertEquals(TaskStatus.DONE, epic1.getStatus(),
                "Статус эпика не обновился");
    }

    @Test
    void subtaskBelongsToEpic() {
        assertEquals(epic1.getId(), subtask1.getEpicId(), "Подзадача не пренадлежит Epic");
    }

    @Test
    void createSubtaskWithInvalidEpicId() {
        Subtask subtask3 = inMemoryTaskManager.createSubtask(new Subtask("Подзадача 3",
                "Тест", 1, 999));
        assertNull(subtask3, "Указан неверный EPIC ID");
    }

    @Test
    void subtaskCannotBeItsOwnEpic() {
        Subtask invalidSubtask = new Subtask("Некорректная подзадача",
                "Попытка сделать её своим же эпиком",
                1, 1);
        Subtask result = inMemoryTaskManager.createSubtask(invalidSubtask);

        assertNull(result, "Подзадача, у которой epicId совпадает с её собственным id, не должна быть создана");
    }


}
