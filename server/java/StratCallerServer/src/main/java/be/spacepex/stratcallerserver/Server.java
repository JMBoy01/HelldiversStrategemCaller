package be.spacepex.stratcallerserver;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    int port = 7070;

    ServerSocket serverSocket;
    Socket socket;
    BufferedReader br;
    StratCallerServerController controller;
    public Server(StratCallerServerController controller){
        this.controller = controller;
    }
    public void establishConnection(String clientIp){
        controller.printToConsole("--------------------START--------------------");
        controller.printToConsole("Establishing connection...");
        try {
            Socket socket = new Socket(clientIp, port);

            PrintWriter pw = new PrintWriter(socket.getOutputStream());

            String serverIp = getHostIp();

            pw.write(serverIp);
            pw.flush();
            pw.close();

            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        controller.printToConsole("Waiting for response...");

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String msg = br.readLine();
            if(msg != null){
                controller.printToConsole("Response: " + msg);
                controller.printToConsole("Connection established!");
            }
            else{
                controller.printToConsole("Something went wrong, try again.");
            }

            socket.close();
            serverSocket.close();
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Runnable receiveAndPressKeyInputs(){
        return () -> {
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Robot robot;
            try {
                robot = new Robot();
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                try {
                    socket = serverSocket.accept();
                    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    String msg = br.readLine();
                    Input input = Input.valueOf(msg);

                    controller.printToConsole("Input: " + msg);

                    switch (input) {
                        case STRATEGEM_MENU_DOWN -> {
                            robot.keyPress(KeyEvent.VK_C);
                        }
                        case STRATEGEM_MENU_UP -> {
                            robot.keyRelease(KeyEvent.VK_C);
                        }
                        case STRATEGEM_MENU_PRESS -> {
                            robot.keyPress(KeyEvent.VK_C);
                            robot.keyRelease(KeyEvent.VK_C);
                        }
                        case UP -> {
                            robot.keyPress(KeyEvent.VK_UP);
                            robot.keyRelease(KeyEvent.VK_UP);
                        }
                        case DOWN -> {
                            robot.keyPress(KeyEvent.VK_DOWN);
                            robot.keyRelease(KeyEvent.VK_DOWN);
                        }
                        case LEFT -> {
                            robot.keyPress(KeyEvent.VK_LEFT);
                            robot.keyRelease(KeyEvent.VK_LEFT);
                        }
                        case RIGHT -> {
                            robot.keyPress(KeyEvent.VK_RIGHT);
                            robot.keyRelease(KeyEvent.VK_RIGHT);
                        }
                    }

                    br.close();
                } catch (Exception e) {
                    if (!(e instanceof SocketTimeoutException)) {
                        try {
                            serverSocket.close();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                        throw new RuntimeException(e);
                    }
                } finally {
                    try {
                        if(serverSocket != null) {
                            serverSocket.close();
                        }
                        if(socket != null){
                            socket.close();
                        }
                        if(br != null){
                            br.close();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
    }

    public void stopServerSocket(String clientIp){
        try {
            sendStopMsg(clientIp);
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendStopMsg(String clientIp){
        try {
            Socket socket = new Socket(clientIp, port-1);

            PrintWriter pw = new PrintWriter(socket.getOutputStream());

            pw.write("STOP");
            pw.flush();
            pw.close();

            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getHostIp(){
        String hostIp;
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            hostIp = socket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return hostIp;
    }

    public boolean validateIpAddress(String ipAddress){
        String IPADDRESS_PATTERN =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                        + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                        + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                        + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches() || ipAddress == "localhost";
    }
}
