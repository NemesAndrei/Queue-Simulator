package org.example.Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Queue implements Runnable {
    private BlockingQueue<Client> clients;
    private AtomicInteger waitingTime;

    public BlockingQueue<Client> getClients() {
        return clients;
    }

    public AtomicInteger getWaitingTime() {
        return waitingTime;
    }

    public void setClients(BlockingQueue<Client> clients) {
        this.clients = clients;
    }

    public void setWaitingTime(AtomicInteger waitingTime) {
        this.waitingTime = waitingTime;
    }

    public Queue() {
        clients = new LinkedBlockingQueue<>();
        waitingTime = new AtomicInteger(0);
    }

    public void addClient(Client client) {
        clients.add(client);
        waitingTime.getAndAdd(client.getServiceTime());
    }


    @Override
    public void run() {
        while (true) {
            try {
                if (!clients.isEmpty()) {

                    Client client = clients.peek();
                    if (client.getServiceTime() == 0) {
                        clients.poll();
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
