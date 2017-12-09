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
//Interface Class of  MonitorLoginInterface
public interface MonitorLoginInterface extends Remote {

    public boolean getCredentials(String uName, String Password) throws RemoteException;
}
