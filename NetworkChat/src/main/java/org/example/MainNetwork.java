package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static org.example.MainBasic.fileLoggerAllMessage;

public class MainNetwork {
    public static File portFile = new File("settings.txt");
    public static File fileLoggerAllMessagesInServer = new File("NetworkChat/FileServer.log");

    public static void main(String[] args) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(portFile)) {
            byte[] bytes = ("host: 127.0.0.1\n" +
                    "port: 8095").getBytes();
            fos.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int port = Integer.parseInt(Objects.requireNonNull(numberPortAndHost(portFile, "port")));
        ServerSocket serverSocket = new ServerSocket(port);

        String textFirst = "Server started!\n";
        System.out.printf(textFirst);
        fileLoggerAllMessage(fileLoggerAllMessagesInServer, textFirst);
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();

                Runnable runnable = () -> {
                    try {
                        ClientServer client = new ClientServer (clientSocket);
                        client.start();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }};

                Thread thread = new Thread(runnable);
                thread.start();
                thread.join();
            }
        } catch (IOException e) {
            serverSocket.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static String numberPortAndHost(File file, String textAboutInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file)) {
            int i;
            while ((i = fis.read()) != -1) {
                stringBuilder.append((char) i);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if ("host".equalsIgnoreCase(textAboutInfo)) {
            return stringBuilder.substring(stringBuilder.indexOf("host: ") + 6, stringBuilder.indexOf("\n"));
        } else if ("port".equalsIgnoreCase(textAboutInfo)) {
            return stringBuilder.substring(stringBuilder.indexOf("port: ") + 6);
        }
        System.out.println("Ошибка: вы не выбрали указатель host/port");
        return null;
    }
}