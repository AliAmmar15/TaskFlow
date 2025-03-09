package com.taskflow.analytics;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.taskflow.model.Task;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class TaskAnalytics {
    private final List<Task> tasks;

    public TaskAnalytics(List<Task> tasks) {
        this.tasks = tasks;
    }

    public double calculateCompletionRate() {
        long completedCount = tasks.stream().filter(Task::isCompleted).count();
        return (double) completedCount / tasks.size() * 100;
    }

    public String getMostFrequentCategory() {
        return tasks.stream().collect(Collectors.groupingBy(Task::getCategory, Collectors.counting())).entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("No tasks available");
    }

    public ObservableList<PieChart.Data> generateCharts() {
        Map<String, Long> categoryCount = tasks.stream().collect(Collectors.groupingBy(Task::getCategory, Collectors.counting()));
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        categoryCount.forEach((category, count) -> pieChartData.add(new PieChart.Data(category, count)));
        return pieChartData;
    }
}