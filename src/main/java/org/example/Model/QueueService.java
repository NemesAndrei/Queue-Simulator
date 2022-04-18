package org.example.Model;

import java.util.ArrayList;

public class QueueService {

    private ArrayList<Queue> queues = new ArrayList<>();

    public QueueService(Integer numberOfQueues) {
        for (int i = 0; i < numberOfQueues; i++) {
            Queue queue = new Queue();
            queues.add(queue);
            new Thread(queue).start();
        }
    }

    public ArrayList<Queue> getQueues() {
        return queues;
    }

    public Client dispatchClient(Client client) {
        int minimum = 9999;
        int index = 0;
        for (Queue queue : queues) {
            if (queue.getClients().size() < minimum) {
                minimum = queue.getClients().size();
                index = queues.indexOf(queue);
            }
        }
        for (Queue queue : queues) {
            if (queue.equals(queues.get(index))) {
                client.setQueue(index);
                queue.addClient(client);
                break;
            }
        }
        return client;
    }
}
