package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class Servers {
    public static final String serverName = "SERVER";

    private static final ConcurrentMap<ClientServer, PrintWriter> mapServer = new ConcurrentHashMap<>();

    public static ConcurrentMap<ClientServer, PrintWriter> getMapServer() {
        return mapServer;
    }

    public static void sendMessageClient(String text) throws IOException {
        for (PrintWriter pw : mapServer.values()) {
            pw.println(text);
        }
    }

    public static void sendMessageOtherClients(String text, ClientServer cs) throws IOException {
        for (ClientServer clientServer : mapServer.keySet()) {
            if (!clientServer.equals(cs)) {
                mapServer.get(clientServer).println(text);
            }
        }
    }
}