package org.example;

import java.io.*;
import java.util.Objects;

import static org.example.MainNetwork.numberPortAndHost;
import static org.example.MainNetwork.portFile;

public class MainClient {

    public static void main(String[] args) throws IOException {

        Client client = new Client();

        String host = numberPortAndHost(portFile, "host");
        int port = Integer.parseInt(Objects.requireNonNull(numberPortAndHost(portFile, "port")));

        client.startMessagesInServer(host, port);
    }
}