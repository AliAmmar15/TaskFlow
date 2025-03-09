package com.taskflow.model;

import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Task {
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    private final StringProperty title;
    private final StringProperty description;
    private final ObjectProperty<LocalDate> deadline;
    private final ObjectProperty<Priority> priority;
    private final StringProperty category;
    private final BooleanProperty isCompleted;

    public Task(String title, String description, LocalDate deadline, Priority priority, String category) {
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.deadline = new SimpleObjectProperty<>(deadline);
        this.priority = new SimpleObjectProperty<>(priority);
        this.category = new SimpleStringProperty(category);
        this.isCompleted = new SimpleBooleanProperty(false);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public LocalDate getDeadline() {
        return deadline.get();
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline.set(deadline);
    }

    public ObjectProperty<LocalDate> deadlineProperty() {
        return deadline;
    }

    public Priority getPriority() {
        return priority.get();
    }

    public void setPriority(Priority priority) {
        this.priority.set(priority);
    }

    public ObjectProperty<Priority> priorityProperty() {
        return priority;
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public boolean isCompleted() {
        return isCompleted.get();
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted.set(isCompleted);
    }

    public BooleanProperty isCompletedProperty() {
        return isCompleted;
    }

    public void markComplete() {
        this.isCompleted.set(true);
    }

    public void editTask(String title, String description, LocalDate deadline, Priority priority, String category) {
        this.title.set(title);
        this.description.set(description);
        this.deadline.set(deadline);
        this.priority.set(priority);
        this.category.set(category);
    }

    public String getTaskDetails() {
        return String.format("Title: %s\nDescription: %s\nDeadline: %s\nPriority: %s\nCategory: %s\nCompleted: %s",
                title.get(), description.get(), deadline.get(), priority.get(), category.get(), isCompleted.get());
    }
}