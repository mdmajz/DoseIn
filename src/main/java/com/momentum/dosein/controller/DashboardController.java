package com.momentum.dosein.controller;

import com.momentum.dosein.model.MedicineReminder;
import com.momentum.dosein.model.User;
import com.momentum.dosein.service.ReminderService;
import com.momentum.dosein.util.Session;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {

    @FXML private Label greetingLabel;
    @FXML private Label sloganLabel;
    @FXML private Label timeLabel;
    @FXML private VBox scheduleContainer;

    private Timeline clock;
    private final ReminderService reminderService = new ReminderService();

    @FXML
    public void initialize() {
        // Load Poppins font
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Italic.ttf"), 14);

        // Greet user
        User current = Session.getCurrentUser();
        greetingLabel.setText("Hello, " + (current != null ? current.getUsername() : "User") + "!");
        greetingLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 24));

        sloganLabel.setText("Your Health Matters - Stay on Track!");
        sloganLabel.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));

        // Start clock with seconds
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm:ss a");
        timeLabel.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
        clock = new Timeline(
                new KeyFrame(Duration.ZERO,
                        e -> timeLabel.setText(LocalTime.now().format(dtf))),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        // Load reminders
        loadSchedule();
    }

    private void loadSchedule() {
        scheduleContainer.getChildren().clear();
        List<MedicineReminder> allReminders = reminderService.getAllReminders();

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalTime cutoffTime = now.minusMinutes(30); // 30 minutes grace period

        DateTimeFormatter tfmt = DateTimeFormatter.ofPattern("h:mm a");
        boolean hasReminders = false;

        for (MedicineReminder r : allReminders) {
            // Check if reminder is active today (between start and end dates)
            boolean isActiveToday = !today.isBefore(r.getStartDate()) &&
                    !today.isAfter(r.getEndDate());

            // Check if time is upcoming or within grace period
            boolean isRelevantTime = r.getTime().isAfter(now) ||
                    (r.getTime().isAfter(cutoffTime) &&
                            r.getTime().isBefore(now));

            if (isActiveToday && isRelevantTime) {
                hasReminders = true;
                VBox card = new VBox(5);
                card.getStyleClass().add("reminder-card");

                if (r.getTime().isBefore(now)) {
                    card.getStyleClass().add("past-due-card");

                    Label statusLabel = new Label("PAST DUE");
                    statusLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 10));
                    statusLabel.getStyleClass().add("status-indicator");
                    card.getChildren().add(statusLabel);
                }

                Label timeLabel = new Label(r.getTime().format(tfmt));
                timeLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
                timeLabel.getStyleClass().add("reminder-time");

                Label medicineLabel = new Label(r.getMedicineName() + " " + r.getDosage());
                medicineLabel.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
                medicineLabel.getStyleClass().add("reminder-medicine");

                Label noteLabel = new Label(r.getNote());
                noteLabel.setFont(Font.font("Poppins", FontPosture.ITALIC, 12));
                noteLabel.getStyleClass().add("reminder-note");

                card.getChildren().addAll(timeLabel, medicineLabel, noteLabel);
                scheduleContainer.getChildren().add(card);
            }
        }

        if (!hasReminders) {
            Label emptyLabel = new Label("No upcoming reminders for today");
            emptyLabel.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
            emptyLabel.getStyleClass().add("empty-label");
            scheduleContainer.getChildren().add(emptyLabel);
        }
    }


    public void cleanup() {
        if (clock != null) {
            clock.stop();
        }
    }

    @FXML
    private void handleDashboard(ActionEvent e) {
        swapRoot("/com/momentum/dosein/fxml/dashboard.fxml", e);
    }

    @FXML
    private void handleManageSchedule(ActionEvent e) {
        swapRoot("/com/momentum/dosein/fxml/manage_schedule.fxml", e);
    }

    @FXML
    private void handleDoctorContacts(ActionEvent e) {
        try {
            Parent contacts = FXMLLoader.load(
                    getClass().getResource("/com/momentum/dosein/fxml/doctor_contacts.fxml")
            );
            Scene scene = ((Node)e.getSource()).getScene();
            scene.setRoot(contacts);
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Could not load Doctor Contacts screen.")
                    .showAndWait();
        }
    }

    @FXML
    private void handleEmergency(ActionEvent e) {
        swapRoot("/com/momentum/dosein/fxml/emergency.fxml", e);
    }

    @FXML
    private void handleAboutUs(ActionEvent e) {
        swapRoot("/com/momentum/dosein/fxml/about_us.fxml", e);
    }

    @FXML
    private void handleSetReminder(ActionEvent e) {
        swapRoot("/com/momentum/dosein/fxml/set_reminder.fxml", e);
    }

    @FXML
    private void handleSignOut(ActionEvent e) {
        Session.clear();
        swapRoot("/com/momentum/dosein/fxml/login.fxml", e);
    }

    private void swapRoot(String fxmlPath, ActionEvent e) {
        try {
            Parent pane = FXMLLoader.load(
                    getClass().getResource(fxmlPath)
            );
            Scene scene = ((Node)e.getSource()).getScene();
            scene.setRoot(pane);
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Could not load screen: " + fxmlPath)
                    .showAndWait();
        }
    }
}