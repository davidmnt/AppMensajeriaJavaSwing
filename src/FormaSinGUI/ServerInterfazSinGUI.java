package FormaSinGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerInterfazSinGUI {
    private JTextArea logArea;
    private List<PrintWriter> clientWriters;

    public ServerInterfazSinGUI() {
        JFrame ventana = new JFrame("Servidor");
        ventana.setBounds(0,0,400, 300);
        ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ventana.setLayout(new BorderLayout());
        ventana.setVisible(true);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Enviar");

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessageToAllClients(messageField.getText());
                messageField.setText("");
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        ventana.add(scrollPane, BorderLayout.CENTER);
        ventana.add(inputPanel, BorderLayout.SOUTH);

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerInterfazSinGUI::new);
    }
}