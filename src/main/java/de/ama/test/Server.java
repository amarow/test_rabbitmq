package de.ama.test;

import de.ama.mq.server.ServerContext;

public class Server {
    public static void main(String[] args) {
        new ServerContext().start();
    }
}
