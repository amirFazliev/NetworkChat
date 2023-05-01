package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static org.example.MainBasic.fileLoggerAllMessage;
import static org.example.MainBasic.messagePush;
import static org.example.MainNetwork.fileLoggerAllMessagesInServer;
import static org.example.Servers.*;

public class ClientServer extends Thread {

    private static Socket clientSocketServer;

    public ClientServer(Socket clientSocket) throws IOException {
        clientSocketServer = clientSocket;
    }

    @Override
    public void run() {
        String textFirstServerAfterConnectedClient =
                String.format("Connected new client. Your inet-address is %s, your port is %d\n", clientSocketServer.getInetAddress(), clientSocketServer.getPort());
        System.out.printf(textFirstServerAfterConnectedClient);
        fileLoggerAllMessage(fileLoggerAllMessagesInServer, textFirstServerAfterConnectedClient);

        new InputServerThread().start();
        new OutputServerThread().start();
    }

    private class InputServerThread extends Thread {
        @Override
        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocketServer.getInputStream()));

                while (true) {
                    try {
                        String answer = input.readLine();
                        System.out.println(answer);
                        fileLoggerAllMessage(fileLoggerAllMessagesInServer, answer);
                        sendMessageOtherClients(answer, ClientServer.this);
                        if (answer.endsWith("/exit")) {
                            input.close();
                            closedBuffer(this);
                            break;
                        }
                    } catch (IOException e) {
                        try {
                            input.close();
                            closedBuffer(this);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    protected class OutputServerThread extends Thread {

        @Override
        public void run() {
            try {
                PrintWriter output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocketServer.getOutputStream())), true);
                getMapServer().put(ClientServer.this, output);
                while (true) {
                    try {
                        String text = new Scanner(System.in).nextLine();
                        String question = messagePush(serverName, text);
                        fileLoggerAllMessage(fileLoggerAllMessagesInServer, question);
                        sendMessageClient(question);
                    } catch (IOException ignored) {
                        try {
                            output.close();
                            closedBuffer(this);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void closedBuffer(Thread thread) throws IOException {
        try {
            if (!clientSocketServer.isClosed()) {
                clientSocketServer.close();
                getMapServer().remove(ClientServer.this);
                thread.interrupt();
            }
        } catch (IOException ignored) {
        }
    }
}