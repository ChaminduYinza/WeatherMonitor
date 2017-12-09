/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;
import java.rmi.*;

/**
 *
 * @author Y`inza
 */
//Interface Class of  MonitorDashBoardEvents
public interface MonitorDashBoardEvents extends Remote {
    
    public String getSensorDetails() throws RemoteException;
    
     public String getLiveSensors() throws RemoteException;
     
      public boolean updateLiveSensors(String sensorName) throws RemoteException;
      
      public void updateLiveSensorList()throws RemoteException;
      
      public int getConnectedMonitors()throws RemoteException;
      
      public void setMonitorCount()throws RemoteException;
      
       public void setDisconnectMonitorCount()throws RemoteException;
    
}
