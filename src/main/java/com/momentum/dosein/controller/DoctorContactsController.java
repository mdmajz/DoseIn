package com.momentum.dosein.controller;

import com.momentum.dosein.model.DoctorContact;
import com.momentum.dosein.service.DoctorService;
import com.momentum.dosein.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.List;

public class DoctorContactsController {

    @FXML private VBox contactsContainer;
    @FXML private Label nameLabel;
    @FXML private Label specialtyLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label locationLabel;
    @FXML private Label noteLabel;

    private final DoctorService service = new DoctorService();
    private DoctorContact selectedContact = null;

    @FXML
    public void initialize() {
        loadContacts();
    }

    private void loadContacts() {
        // Clear existing buttons
        contactsContainer.getChildren().clear();
        
        // Load persisted contacts
        List<DoctorContact> all = service.getAllContacts();
        
        // Create buttons for each contact
        for (DoctorContact contact : all) {
            Button contactButton = new Button(contact.getName());
            contactButton.getStyleClass().add("contact-button");
            contactButton.setOnAction(e -> selectContact(contact));
            contactsContainer.getChildren().add(contactButton);
        }
    }

    private void selectContact(DoctorContact contact) {
        // Remove selection from all buttons
        for (javafx.scene.Node node : contactsContainer.getChildren()) {
            if (node instanceof Button) {
                node.getStyleClass().remove("contact-button-selected");
                node.getStyleClass().add("contact-button");
            }
        }
        
        // Add selection to clicked button
        for (javafx.scene.Node node : contactsContainer.getChildren()) {
            if (node instanceof Button && ((Button) node).getText().equals(contact.getName())) {
                node.getStyleClass().remove("contact-button");
                node.getStyleClass().add("contact-button-selected");
                break;
            }
        }
        
        selectedContact = contact;
        showDetails(contact);
    }

    private void showDetails(DoctorContact dc) {
        if (dc==null) {
            nameLabel.setText("");
            specialtyLabel.setText("");
            phoneLabel.setText("");
            emailLabel.setText("");
            locationLabel.setText("");
            noteLabel.setText("");
        } else {
            nameLabel.setText(dc.getName());
            specialtyLabel.setText(dc.getSpecialty());
            phoneLabel.setText(dc.getPhone());
            emailLabel.setText(dc.getEmail());
            locationLabel.setText(dc.getLocation());
            noteLabel.setText(dc.getNote());
        }
    }

    @FXML
    private void handleAdd(ActionEvent e) {
        try {
            Parent p = FXMLLoader.load(
                    getClass().getResource("/com/momentum/dosein/fxml/add_doctor.fxml")
            );
            Scene s = ((Node)e.getSource()).getScene();
            s.setRoot(p);
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Could not load Add Doctor screen.").showAndWait();
        }
    }

    @FXML
    private void handleDelete(ActionEvent e) {
        if (selectedContact == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Select a contact first.").showAndWait();
            return;
        }
        service.deleteContact(selectedContact);
        loadContacts();  // reload list & clear details
        selectedContact = null;
        showDetails(null);
    }

    // sidebar navigation stubs:
    @FXML private void handleDashboard(ActionEvent e)       { navigate("/com/momentum/dosein/fxml/dashboard.fxml", e); }
    @FXML private void handleManageSchedule(ActionEvent e)  { navigate("/com/momentum/dosein/fxml/manage_schedule.fxml", e); }
    @FXML private void handleDoctorContacts(ActionEvent e)  { /* noop */ }
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
