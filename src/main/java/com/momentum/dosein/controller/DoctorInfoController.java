package com.momentum.dosein.controller;

import com.momentum.dosein.model.DoctorContact;
import com.momentum.dosein.service.DoctorService;
import com.momentum.dosein.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import java.io.IOException;

public class DoctorInfoController {

    @FXML private Label nameLabel;
    @FXML private Label specialtyLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label locationLabel;
    @FXML private Label noteLabel;

    private final DoctorService service = new DoctorService();
    private DoctorContact currentDoctor;

    @FXML
    public void initialize() {
        // Get the selected doctor from the previous scene
        // This will be set when navigating to this scene
    }

    public void setDoctor(DoctorContact doctor) {
        this.currentDoctor = doctor;
        if (doctor != null) {
            nameLabel.setText(doctor.getName());
            specialtyLabel.setText(doctor.getSpecialty());
            phoneLabel.setText(doctor.getPhone());
            emailLabel.setText(doctor.getEmail());
            locationLabel.setText(doctor.getLocation());
            noteLabel.setText(doctor.getNote());
        }
    }

    @FXML
    private void handleGoBack(ActionEvent e) {
        try {
            Parent p = FXMLLoader.load(
                    getClass().getResource("/com/momentum/dosein/fxml/doctor_contacts.fxml")
            );
            Scene s = ((Node)e.getSource()).getScene();
            s.setRoot(p);
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Could not load Doctor Contacts screen.").showAndWait();
        }
    }

    @FXML
    private void handleDelete(ActionEvent e) {
        if (currentDoctor == null) {
            new Alert(Alert.AlertType.WARNING,
                    "No doctor selected.").showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Doctor");
        alert.setHeaderText("Delete Doctor Contact");
        alert.setContentText("Are you sure you want to delete " + currentDoctor.getName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                service.deleteContact(currentDoctor);
                handleGoBack(e);
            }
        });
    }

    // sidebar navigation stubs:
    @FXML private void handleDashboard(ActionEvent e)       { navigate("/com/momentum/dosein/fxml/dashboard.fxml", e); }
    @FXML private void handleManageSchedule(ActionEvent e)  { navigate("/com/momentum/dosein/fxml/manage_schedule.fxml", e); }
    @FXML private void handleDoctorContacts(ActionEvent e)  { navigate("/com/momentum/dosein/fxml/doctor_contacts.fxml", e); }
    @FXML private void handleEmergency(ActionEvent e)       { navigate("/com/momentum/dosein/fxml/emergency.fxml", e); }
    @FXML private void handleAboutUs(ActionEvent e)         { navigate("/com/momentum/dosein/fxml/about_us.fxml", e); }
    @FXML private void handleSignOut(ActionEvent e)         {
        Session.clear();
        navigate("/com/momentum/dosein/fxml/login.fxml", e);
    }

    private void navigate(String fxml, ActionEvent e) {
        try {
            Parent p = FXMLLoader.load(getClass().getResource(fxml));
            ((Node)e.getSource()).getScene().setRoot(p);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}