package org.example.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.Model.QueueManager;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    @FXML
    private TextField runningTime;

    @FXML
    private Button startSimulation;

    @FXML
    private Label runningTimeLabel;

    @FXML
    private TextField noOfQueues;

    @FXML
    private Label queuesLabel;

    @FXML
    private Label clientsLabel;

    @FXML
    private TextField noOfClients;

    @FXML
    private Label minArrivalLabel;

    @FXML
    private TextField minArrivalTime;

    @FXML
    private Label maxArrivalLabel;

    @FXML
    private TextField maxArrivalTime;

    @FXML
    private Label minServiceLabel;

    @FXML
    private TextField minServiceTime;

    @FXML
    private Label maxServiceLabel;

    @FXML
    private TextField maxServiceTime;

    @FXML
    public TextArea textArea;

    QueueManager manager;

    boolean isNumeric(String s) {
        if (s == null) {
            return false;
        }
        try {
            Integer d = Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @FXML
    void handleEvents(ActionEvent event) {
        if (event.getSource() == startSimulation) {
            if (isNumeric(runningTime.getText())) {
                Integer runTime = Integer.valueOf(runningTime.getText());
                if (isNumeric(noOfQueues.getText())) {
                    Integer queueNo = Integer.valueOf(noOfQueues.getText());
                    if (isNumeric(noOfClients.getText())) {
                        Integer clientsNo = Integer.valueOf(noOfClients.getText());
                        if (isNumeric(minArrivalTime.getText())) {
                            Integer minArrival = Integer.valueOf(minArrivalTime.getText());
                            if (isNumeric(maxArrivalTime.getText())) {
                                Integer maxArrival = Integer.valueOf(maxArrivalTime.getText());
                                if (minArrival < maxArrival) {
                                    if (isNumeric(minServiceTime.getText())) {
                                        Integer minService = Integer.valueOf(minServiceTime.getText());
                                        if (isNumeric(maxServiceTime.getText())) {
                                            Integer maxService = Integer.valueOf(maxServiceTime.getText());
                                            if (minService < maxService) {
                                                manager = new QueueManager(runTime, minArrival, maxArrival, minService, maxService, queueNo, clientsNo, textArea);
                                                Thread thread = new Thread(manager);
                                                thread.start();
                                            } else printErrorMessage("MIN SERVICE TIME MUST BE SMALLER THAN MAX SERVICE TIME");
                                        } else printErrorMessage("INVALID MAX SERVICE TIME");
                                    } else printErrorMessage("INVALID MIN SERVICE TIME");
                                } else printErrorMessage("MIN ARRIVAL TIME MUST BE SMALLER THAN MAX ARRIVAL TIME");
                            } else printErrorMessage("INVALID MAX ARRIVAL TIME");
                        } else printErrorMessage("INVALID MIN ARRIVAL TIME");
                    } else printErrorMessage("INVALID NO OF CLIENTS");
                } else printErrorMessage("INVALID NO OF QUEUES");
            } else printErrorMessage("INVALID RUNNING TIME");
        }
    }

    private void printErrorMessage(String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Warning Dialog");
        alert.setContentText(s);
        alert.showAndWait();
    }
    public void printInstructions()
    {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Instructions");
        alert.setContentText("Insert integer values in all of the available fields\nIf one of the fields is not numerical you will receive an error\nThe min service/arrival time must be smaller than the max service/arrival time, or else the program will also throw an error");
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
