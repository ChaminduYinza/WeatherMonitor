/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Y`inza
 */
//Interface Class of  MonitorUpdateLiveSensor
public interface MonitorUpdateLiveSensor extends Remote {

    //
    public String setAlert() throws RemoteException;
}
