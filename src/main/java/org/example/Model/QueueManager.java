package org.example.Model;


import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class QueueManager implements Runnable {

    private Integer timeLimit;
    private Integer minArrivalTime;
    private Integer maxArrivalTime;
    private Integer minServiceTime;
    private Integer maxServiceTime;
    private Integer numberOfQueues;
    private Integer numberOfClients;
    private QueueService queueService;
    private ArrayList<Client> generatedClients;
    private TextArea displayText;
    private Integer currentTime = 0;
    private boolean isStillRunning = true;
    private double averageWait = 0;
    private double averageService = 0;
    private Integer peakHour = 0;
    private Integer maxClients = 0;

    public TextArea getDisplayText() {
        return displayText;
    }

    public QueueManager(Integer timeLimit, Integer minArrivalTime, Integer maxArrivalTime, Integer minServiceTime, Integer maxServiceTime, Integer numberOfQueues, Integer numberOfClients, TextArea displayText) {
        this.timeLimit = timeLimit;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minServiceTime = minServiceTime;
        this.maxServiceTime = maxServiceTime;
        this.numberOfQueues = numberOfQueues;
        this.numberOfClients = numberOfClients;
        this.displayText = displayText;
        this.displayText.setText("");

        queueService = new QueueService(numberOfQueues);
        generatedClients = new ArrayList<>();
        generateClients(generatedClients);
    }

    private void generateClients(ArrayList<Client> generatedClients) {
        Random rand1 = new Random();
        Random rand2 = new Random();
        for (int i = 1; i <= numberOfClients; i++) {
            generatedClients.add(new Client(i, rand1.nextInt(maxArrivalTime - minArrivalTime) + minArrivalTime, rand2.nextInt(maxServiceTime - minServiceTime) + minServiceTime));
            averageService = averageService + generatedClients.get(i - 1).getServiceTime();
        }
        averageService = averageService / numberOfClients;
        generatedClients.sort(Comparator.comparing(Client::getArrivalTime));
    }

    @Override
    public void run() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String reportName = now.getYear() + "-" + now.getMonthValue() + "-" + now.getDayOfMonth() + " " + now.getHour() + "-" + now.getMinute() + "-" + now.getSecond() + ".txt";
        File report = new File(reportName);
        try {
            report.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (currentTime <= timeLimit && isStillRunning) {
            /*Platform.runLater(() -> {
                displayText.clear();
            });*/
            addClientsToQueues();
            displayWaitingClients(reportName);
            displayQueues(reportName);
            isStillRunning = checkIfProgramFinished();
            currentTime = goToNextStep();
        }
        String lastString = new String("\nAverage Waiting time: " + averageWait / numberOfClients + "\n" + "Average Service Time: " + averageService + "\n" + "Peak Hour: " + peakHour);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(reportName, true));
            writer.append(lastString);
            writer.close();
            final String s = new String("\nSimulation is done. Check text file for additional statistics.");
            Platform.runLater(() -> {
                updateGUI(s);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("done");
    }

    private void displayWaitingClients(String reportName) {
        try {
            System.out.println(currentTime + "/" + timeLimit);
            BufferedWriter writer = new BufferedWriter(new FileWriter(reportName, true));
            writer.append("\nSimulation Time ").append(String.valueOf(currentTime));
            final String s = new String("\nSimulation Time " + currentTime + "\n");
            Platform.runLater(() -> {
                updateGUI(s);
            });
            StringBuilder builder = new StringBuilder();
            builder.append("Waiting clients: ");
            for (Client client : generatedClients) {
                if (!client.getArrivalTime().equals(currentTime)) {
                    builder.append(client.toString());
                    builder.append(";");
                }
            }
            String str = builder.toString();
            str = str.substring(0, str.length() - 1);
            final String s1 = new String(str + "\n");
            Platform.runLater(() -> {
                updateGUI(new String(s1));
            });
            writer.newLine();
            writer.append(str);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addClientsToQueues() {
        if (!generatedClients.isEmpty()) {
            ArrayList<Client> temp = new ArrayList<>();
            for (Client client : generatedClients) {
                if (client.getArrivalTime().equals(currentTime)) {
                    queueService.dispatchClient(client);
                    for (Client tempClient : queueService.getQueues().get(client.getQueue()).getClients()) {
                        if (!tempClient.equals(client)) {
                            averageWait = averageWait + tempClient.getServiceTime();
                        }
                    }
                    temp.add(client);
                }
            }
            generatedClients.removeAll(temp);
        }
    }

    private void displayQueues(String reportName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(reportName, true));
            Integer tempSize = 0;
            for (int i = 0; i < numberOfQueues; i++) {
                boolean found = false;
                writer.append("Queue ").append(String.valueOf(i)).append(": ");
                final String s = new String("Queue" + i + ":");
                Platform.runLater(() -> {
                    updateGUI(new String(s));
                });
                tempSize += queueService.getQueues().get(i).getClients().size();
                for (Client client : queueService.getQueues().get(i).getClients()) {
                    if (client.getServiceTime() > 0) {
                        writer.append(client.toString()).append(";");
                        final String s2 = new String(client.toString() + ";");
                        Platform.runLater(() -> {
                            updateGUI(s2);
                        });
                        if (client.equals(queueService.getQueues().get(i).getClients().peek())) {
                            client.setServiceTime(client.getServiceTime() - 1);
                        }
                        found = true;
                    }
                }
                if (!found) {
                    Platform.runLater(() -> {
                        updateGUI("closed");
                    });
                    writer.append(" closed");
                }
                Platform.runLater(() -> {
                    updateGUI("\n");
                });
                writer.newLine();
            }
            if (tempSize > maxClients) {
                maxClients = tempSize;
                peakHour = currentTime;
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfProgramFinished() {
        if (generatedClients.isEmpty()) {
            boolean allEmpty = true;
            for (int i = 0; i < numberOfQueues; i++) {
                if (!queueService.getQueues().get(i).getClients().isEmpty()) {
                    allEmpty = false;
                }
            }
            if (allEmpty == true) {
                isStillRunning = false;
            }
        }
        return isStillRunning;
    }

    private Integer goToNextStep() {
        try {
            currentTime++;
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return currentTime;
    }

    private void updateGUI(String s) {
        displayText.appendText(s);
    }


}