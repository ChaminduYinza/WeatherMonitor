/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Y`inza
 */
public class SensorClass {

    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Sensor");

    public SensorClass() {

        frame.pack();

    }
    //This will be used to get user inputs for password and username
    JTextField userName = new JTextField();
    JTextField Password = new JTextField();
    Object[] message = {
        "Username :", userName,
        "Password :", Password};

    //This class will be used to generate sensor value every 5 minute and output values to the server every one hour time
    public void GenerateSensorValue(String Name) throws InterruptedException {

        double randomSensorNumber = 0;
        //Formatting created number into two decimal points
        NumberFormat formatter = new DecimalFormat("#0.00");
        int count = 0;
        randomSensorNumber = (Math.random() * 100 + 1);
        out.println(Name + " Sensor : " + formatter.format(randomSensorNumber));

        while (true) {
            try {
                randomSensorNumber = (Math.random() * 100 + 1);

                count++;

                if (count == 12) {
                    out.println(Name + " Sensor : " + formatter.format(randomSensorNumber));
                    count = 0;
                    System.out.println(formatter.format(randomSensorNumber));
                    System.out.println(Name);

                }
                //Sleeping thread for 5 minute of time
                Thread.sleep(300000);

            } catch (InterruptedException ex) {
                Logger.getLogger(SensorClass.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

//    private String getSensorLocation() {
//        return JOptionPane.showInputDialog(
//                frame,
//                "Enter Sensor Location:",
//                "Welcome to the Sensor Registration",
//                JOptionPane.QUESTION_MESSAGE);
//    }
    //Swing containt for let user to input username and password
    private String getUsernameAndPassword() {

        int option = JOptionPane.showConfirmDialog(frame, message, "Enter Login Credentials", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String UN = userName.getText();
            String PW = Password.getText();
            return UN + "-" + PW;

        }

        return null;
    }

    //Swing containt for let user to input server address
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
                frame,
                "Enter IP Address of the Server:",
                "Welcome to the Sensor Registration",
                JOptionPane.QUESTION_MESSAGE);
    }

    //Swing containt for let user to input sensor name
    private String getName() {
        return JOptionPane.showInputDialog(
                frame,
                "Choose a sensor name:",
                "Sensor name selection",
                JOptionPane.PLAIN_MESSAGE);
    }

    private void run() throws IOException, InterruptedException {

        String serverAddress = getServerAddress();
        Socket socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        boolean Accepted = false;
        boolean validCredentials = false;
        String SensorName = "";
        String SensorLocation = "";
        while (true) {

            //Listning to the server
            String line = in.readLine();
            if (line.startsWith("SUBMITNAME")) {
                SensorName = getName();
                //SensorLocation = getSensorLocation();
                //Writes SensorName in to server
                out.println(SensorName);
            } else if (line.startsWith("AUTHENTICATION")) {
                String UNPW = getUsernameAndPassword();
                //Writes username and password in to server
                out.println(UNPW);
            } else if (line.startsWith("INVALIDSENSOR")) {
                JOptionPane.showMessageDialog(frame, "Invalid Sensor Name");

            } else if (line.startsWith("VALIDSENSOR")) {
                Accepted = true;

            } else if (line.startsWith("INVALIDCREDENTIALS")) {
                JOptionPane.showMessageDialog(frame, "Invalid Username or Password");

            }
            if (Accepted == true) {
                GenerateSensorValue(SensorName);
            }

        }
    }

    public static void main(String[] args) throws Exception {
        SensorClass client = new SensorClass();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.run();
    }

}
