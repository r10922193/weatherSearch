/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weathersearch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

public class client {

    //GUI conponent
    private JButton button;
    private JTextArea textArea1, textArea2;
    private JLabel label, label2;
    private JList placeJList; // list to display colors
    private final String places[] = {"taipei-city", "keelung-city", "new-taipei-city",
        "taoyuan-city", "hsinchu-city", "miaoli-county", "taichung-city", "nantou-county",
        "changhua-county", "yunlin-county", "chiayi-city", "tainan-city", "kaohsiung-city",
        "pingtung-county", "taitung-county", "hualien-county", "yilan-county", "penghu-county",
        "kinmen-county", "lienchiang-county"};
    //socket parameter
    String serverName;
    int port;
    Socket socket;
    ObjectInputStream input;
    ObjectOutputStream output;

    //constructor
    public client(String name, int p) {
        //GUI setting
        JFrame frame = new JFrame("Weather Search");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 400);

        placeJList = new JList(places);
        placeJList.setVisibleRowCount(5); // display five rows at once
        placeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        placeJList.setSelectedIndex(0);

        button = new JButton("Search");

        textArea1 = new JTextArea();
        textArea1.setBackground(Color.getHSBColor(36, 19, 80));
        textArea2 = new JTextArea();
        textArea2.setBackground(Color.PINK);
        label = new JLabel("");
        label2 = new JLabel("Please select a place:");

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel panel2 = new JPanel();
        panel.setBackground(Color.LIGHT_GRAY);
        panel2.setBackground(Color.LIGHT_GRAY);
        //JScrollPane panel3 = new JScrollPane(textArea);
        JScrollPane panel4 = new JScrollPane(placeJList);

        JTabbedPane tabbedPane = new JTabbedPane();
        JScrollPane panel3 = new JScrollPane(textArea1); // create second panel
        tabbedPane.addTab("daily Weather", null, panel3, "Weather of the week");
        JScrollPane panel5 = new JScrollPane(textArea2); // create second panel
        panel5.setBackground(Color.CYAN); // set background to yellow
        tabbedPane.addTab("Current Wearther", null, panel5, "Real time");

        panel2.add(label2);
        panel2.add(panel4);
        panel2.add(button);
        panel2.add(label);

        panel.add(panel2, "North");
        panel.add(tabbedPane, "Center");
        frame.add(panel);
        frame.setVisible(true);
        //set server name and port
        serverName = name;
        port = p;
        //create client socket
        try {
            socket = new Socket(InetAddress.getByName(serverName), port);
            System.out.println("create client socket successfully");
            //output Stream to send data
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            //input Stream to get data
            input = new ObjectInputStream(socket.getInputStream());
        } catch (UnknownHostException x) {
            x.printStackTrace();
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    //method to execute connection
    public void execute() {
        System.out.println("execution...");
        //if click search button
        button.addActionListener((ActionEvent e) -> {
            if (button.isEnabled()) {
                button.setEnabled(false);
                new Thread() {
                    public void run() {
                        label.setText("searching...");
                        try {
                            //get user input place
                            int command = placeJList.getSelectedIndex();
                            //send data to server
                            output.writeObject(command);
                            output.flush();
                            //get data from server
                            String message = (String) input.readObject();
                            textArea1.setText(message);
                            String message2 = (String) input.readObject();
                            textArea2.setText(message2);
                        } catch (IOException ex) {
                            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        button.setEnabled(true);
                        label.setText("");
                    }
                }.start();
            }
        });
    }

    public static void main(String args[]) {
        //set server name and port
        client client = new client("127.0.0.1", 5001);
        //execute
        client.execute();
    }
}

