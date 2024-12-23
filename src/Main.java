public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task1 = taskManager.createTask("Сделать чай", "Необходимо сделать зеленый чай");
        Task task2 = taskManager.createTask("Съездить за покупками", "Купить подарки к НГ");


        Epic epic1 = taskManager.createEpic("Переезд в новый дом", "Спланировать переезд в новый дом");
        Subtask subtask1 = taskManager.createSubtask("Упаковка вещи",
                "Упаковать вещи в коробки, хрупкие вещи в пленку", epic1);
        Subtask subtask2 = taskManager.createSubtask("Арендовать грузовик для перевозки вещей",
                "Позвонить в транспортные компании, узнать цены, заказать машину", epic1);

        Epic epic2 = taskManager.createEpic("Приготовить торт", "Приготовить торт по рецепту.");
        Subtask subtask3 = taskManager.createSubtask("Купить продукты по рецепту",
                "Сходить за продуктами", epic2);

        System.out.println("Задачи:" + taskManager.getAllTasks());

        System.out.println("\nЭпик Переезд в новый дом:" + taskManager.getSubtasksForEpic(epic1));

        System.out.println("\nЭпик Приготовить торт:" + taskManager.getSubtasksForEpic(epic2) );

        task1.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task1);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task2);

        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateTask(subtask1);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subtask2);
        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateTask(subtask3);


        taskManager.updateEpicStatus(epic1);
        taskManager.updateEpicStatus(epic2);


        System.out.println("\nОбновлённые статусы:" + taskManager.getAllTasks());

        System.out.println("\nСтатус эпика Переезд в новый дом: " + epic1.getStatus());
        System.out.println("Статус эпика Приготовить торт: " + epic2.getStatus());

        taskManager.removeTaskById(task2.getId());
        taskManager.removeTaskById(epic2.getId());


        System.out.println("\nЗадачи после удаления :" + taskManager.getAllTasks());

        System.out.println("\nПодзадачи после удаления :" + taskManager.getSubtasksForEpic(epic2));

        System.out.println(taskManager.getTaskById(3));
        taskManager.clearTasks();
        System.out.println(taskManager.getAllTasks());

    }
}

