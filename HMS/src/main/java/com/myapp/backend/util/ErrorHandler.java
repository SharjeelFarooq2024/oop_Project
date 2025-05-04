package com.myapp.backend.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for consistent error handling across the application.
 */
public class ErrorHandler {
    private static final Logger logger = Logger.getLogger(ErrorHandler.class.getName());
    
    /**
     * Handles an exception by logging it and optionally showing an alert dialog.
     * 
     * @param ex The exception to handle.
     * @param showToUser Whether to show an alert to the user.
     * @param title The title of the alert (if shown).
     * @param message The message to show in the alert.
     */
    public static void handleException(Exception ex, boolean showToUser, String title, String message) {
        // Log the exception
        logger.log(Level.SEVERE, message, ex);
        
        // Show alert if requested
        if (showToUser) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText("An error occurred");
                alert.setContentText(message);
                
                // Create expandable exception details
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String exceptionText = sw.toString();
                
                TextArea textArea = new TextArea(exceptionText);
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                
                GridPane.setVgrow(textArea, Priority.ALWAYS);
                GridPane.setHgrow(textArea, Priority.ALWAYS);
                
                GridPane expContent = new GridPane();
                expContent.setMaxWidth(Double.MAX_VALUE);
                expContent.add(textArea, 0, 0);
                
                alert.getDialogPane().setExpandableContent(expContent);
                alert.showAndWait();
            });
        }
    }
    
    /**
     * Logs a message at the specified level.
     * 
     * @param level The logging level.
     * @param message The message to log.
     */
    public static void logMessage(Level level, String message) {
        logger.log(level, message);
    }
    
    /**
     * Shows a simple alert to the user.
     * 
     * @param type The type of alert.
     * @param title The title of the alert.
     * @param message The message to show.
     */
    public static void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}