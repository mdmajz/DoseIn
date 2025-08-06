package com.momentum.dosein.controller;

import com.momentum.dosein.model.DoctorContact;
import com.momentum.dosein.service.DoctorService;
import com.momentum.dosein.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import java.io.IOException;
import java.util.List;

public class DoctorContactsController {

    @FXML private ListView<DoctorContact> contactsList;
    @FXML private Label nameLabel;
    @FXML private Label specialtyLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label locationLabel;
    @FXML private Label noteLabel;

    private final DoctorService service = new DoctorService();

    @FXML
    public void initialize() {
        // load persisted contacts
        List<DoctorContact> all = service.getAllContacts();
        contactsList.getItems().setAll(all);

        // Create custom cell factory for doctor contact bars
        contactsList.setCellFactory(lv -> new ListCell<DoctorContact>() {
            @Override
            protected void updateItem(DoctorContact dc, boolean empty) {
                super.updateItem(dc, empty);
                if (empty || dc == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Create custom layout for doctor contact bar
                    HBox contactBar = new HBox();
                    contactBar.setAlignment(Pos.CENTER_LEFT);
                    contactBar.setSpacing(10);
                    contactBar.setStyle("-fx-padding: 0 15;");
                    
                    // Doctor name on the left
                    Label nameLabel = new Label(dc.getName());
                    nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
                    
                    // Spacer to push phone to the right
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    // Phone number on the right
                    Label phoneLabel = new Label(dc.getPhone());
                    phoneLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
                    
                    contactBar.getChildren().addAll(nameLabel, spacer, phoneLabel);
                    setGraphic(contactBar);
                    setText(null);
                }
            }
        });

        contactsList.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldDc, newDc) -> showDetails(newDc));
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
    private void handleGoBack(ActionEvent e) {
        // This method is for the "Go back" button in the doctor info panel
        // For now, it just clears the selection
        contactsList.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleDelete(ActionEvent e) {
        DoctorContact sel = contactsList.getSelectionModel().getSelectedItem();
        if (sel==null) {
            new Alert(Alert.AlertType.WARNING,
                    "Select a contact first.").showAndWait();
            return;
        }
        service.deleteContact(sel);
        initialize();  // reload list & clear details
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
