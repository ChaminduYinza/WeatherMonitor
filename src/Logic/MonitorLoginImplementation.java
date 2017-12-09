/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import Interface.MonitorLoginInterface;
import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Y`inza
 */
public class MonitorLoginImplementation extends UnicastRemoteObject implements MonitorLoginInterface {

    public MonitorLoginImplementation() throws RemoteException {

    }

    @Override
    
    //Thos method is validating credentials of monitoring station with username and password textfile
    public boolean getCredentials(String uName, String Password) throws RemoteException {
        if (uName.length() == 0 || Password.length() == 0) {
            return false;
        }

        File LoginDatabase = new File("login.txt");
        boolean isValid = false;

        try {
            Scanner CredentialDatabase = new Scanner(LoginDatabase);
            CredentialDatabase.useDelimiter(",");

            while (CredentialDatabase.hasNextLine()) {
                CredentialDatabase.nextLine();
                String txtUserName = CredentialDatabase.next();
                String txtPassword = CredentialDatabase.next();

                if ((!(txtPassword == null ? Password == null : txtPassword.equals(Password))) || (!(txtUserName == null ? uName == null : txtUserName.equals(uName)))) {
                } else {
                    isValid = true;
                    break;
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isValid;

    }

}
