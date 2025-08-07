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
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;

public class DashboardController {

    @FXML private Label greetingLabel;
    @FXML private Label sloganLabel;
    @FXML private Label timeLabel;
    @FXML private VBox scheduleContainer;

    private Timeline clock;
    private final ReminderService reminderService = new ReminderService();
    private static final Set<String> SHOWN_ALERTS = new HashSet<>();
    private static LocalDate ALERT_DATE = LocalDate.now();
    private int lastCheckedMinute = -1; // Gate for checkAndShowDueReminders

    @FXML
    public void initialize() {
        // Load Poppins font
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Italic.ttf"), 14);

        // Greet user
        User current = Session.getCurrentUser();
        greetingLabel.setText("Hello, " + (current != null ? current.getUsername() : "Majharul") + "!");
        greetingLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 28));

        sloganLabel.setText("Your Health Matters-Stay on Track!");
        sloganLabel.setFont(Font.font("Poppins", FontWeight.MEDIUM, 16));

        // Start clock with seconds
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh : mm : ss a");
        timeLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        clock = new Timeline(
                new KeyFrame(Duration.ZERO,
                        e -> {
                            timeLabel.setText(LocalTime.now().format(dtf));
                            // Reset shown alerts across days
                            LocalDate todayNow = LocalDate.now();
                            if (!todayNow.equals(ALERT_DATE)) {
                                ALERT_DATE = todayNow;
                                SHOWN_ALERTS.clear();
                                lastCheckedMinute = -1; // reset minute gate
                            }
                            // Run alert check only once per minute
                            int currentMinute = LocalTime.now().getMinute();
                            if (currentMinute != lastCheckedMinute) {
                                lastCheckedMinute = currentMinute;
                                checkAndShowDueReminders();
                            }
                            // Refresh reminders every second
                            loadSchedule();
                        }),
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

        DateTimeFormatter tfmt = DateTimeFormatter.ofPattern("h:mm a");
        List<MedicineReminder> validReminders = new ArrayList<>();

        // Filter and collect valid reminders
        for (MedicineReminder r : allReminders) {
            // Check if reminder is active today (between start and end dates)
            boolean isActiveToday = !today.isBefore(r.getStartDate()) &&
                    !today.isAfter(r.getEndDate());

            // Check if time is upcoming (not past current time)
            boolean isUpcoming = r.getTime().isAfter(now);

            if (isActiveToday && isUpcoming) {
                validReminders.add(r);
            }
        }

        // Sort reminders by time (earliest first)
        validReminders.sort(Comparator.comparing(MedicineReminder::getTime));

        boolean hasReminders = false;

        for (MedicineReminder r : validReminders) {
            hasReminders = true;
            HBox card = new HBox(15);
            card.getStyleClass().add("reminder-card");
            card.setAlignment(Pos.CENTER_LEFT);

            // Time container with box (left side)
            VBox timeContainer = new VBox();
            timeContainer.setAlignment(Pos.CENTER_LEFT);
            timeContainer.getStyleClass().add("reminder-time-box");

            Label timeLabel = new Label(r.getTime().format(tfmt));
            timeLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
            timeLabel.getStyleClass().add("reminder-time");
            timeContainer.getChildren().add(timeLabel);

            // Details container (right side)
            VBox detailsContainer = new VBox(4);
            detailsContainer.setAlignment(Pos.CENTER_LEFT);
            detailsContainer.getStyleClass().add("reminder-details-container");

            Label medicineLabel = new Label(r.getMedicineName() + " " + r.getDosage());
            medicineLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
            medicineLabel.getStyleClass().add("reminder-medicine");

            String noteText = r.getNote() != null && !r.getNote().trim().isEmpty() ? r.getNote() : "N/A";
            Label noteLabel = new Label(noteText);
            noteLabel.setFont(Font.font("Poppins", FontPosture.ITALIC, 14));
            noteLabel.getStyleClass().add("reminder-note");

            detailsContainer.getChildren().addAll(medicineLabel, noteLabel);

            card.getChildren().addAll(timeContainer, detailsContainer);
            scheduleContainer.getChildren().add(card);
        }

        if (!hasReminders) {
            Label emptyLabel = new Label("No upcoming reminders for today");
            emptyLabel.setFont(Font.font("Poppins", FontWeight.MEDIUM, 16));
            emptyLabel.getStyleClass().add("empty-label");
            scheduleContainer.getChildren().add(emptyLabel);
        }
    }

    private void checkAndShowDueReminders() {
        List<MedicineReminder> allReminders = reminderService.getAllReminders();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Prevent duplicate dialogs within the same tick if duplicates exist in storage
        Set<String> emittedThisTick = new HashSet<>();

        for (MedicineReminder r : allReminders) {
            boolean isActiveToday = !today.isBefore(r.getStartDate()) && !today.isAfter(r.getEndDate());
            if (!isActiveToday) continue;

            // Trigger when hour and minute match current time
            if (r.getTime().getHour() == now.getHour() && r.getTime().getMinute() == now.getMinute()) {
                String medName = r.getMedicineName() == null ? "" : r.getMedicineName().trim().toLowerCase();
                String dosage  = r.getDosage() == null ? "" : r.getDosage().trim().toLowerCase();
                String minuteKey = today + "|" + String.format("%02d:%02d", r.getTime().getHour(), r.getTime().getMinute())
                        + "|" + medName + "|" + dosage;

                if (SHOWN_ALERTS.contains(minuteKey) || emittedThisTick.contains(minuteKey)) {
                    continue;
                }
                emittedThisTick.add(minuteKey);
                SHOWN_ALERTS.add(minuteKey);

                String timeText = r.getTime().format(DateTimeFormatter.ofPattern("h:mm a"));
                String med = r.getMedicineName() + (r.getDosage() != null && !r.getDosage().isBlank() ? (" " + r.getDosage()) : "");
                String note = (r.getNote() != null && !r.getNote().isBlank()) ? r.getNote() : "No additional information";

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Medicine Reminder");
                alert.setHeaderText("It's time: " + timeText);
                alert.setContentText("Medicine: " + med + "\n" + "Info: " + note);
                alert.show();
            }
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
    private void handleCostEstimator(ActionEvent e) {
        swapRoot("/com/momentum/dosein/fxml/cost_estimator.fxml", e);
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