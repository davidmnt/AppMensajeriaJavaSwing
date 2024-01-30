package FormaConGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteInterfazConGUI {
    private JPanel PanelPrincipal;
    private JTextField fieldMensaje;
    private JButton btn_Enviar;
    private JTextArea logArea;

    private PrintWriter out;

    public static void main(String[] args) {
        JFrame frame = new JFrame("ClienteInterfazConGUI");
        frame.setContentPane(new ClienteInterfazConGUI().PanelPrincipal);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0,0,400,350);
        frame.setVisible(true);
    }

    ClienteInterfazConGUI(){
        btn_Enviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(fieldMensaje.getText());
                fieldMensaje.setText("");
            }
        });

        connectToServer(logArea);
    }


    private void sendMessage(String message) {
        out.println(message);
        logArea.append("You: " + message + "\n");
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
}
