package com.momentum.dosein.controller;

import com.momentum.dosein.model.MedicineReminder;
import com.momentum.dosein.service.ReminderService;
import com.momentum.dosein.util.Session;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SetReminderController {

    @FXML private TextField    nameField;
    @FXML private DatePicker   startDatePicker, endDatePicker;
    @FXML private Spinner<Integer> hourSpinner, minuteSpinner;
    @FXML private ToggleButton amToggle, pmToggle;
    @FXML private ListView<String> timesListView;
    @FXML private TextArea    noteField;

    private final ReminderService reminderService = new ReminderService();
    private final DateTimeFormatter displayFmt =
            DateTimeFormatter.ofPattern("h:mm a");
    private final List<String> selectedTimes = new ArrayList<>();

    @FXML
    private void initialize() {
        // Make spinners editable so user can type
        hourSpinner.setEditable(true);
        minuteSpinner.setEditable(true);

        // Spinner ranges
        hourSpinner.setValueFactory(new IntegerSpinnerValueFactory(1, 12, 12));
        minuteSpinner.setValueFactory(new IntegerSpinnerValueFactory(0, 59, 0));

        // Group AM/PM toggles
        ToggleGroup amPmGroup = new ToggleGroup();
        amToggle.setToggleGroup(amPmGroup);
        pmToggle.setToggleGroup(amPmGroup);
        amToggle.setSelected(true);
        
        // Initialize date pickers with current date
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(7));
    }

    @FXML
    private void handleAddTime(ActionEvent e) {
        int hour = hourSpinner.getValue() % 12;
        if (pmToggle.isSelected()) hour += 12;
        LocalTime t = LocalTime.of(hour, minuteSpinner.getValue());
        String timeText = t.format(displayFmt);
        
        if (!selectedTimes.contains(timeText)) {
            selectedTimes.add(timeText);
            timesListView.getItems().add(timeText);
        }
    }

    @FXML
    private void handleRemoveTime(ActionEvent e) {
        // Remove selected time from list
        String selectedTime = timesListView.getSelectionModel().getSelectedItem();
        if (selectedTime != null) {
            selectedTimes.remove(selectedTime);
            timesListView.getItems().remove(selectedTime);
        }
    }

    @FXML
    private void handleSetReminder(ActionEvent e) {
        String name = nameField.getText().trim();
        if (name.isEmpty() || selectedTimes.isEmpty()) {
            new Alert(Alert.AlertType.WARNING,
                    "Please enter a name and at least one alert time.")
                    .showAndWait();
            return;
        }

        String note = noteField.getText().trim();
        LocalDate start = startDatePicker.getValue();
        LocalDate end   = endDatePicker.getValue();

        for (String ts : selectedTimes) {
            LocalTime lt = LocalTime.parse(ts, displayFmt);
            MedicineReminder r = new MedicineReminder(name, "", lt, note);
            reminderService.addReminder(r);
        }

        new Alert(Alert.AlertType.INFORMATION,
                "Reminder(s) saved!").showAndWait();
        navigateToDashboard(e);
    }

    /** Cancel button → back to Dashboard */
    @FXML
    private void handleCancel(ActionEvent e) {
        navigateToDashboard(e);
    }

    /** Dashboard nav (button or logo click) → back to Dashboard */
    @FXML
    private void handleDashboard(Event e) {
        navigateToDashboard(e);
    }

    private void navigateToDashboard(Event event) {
        try {
            Parent dash = FXMLLoader.load(
                    getClass().getResource(
                            "/com/momentum/dosein/fxml/dashboard.fxml"
                    )
            );
            Scene scene = ((Node)event.getSource()).getScene();
            scene.setRoot(dash);
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Could not load Dashboard.").showAndWait();
        }
    }

    // Navigation handlers for all menu items
    @FXML 
    private void handleManageSchedule(ActionEvent e) {
        try {
            Parent schedule = FXMLLoader.load(
                    getClass().getResource(
                            "/com/momentum/dosein/fxml/manage_schedule.fxml"
                    )
            );
            Scene scene = ((Node)e.getSource()).getScene();
            scene.setRoot(schedule);
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Could not load Manage Schedule.").showAndWait();
        }
    }

    @FXML 
    private void handleDoctorContacts(ActionEvent e) {
        try {
            Parent contacts = FXMLLoader.load(
                    getClass().getResource(
                            "/com/momentum/dosein/fxml/doctor_contacts.fxml"
                    )
            );
            Scene scene = ((Node)e.getSource()).getScene();
            scene.setRoot(contacts);
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Could not load Doctor Contacts.").showAndWait();
        }
    }

    @FXML 
    private void handleEmergency(ActionEvent e) {
        try {
            Parent emergency = FXMLLoader.load(
                    getClass().getResource(
                            "/com/momentum/dosein/fxml/emergency.fxml"
                    )
            );
            Scene scene = ((Node)e.getSource()).getScene();
            scene.setRoot(emergency);
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Could not load Emergency.").showAndWait();
        }
    }

    @FXML 
    private void handleAboutUs(ActionEvent e) {
        try {
            Parent about = FXMLLoader.load(
                    getClass().getResource(
                            "/com/momentum/dosein/fxml/about_us.fxml"
                    )
            );
            Scene scene = ((Node)e.getSource()).getScene();
            scene.setRoot(about);
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Could not load About Us.").showAndWait();
        }
    }

    @FXML
    private void handleSignOut(Event e) {
        Session.clear();
        try {
            Parent login = FXMLLoader.load(
                    getClass().getResource(
                            "/com/momentum/dosein/fxml/login.fxml"
                    )
            );
            Scene scene = ((Node)e.getSource()).getScene();
            scene.setRoot(login);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
