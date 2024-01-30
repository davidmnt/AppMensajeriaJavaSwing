package FormaSinGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ClientInterfazSinGUI {
    private PrintWriter out;

    public ClientInterfazSinGUI() {

        JFrame ventana = new JFrame("Cliente");

        ventana.setBounds(0,0,400, 450);
        ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ventana.setLayout(new BorderLayout());
        ventana.setVisible(true);

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Enviar");


        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(messageField.getText());
                messageField.setText("");
            }
        });
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        ventana.add(scrollPane, BorderLayout.CENTER);
        ventana.add(inputPanel, BorderLayout.SOUTH);

        connectToServer(chatArea);
    }

    private void sendMessage(String message) {
        out.println(message);
    }

    private void connectToServer(JTextArea chatArea) {
        try {
            Socket socket = new Socket("localhost", 5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        chatArea.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientInterfazSinGUI::new);
    }
}