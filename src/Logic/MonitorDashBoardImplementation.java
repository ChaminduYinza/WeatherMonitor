/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import Interface.MonitorDashBoardEvents;
import Interface.MonitorLoginInterface;
import Server.Server;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Y`inza
 */
public class MonitorDashBoardImplementation extends UnicastRemoteObject implements MonitorDashBoardEvents {

    String sensorValues;
    private int MonitorCount;

    public MonitorDashBoardImplementation() throws IOException {

    }

    @Override

    //This method is used to read sensor values from sensorValues textfile
    public String getSensorDetails() throws RemoteException {
        String SensorString = "";
        try {

            sensorValues = new String(Files.readAllBytes(Paths.get("sensorValues.txt")), StandardCharsets.UTF_8);
            return SensorString = sensorValues;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return SensorString;
    }

    @Override
    //This method is used to read live sensor names from liveSensors textfile
    public String getLiveSensors() throws RemoteException {
        String liveSensors = "";
        try {
 
            liveSensors = new String(Files.readAllBytes(Paths.get("liveSensors.txt")), StandardCharsets.UTF_8);

        } catch (IOException ex) {
            Logger.getLogger(MonitorDashBoardImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return liveSensors;
    }

    @Override

    public boolean updateLiveSensors(String sensorName) throws RemoteException {

        try {

            String liveSensors = new String(Files.readAllBytes(Paths.get("liveSensors.txt")), StandardCharsets.UTF_8);
            if (!liveSensors.equals(sensorName)) {
                return true;
            } else {
                return false;
            }

        } catch (IOException ex) {
            Logger.getLogger(MonitorDashBoardImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public void updateLiveSensorList() {
        try {
            String liveSensors = new String(Files.readAllBytes(Paths.get("liveSensors.txt")), StandardCharsets.UTF_8);
            FileWriter liveSensorDatabases = new FileWriter("liveSensors.txt", false); //the true will append the new data
            liveSensors = liveSensors + "\nCHANGED";
            liveSensorDatabases.write(liveSensors);
            liveSensorDatabases.close();
        } catch (IOException ex) {
            Logger.getLogger(MonitorDashBoardImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int getConnectedMonitors() throws RemoteException {
       return MonitorCount;
    }

    @Override
    public void setMonitorCount() throws RemoteException {
        MonitorCount++;
    }

    @Override
    public void setDisconnectMonitorCount() throws RemoteException {
         MonitorCount--;
    }
}
