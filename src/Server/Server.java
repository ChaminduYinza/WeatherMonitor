/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Logic.MonitorDashBoardImplementation;
import Logic.MonitorLoginImplementation;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Y`inza
 */
public class Server {

    //Use port number as 9001 for Sensor Class
    private static final int PORT = 9001;

    //This Hashset will be using for add connected sensors
    private static HashSet<String> names = new HashSet<String>();
    //private static HashSet<String> newOnlineUsers = new HashSet<String>();

    //Use this Printwritter in order to communicate
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    private static Map<String, PrintWriter> NamedWriterMap = new HashMap();

   //This is use to add newline in the database
    private static String newLine = System.getProperty("line.separator");
    
    //This file path is used for store sensor values and update them
    private static String filePath = "sensorValues.txt";
    
    //This file path is used for store Connected sensors and update them
    private static String liveSensorFilePath = "liveSensors.txt";
    
    //This hashmap is used for store sensor values
    private static Map SensorValues = new HashMap();
    
    //This hashmap is used for store connected sensor names
    private static Map LiveSensors = new HashMap();

    public static void main(String[] args) {
        try {
            //Creating registry and assign 1099 as portnumber
            Registry myReg = LocateRegistry.createRegistry(1099);
            //Create an instance of MonitorLoginImplementation in order to use the functions inside it
            MonitorLoginImplementation MLI = new MonitorLoginImplementation();            
            //Create an instance of MonitorDashBoardImplementation in order to use the functions inside it
            MonitorDashBoardImplementation MDE = new MonitorDashBoardImplementation();
            //Bind registerd registry with MonitorLogin so this can be used to communicate with MonitorLoginImplementation functions
            myReg.bind("MonitorLogin", MLI);
            
            //Bind registerd registry with MonitorDetails so this can be used to communicate with MonitorDashBoardImplementation functions
            myReg.bind("MonitorDetails", MDE);
            System.out.println("Server is up and runing");
            
            //Clear the database when server is starting fresh
            FileWriter userDatabase = new FileWriter(filePath, false); //the true will append the new data
            userDatabase.write("");
            userDatabase.close();

            //Create server socket with assigned port number
            ServerSocket listener = new ServerSocket(PORT);
            try {
                while (true) {
                    new Handler(listener.accept()).start();

                }
            } finally {
                listener.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    //Handler thread class
    private static class Handler extends Thread {

        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {

                 //Read input stream
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                boolean isValid = false;
                //This loop will run infinite
                while (true) {

                    //This will be used to do Authetincation process
                    out.println("AUTHENTICATION");
                    String UNPW = in.readLine();

                    if ("admin".equals(UNPW.split("-")[0]) && "admin".equals(UNPW.split("-")[1])) {
                        out.println("SUBMITNAME");

                    } else {
                         //If given inputs are false then this will use to set relogin
                        out.println("INVALIDCREDENTIALS");

                    }

                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    //Registering connected sensor and validate exsistance
                    synchronized (names) {
                        if (!names.contains(name)) {

                            names.add(name);
                            NamedWriterMap.put(name, out);
                            LiveSensors.put(name, name + "-");
                            FileWriter liveSensorDatabases = new FileWriter(liveSensorFilePath, false); //the true will append the new data
                            //Update liveSensors textfile with newly connected devices
                            LiveSensors.forEach((KEY, VALUE) -> {
                                try {
                                    liveSensorDatabases.write(VALUE + newLine);
                                } catch (IOException ex) {
                                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
       
                            liveSensorDatabases.close();
                            isValid = true;
                            break;
                        } else {
                            //If given name is invalid then this will be used to show sensor name ui to user
                            out.println("INVALIDSENSOR " + name);
                            writers.add(out);

                        }
                    }
                }
                if (isValid) {
                    out.println("VALIDSENSOR " + name);
                    writers.add(out);
                } //else {
//
//                }

                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    } else {
                        // for (PrintWriter writer : writers) {
                        //System.out.println(input);

                        try {

                            FileWriter userDatabase = new FileWriter(filePath, false); //the true will append the new data
                                
                            //Writing sensor values which are getting from sensors to the SensorValue textfile
                            SensorValues.put(input.split("Sensor ")[0], input.split("Sensor ")[1]);
                            System.out.println(SensorValues);
                            userDatabase.write(SensorValues + newLine);//appends the string to the file
                            userDatabase.close();
                        } catch (IOException ioe) {
                            System.err.println("IOException: " + ioe.getMessage());
                        }

//                            try (Writer WriteToDatabase = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Users\\rpa07\\Desktop\\newText.txt"), StandardCharsets.UTF_8))) {
//                                WriteToDatabase.append(input);
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//                            }
                        // }
                    }

                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                try {
                    if (name != null) {
                        FileWriter liveSensorDatabase = null;
                        try {
                              //Removing sensors from Hashmaps and hashsets
                            names.remove(name);
                            LiveSensors.remove(name);
                            SensorValues.remove(name + " ");
                            //Update sensor value textfile
                            FileWriter sensorValueDatabase = new FileWriter(filePath, false); //the true will append the new data
                            if (SensorValues.size() == 0) {
                                sensorValueDatabase.write("");
                            } else {
                                
                                sensorValueDatabase.write(SensorValues + newLine);
                            }

                            sensorValueDatabase.close();
                            
                            //update connected sensor text file when sensor is removed/disconnected
                            FileWriter liveSensorDatabases = new FileWriter(liveSensorFilePath, false); //the true will append the new data
                            LiveSensors.forEach((KEY, VALUE) -> {
                                try {
                                    liveSensorDatabases.write(VALUE + newLine);
                                } catch (IOException ex) {
                                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
                            liveSensorDatabases.close();
                            // liveSensorDatabases.write("CHANGED");
                        } finally {
                        }
                    }
                    if (out != null) {
                        writers.remove(out);
                    }
                    System.out.println("Client Disconnected");
//                try {
                    //Removing sensors from Hashmaps and hashsets
                    names.remove(name);
                    LiveSensors.remove(name);
                    SensorValues.remove(name + " ");
                    //Update sensor value textfile
                    FileWriter sensorValueDatabase = new FileWriter(filePath, false); //the true will append the new data
                    if (SensorValues.size() == 0) {
                        sensorValueDatabase.write("");
                    } else {
                        sensorValueDatabase.write(SensorValues + newLine);
                    }
                    sensorValueDatabase.close();
                    //update connected sensor text file when sensor is removed/disconnected
                    FileWriter liveSensorDatabases = new FileWriter(liveSensorFilePath, false); //the true will append the new data
                    LiveSensors.forEach((KEY, VALUE) -> {
                        try {
                            liveSensorDatabases.write(VALUE + newLine);
                        } catch (IOException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });

                    liveSensorDatabases.close();

                    socket.close();
//
                } catch (IOException e) {

                }
            }
        }
    }

}
