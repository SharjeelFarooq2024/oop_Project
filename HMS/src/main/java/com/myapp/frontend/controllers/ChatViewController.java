package com.myapp.frontend.controllers;

import com.myapp.backend.model.*;
import com.myapp.backend.services.ChatService;
import com.myapp.backend.services.NotificationService;
import com.myapp.backend.dao.DoctorDAO;
import com.myapp.backend.dao.PatientDAO;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ChatViewController implements Initializable {

    @FXML private ListView<String> contactsListView;
    @FXML private ListView<ChatMessage> messageListView;
    @FXML private TextField messageField;
    @FXML private Button sendButton;
    @FXML private Button backButton;
    @FXML private Label chatHeaderLabel;
    @FXML private CheckBox sendEmailCheckbox;

    private User currentUser;
    private User selectedContact;
    private boolean isDoctor;
    private DoctorDAO doctorDAO = new DoctorDAO();
    private PatientDAO patientDAO = new PatientDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupMessageCellFactory();
        
        sendButton.setOnAction(e -> sendMessage());
        backButton.setOnAction(e -> goBack());
        
        contactsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadSelectedContact(newVal);
            }
        });
        
        messageField.setOnAction(e -> sendMessage()); // Send on Enter key
    }
    
    public void setCurrentUser(User user, boolean isDoctor) {
        this.currentUser = user;
        this.isDoctor = isDoctor;
        
        chatHeaderLabel.setText(isDoctor ? 
            "Dr. " + user.getName() + " - Chat" : 
            user.getName() + " - Chat");
            
        loadContacts();
    }
    
    private void loadContacts() {
        ObservableList<String> contacts = FXCollections.observableArrayList();
        
        if (isDoctor) {
            // Doctor sees patients
            Doctor doctor = (Doctor) currentUser;
            if (doctor.getPatientIds() != null) {
                for (String patientId : doctor.getPatientIds()) {
                    Patient patient = patientDAO.findById(patientId);
                    if (patient != null) {
                        contacts.add(patient.getName() + " (" + patient.getId() + ")");
                    }
                }
            }
        } else {
            // Patient sees assigned doctors
            List<Doctor> allDoctors = doctorDAO.loadDoctors();
            String patientId = currentUser.getId();
            
            for (Doctor doctor : allDoctors) {
                if (doctor.getPatientIds() != null && doctor.getPatientIds().contains(patientId)) {
                    contacts.add("Dr. " + doctor.getName() + " (" + doctor.getId() + ")");
                }
            }
        }
        
        contactsListView.setItems(contacts);
    }
    
    private void loadSelectedContact(String contactInfo) {
        String contactId = extractIdFromContactInfo(contactInfo);
        
        if (isDoctor) {
            selectedContact = patientDAO.findById(contactId);
        } else {
            selectedContact = doctorDAO.findById(contactId);
        }
        
        if (selectedContact != null) {
            loadConversation();
        }
    }
    
    private String extractIdFromContactInfo(String contactInfo) {
        // Format: "Name (id)"
        int start = contactInfo.lastIndexOf("(") + 1;
        int end = contactInfo.lastIndexOf(")");
        if (start > 0 && end > start) {
            return contactInfo.substring(start, end);
        }
        return "";
    }
    
    private void loadConversation() {
        if (selectedContact == null || currentUser == null) return;
        
        List<ChatMessage> messages = ChatService.getConversation(
            currentUser.getId(), selectedContact.getId());
        
        ObservableList<ChatMessage> observableMessages = 
            FXCollections.observableArrayList(messages);
        messageListView.setItems(observableMessages);
        
        // Mark messages as read
        ChatService.markConversationAsRead(currentUser.getId(), selectedContact.getId());
        
        // Scroll to bottom
        if (!messages.isEmpty()) {
            messageListView.scrollTo(messages.size() - 1);
        }
    }
    
    private void sendMessage() {
        if (selectedContact == null || messageField.getText().isEmpty()) return;
        
        String content = messageField.getText();
        
        // Send message through ChatService
        ChatService.sendMessage(
            currentUser.getId(), 
            selectedContact.getId(), 
            content,
            sendEmailCheckbox.isSelected()
        );
        
        // Clear input field
        messageField.clear();
        
        // Reload conversation to show new message
        loadConversation();
    }
    
    private void setupMessageCellFactory() {
        messageListView.setCellFactory(new Callback<ListView<ChatMessage>, ListCell<ChatMessage>>() {
            @Override
            public ListCell<ChatMessage> call(ListView<ChatMessage> param) {
                return new ListCell<ChatMessage>() {
                    @Override
                    protected void updateItem(ChatMessage item, boolean empty) {
                        super.updateItem(item, empty);
                        
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                            return;
                        }
                        
                        VBox messageBox = new VBox(5);
                        
                        boolean isSentByCurrentUser = item.getSenderId().equals(currentUser.getId());
                        String sender = isSentByCurrentUser ? "You" : 
                            (isDoctor ? selectedContact.getName() : "Dr. " + selectedContact.getName());
                        
                        Label senderLabel = new Label(sender);
                        senderLabel.setStyle("-fx-font-weight: bold;");
                        
                        Text messageText = new Text(item.getContent());
                        messageText.setWrappingWidth(messageListView.getWidth() * 0.8);
                        
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm");
                        Label timeLabel = new Label(item.getTimestamp().format(formatter));
                        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #757575;");
                        
                        messageBox.getChildren().addAll(senderLabel, messageText, timeLabel);
                        
                        String boxStyle = isSentByCurrentUser ?
                            "-fx-background-color: #e3f2fd; -fx-padding: 10; -fx-background-radius: 10;" :
                            "-fx-background-color: #f5f5f5; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 10;";
                        
                        messageBox.setStyle(boxStyle);
                        
                        HBox container = new HBox();
                        if (isSentByCurrentUser) {
                            container.setStyle("-fx-alignment: CENTER-RIGHT;");
                        } else {
                            container.setStyle("-fx-alignment: CENTER-LEFT;");
                        }
                        
                        container.getChildren().add(messageBox);
                        setGraphic(container);
                    }
                };
            }
        });
    }
    
    private void goBack() {
        try {
            String fxmlPath;
            
            if (isDoctor) {
                fxmlPath = "/fxml/DoctorDashboard.fxml";
            } else {
                fxmlPath = "/fxml/PatientDashboard.fxml";
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            if (isDoctor) {
                DoctorDashboardController controller = loader.getController();
                controller.setLoggedInDoctor((Doctor) currentUser);
            } else {
                PatientDashboardController controller = loader.getController();
                controller.setLoggedInPatient((Patient) currentUser);
            }
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error returning to dashboard");
        }
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}