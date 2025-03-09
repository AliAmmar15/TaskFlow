package com.taskflow.logic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.taskflow.model.Task;

public class TaskManager {
    private final List<Task> tasks;

    public TaskManager() {
        tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(String title) {
        tasks.removeIf(task -> task.getTitle().equals(title));
    }

    public void editTask(Task updatedTask) {
        for (Task task : tasks) {
            if (task.getTitle().equals(updatedTask.getTitle())) {
                task.editTask(updatedTask.getTitle(), updatedTask.getDescription(), updatedTask.getDeadline(),
                        updatedTask.getPriority(), updatedTask.getCategory());
                break;
            }
        }
    }

    public void markTaskAsComplete(String title) {
        for (Task task : tasks) {
            if (task.getTitle().equals(title)) {
                task.markComplete();
                break;
            }
        }
    }

    public List<Task> getTasksByCategory(String category) {
        return tasks.stream().filter(task -> task.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
    }

    public List<Task> getOverdueTasks() {
        LocalDate today = LocalDate.now();
        return tasks.stream().filter(task -> task.getDeadline().isBefore(today) && !task.isCompleted())
                .collect(Collectors.toList());
    }

    public List<Task> getAllTasks() {
        return tasks;
    }
}