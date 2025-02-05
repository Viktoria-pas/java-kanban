package ru.yandex.javacource.pasechnyuk.schedule.manager;

import ru.yandex.javacource.pasechnyuk.schedule.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager<T> {

    public Node<T> head;
    public Node<T> tail;
    private final Map<Integer, Node<T>> historyMap = new HashMap<>();

    @Override
    public void add(T task) {
        if (task == null) {
            return;
        }

        if (historyMap.containsKey(task.getId())) {
            removeNode(historyMap.get(task.getId()));
        }

        Node<T> newNode = new Node<>(tail, task, null);
        addLast(newNode);
        historyMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node<T> node = historyMap.remove(id);
        if (node == null) {
            return;
        }
        removeNode(node);
    }

    @Override
    public List<T> getHistory() {
        List<T> history = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            history.add(current.getData());
            current = current.getNext();
        }
        return history;
    }

    private void addLast(Node<T> node) {
        if (tail == null) {
            head = node;
        } else {
            tail.setNext(node);
            node.setPrev(tail);
        }
        tail = node;
    }

    private void removeNode(Node<T> node) {
        if (node == null) {
            return;
        }
        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        } else {
            head = node.getNext();
        }

        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        } else {
            tail = node.getPrev();
        }
    }


}
