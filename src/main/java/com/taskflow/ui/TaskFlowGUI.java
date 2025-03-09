package com.taskflow.ui;

import java.time.LocalDate;
import java.util.Optional;

import com.taskflow.analytics.TaskAnalytics;
import com.taskflow.logic.TaskManager;
import com.taskflow.model.Task;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TaskFlowGUI extends Application {
    private TaskManager taskManager;
    private TableView<Task> taskTable;
    private ObservableList<Task> taskList;
    private TaskAnalytics taskAnalytics;
    private PieChart taskPieChart;
    private boolean isDarkMode = false;

    @Override
    public void start(Stage primaryStage) {
        taskManager = new TaskManager();
        taskList = FXCollections.observableArrayList(taskManager.getAllTasks());
        taskAnalytics = new TaskAnalytics(taskList);

        primaryStage.setTitle("TaskFlow - Smart To-Do List Application");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10, 10, 10, 10));

        // Task Input Form
        VBox taskInputForm = new VBox();
        taskInputForm.setSpacing(10);

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");

        DatePicker deadlinePicker = new DatePicker();
        deadlinePicker.setPromptText("Deadline");

        ComboBox<Task.Priority> priorityBox = new ComboBox<>();
        priorityBox.setItems(FXCollections.observableArrayList(Task.Priority.values()));
        priorityBox.setPromptText("Priority");

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        Button addButton = new Button("Add Task");
        addButton.setTooltip(new Tooltip("Add a new task"));
        addButton.setOnAction(e -> addTask(titleField, descriptionArea, deadlinePicker, priorityBox, categoryField));

        taskInputForm.getChildren().addAll(titleField, descriptionArea, deadlinePicker, priorityBox, categoryField, addButton);

        // Search Bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search tasks...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTasks(newValue));

        // Sorting Options
        ComboBox<String> sortBox = new ComboBox<>();
        sortBox.setItems(FXCollections.observableArrayList("Title", "Deadline", "Priority", "Category"));
        sortBox.setPromptText("Sort by");
        sortBox.setTooltip(new Tooltip("Sort tasks by selected criteria"));
        sortBox.setOnAction(e -> sortTasks(sortBox.getValue()));

        // Task Table
        taskTable = new TableView<>();
        taskTable.setItems(taskList);

        TableColumn<Task, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());

        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        TableColumn<Task, LocalDate> deadlineColumn = new TableColumn<>("Deadline");
        deadlineColumn.setCellValueFactory(cellData -> cellData.getValue().deadlineProperty());

        TableColumn<Task, Task.Priority> priorityColumn = new TableColumn<>("Priority");
        priorityColumn.setCellValueFactory(cellData -> cellData.getValue().priorityProperty());

        TableColumn<Task, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        TableColumn<Task, Boolean> isCompletedColumn = new TableColumn<>("Completed");
        isCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().isCompletedProperty());

        taskTable.getColumns().addAll(titleColumn, descriptionColumn, deadlineColumn, priorityColumn, categoryColumn, isCompletedColumn);

        // Buttons for Mark Complete and Delete Task
        HBox taskActions = new HBox();
        taskActions.setSpacing(10);

        Button completeButton = new Button("Mark Complete");
        completeButton.setTooltip(new Tooltip("Mark the selected task as complete"));
        completeButton.setOnAction(e -> markTaskAsComplete());

        Button deleteButton = new Button("Delete Task");
        deleteButton.setTooltip(new Tooltip("Delete the selected task"));
        deleteButton.setOnAction(e -> deleteTask());

        taskActions.getChildren().addAll(completeButton, deleteButton);

        // Productivity Insights
        VBox productivityInsights = new VBox();
        productivityInsights.setSpacing(10);

        Label completionRateLabel = new Label("Completion Rate: " + taskAnalytics.calculateCompletionRate() + "%");
        Label frequentCategoryLabel = new Label("Most Frequent Category: " + taskAnalytics.getMostFrequentCategory());

        taskPieChart = new PieChart(taskAnalytics.generateCharts());

        productivityInsights.getChildren().addAll(completionRateLabel, frequentCategoryLabel, taskPieChart);

        // Dark Mode Toggle
        CheckBox darkModeCheckBox = new CheckBox("Dark Mode");
        darkModeCheckBox.setTooltip(new Tooltip("Toggle dark mode"));
        darkModeCheckBox.setOnAction(e -> toggleDarkMode(primaryStage.getScene()));

        // Add components to the root layout
        VBox topContainer = new VBox();
        topContainer.getChildren().addAll(taskInputForm, searchField, sortBox, darkModeCheckBox);

        root.setTop(topContainer);
        root.setCenter(taskTable);
        root.setBottom(taskActions);
        root.setRight(productivityInsights);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Add keyboard shortcuts
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.N) {
                addTask(titleField, descriptionArea, deadlinePicker, priorityBox, categoryField);
            } else if (event.isControlDown() && event.getCode() == KeyCode.D) {
                deleteTask();
            } else if (event.isControlDown() && event.getCode() == KeyCode.M) {
                markTaskAsComplete();
            }
        });
    }

    private void addTask(TextField title, TextArea description, DatePicker deadline, ComboBox<Task.Priority> priority, TextField category) {
        Task newTask = new Task(title.getText(), description.getText(), deadline.getValue(), priority.getValue(), category.getText());
        taskManager.addTask(newTask);
        taskList.setAll(taskManager.getAllTasks());
        updateInsights();
    }

    private void markTaskAsComplete() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskManager.markTaskAsComplete(selectedTask.getTitle());
            taskList.setAll(taskManager.getAllTasks());
            updateInsights();
            showNotification("Task Completed", "The task '" + selectedTask.getTitle() + "' has been marked as complete.");
        }
    }

    private void deleteTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Delete Task");
            confirmationAlert.setHeaderText("Are you sure you want to delete this task?");
            confirmationAlert.setContentText(selectedTask.getTitle());
            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                taskManager.removeTask(selectedTask.getTitle());
                taskList.setAll(taskManager.getAllTasks());
                updateInsights();
            }
        }
    }

    private void filterTasks(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            taskList.setAll(taskManager.getAllTasks());
        } else {
            taskList.setAll(taskManager.getAllTasks().stream()
                    .filter(task -> task.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                            task.getDescription().toLowerCase().contains(searchText.toLowerCase()) ||
                            task.getCategory().toLowerCase().contains(searchText.toLowerCase()))
                    .toList());
        }
    }

    private void sortTasks(String criteria) {
        switch (criteria) {
            case "Title":
                taskList.sort((task1, task2) -> task1.getTitle().compareToIgnoreCase(task2.getTitle()));
                break;
            case "Deadline":
                taskList.sort((task1, task2) -> task1.getDeadline().compareTo(task2.getDeadline()));
                break;
            case "Priority":
                taskList.sort((task1, task2) -> task1.getPriority().compareTo(task2.getPriority()));
                break;
            case "Category":
                taskList.sort((task1, task2) -> task1.getCategory().compareToIgnoreCase(task2.getCategory()));
                break;
        }
    }

    private void toggleDarkMode(Scene scene) {
        if (isDarkMode) {
            scene.getStylesheets().remove("dark-theme.css");
        } else {
            scene.getStylesheets().add("dark-theme.css");
        }
        isDarkMode = !isDarkMode;
    }

    private void updateInsights() {
        taskPieChart.setData(taskAnalytics.generateCharts());
    }

    private void showNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}