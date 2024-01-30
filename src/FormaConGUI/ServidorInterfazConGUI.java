package FormaConGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServidorInterfazConGUI {
    private JPanel PanelPrincipal;
    private JTextArea logArea;
    private JTextField fieldMensaje;
    private JButton btn_Enviar;

    private List<PrintWriter> clientWriters;


    public static void main(String[] args) {
        JFrame frame = new JFrame("ServidorInterfazConGUI");
        frame.setContentPane(new ServidorInterfazConGUI().PanelPrincipal);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0,0,400,350);
        frame.setVisible(true);
    }

    ServidorInterfazConGUI(){

        btn_Enviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessageToAllClients(fieldMensaje.getText());
                fieldMensaje.setText("");
            }
        });

        clientWriters = new ArrayList<>();
        startServer();

    }

    private void startServer() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(5000);
                log("Server started. Waiting for client...");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    log("Client connected: " + clientSocket.getInetAddress());

                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                    clientWriters.add(writer);

                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    new Thread(() -> {
                        try {
                            String line;
                            while ((line = in.readLine()) != null) {
                                log("Received from " + clientSocket.getInetAddress() + ": " + line);
                            }
                        } catch (IOException e) {
                            log("Error reading from client: " + e.getMessage());
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                log("Error closing client socket: " + e.getMessage());
                            }
                        }
                    }).start();
                }
            } catch (IOException e) {
                log("Error starting server: " + e.getMessage());
            }
        }).start();
    }

    private void sendMessageToAllClients(String message) {
        log("Server: " + message);
        for (PrintWriter writer : clientWriters) {
            writer.println("Server: " + message);
        }
    }

    private void log(String message) {
        logArea.append(message + "\n");
    }


}
