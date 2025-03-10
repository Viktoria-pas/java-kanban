package ru.yandex.javacource.pasechnyuk.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.pasechnyuk.schedule.task.Epic;
import ru.yandex.javacource.pasechnyuk.schedule.task.Subtask;
import ru.yandex.javacource.pasechnyuk.schedule.task.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        taskManager = createTaskManager();
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(tempFile);
    }

    @Test
    public void testSaveAndLoadEmptyFile() throws IOException {

        File file = File.createTempFile("test", ".csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        FileBackedTaskManager loadedManager = manager.loadFromFile(file);

        assertTrue(loadedManager.getTasks().isEmpty(), "Список задач должен быть пустым");
        assertTrue(loadedManager.getEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(loadedManager.getSubtask().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    public void saveTasks() throws IOException {
        File file = File.createTempFile("test", ".csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task = manager.createTask(new Task("Task 1", "Description 1", 1,
                LocalDateTime.of(2025, 5, 2, 10, 0), Duration.ofMinutes(34)));
        Epic epic = manager.createEpic(new Epic("Epic 1", "Description Epic 1", 2));
        Subtask subtask = manager.createSubtask(new Subtask("Subtask 1", "Description Subtask 1", 3, epic.getId(),
                LocalDateTime.of(2025, 2, 16, 23, 0), Duration.ofMinutes(12)));

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, manager2.getTasks().size());
        assertEquals(1, manager2.getEpics().size());
        assertEquals(1, manager2.getSubtask().size());
    }

    @Test
    public void loadedTasks() throws IOException {
        File file = File.createTempFile("test", ".csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task = manager.createTask(new Task("Task 1", "Description 1", 1,
                LocalDateTime.of(2025, 5, 2, 10, 0), Duration.ofMinutes(34)));
        Epic epic = manager.createEpic(new Epic("Epic 1", "Description Epic 1", 2));
        Subtask subtask = manager.createSubtask(new Subtask("Subtask 1", "Description Subtask 1", 3, epic.getId(),
                LocalDateTime.of(2025, 2, 16, 23, 0), Duration.ofMinutes(12)));


        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);

        Task loadedTask = manager2.getTaskById(1);
        Epic loadedEpic = manager2.getEpicById(2);
        Subtask loadedSubtask = manager2.getSubtaskById(3);

        assertNotNull(loadedTask, "Задача должна быть восстановлена");
        assertNotNull(loadedEpic, "Эпик должен быть восстановлен");
        assertNotNull(loadedSubtask, "Подзадача должна быть восстановлена");
    }

    @Test
    public void namesOfLoadedTasksSame() throws IOException {
        File file = File.createTempFile("test", ".csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task = manager.createTask(new Task("Task 1", "Description 1", 1,
                LocalDateTime.of(2025, 5, 2, 10, 0), Duration.ofMinutes(34)));
        Epic epic = manager.createEpic(new Epic("Epic 1", "Description Epic 1", 2));
        Subtask subtask = manager.createSubtask(new Subtask("Subtask 1", "Description Subtask 1", 3, epic.getId(),
                LocalDateTime.of(2025, 2, 16, 23, 0), Duration.ofMinutes(12)));


        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);


        Task loadedTask = manager2.getTaskById(1);
        Epic loadedEpic = manager2.getEpicById(2);
        Subtask loadedSubtask = manager2.getSubtaskById(3);

        assertEquals("Task 1", loadedTask.getName(), "Название задачи должно совпадать");
        assertEquals("Epic 1", loadedEpic.getName(), "Название эпика должно совпадать");
        assertEquals("Subtask 1", loadedSubtask.getName(), "Название подзадачи должно совпадать");
    }

    @Test
    public void testFileFormat() throws IOException {
        File file = File.createTempFile("test", ".csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task = manager.createTask(new Task("Task 1", "Description 1", 1,
                LocalDateTime.of(2025, 5, 2, 10, 0), Duration.ofMinutes(34)));
        Epic epic = manager.createEpic(new Epic("Epic 1", "Description Epic 1", 2));
        Subtask subtask = manager.createSubtask(new Subtask("Subtask 1", "Description Subtask 1", 3, epic.getId(),

                LocalDateTime.of(2025, 2, 16, 23, 0), Duration.ofMinutes(12)));

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String header = br.readLine();
            assertEquals("id,type,name,status,description, startTime, duration, epic", header, "Заголовок CSV должен совпадать");

            String taskLine = br.readLine();
            assertEquals("1,TASK,Task 1,NEW,Description 1,2025-05-02T10:00,34,", taskLine, "Строка задачи должна совпадать");

            String epicLine = br.readLine();
            assertEquals("2,EPIC,Epic 1,NEW,Description Epic 1,2025-02-16T23:00,12,", epicLine, "Строка эпика должна совпадать");

            String subtaskLine = br.readLine();
            assertEquals("3,SUBTASK,Subtask 1,NEW,Description Subtask 1,2025-02-16T23:00,12,2", subtaskLine, "Строка подзадачи должна совпадать");
        }


    }
}

