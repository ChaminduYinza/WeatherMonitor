/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Interface.MonitorDashBoardEvents;
import java.awt.HeadlessException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Y`inza
 */
public class MonitorDashboard extends javax.swing.JFrame {

    /**
     * Creates new form MonitorDashboard
     */
    public MonitorDashboard() {
        //overing default closing method of jframe
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                //Asking user whether the user wants to exit or not
                if (JOptionPane.showConfirmDialog(null,
                        "Do you want to exit ?", "Exit?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    try {

                        Registry myReg = LocateRegistry.getRegistry("127.0.0.1", 1099);

                        try {
                            //if user click ok button then updating live monitoring count by calling the server
                            MonitorDashBoardEvents MDE = (MonitorDashBoardEvents) myReg.lookup("MonitorDetails");
                            MDE.setDisconnectMonitorCount();
                            System.exit(0);
                        } catch (NotBoundException ex) {
                            Logger.getLogger(MonitorDashboard.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (AccessException ex) {
                            Logger.getLogger(MonitorDashboard.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } catch (RemoteException ex) {
                        Logger.getLogger(MonitorDashboard.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        initComponents();
        t2.start();
        t3.start();

    }

    public void myMethod() {

    }

    //  public class FrameRunnable implements Runnable {
    //  @Override
    //This thread is used to update live sensors into the list
    Thread t3 = new Thread() {
        public void run() {
            DefaultListModel<String> sensorListModel = new DefaultListModel<>();
            try {
                String liveSensors;
                String[] liveSensorsArray;
                //create a remote object registry that accepts address and port
                Registry myReg = LocateRegistry.getRegistry("127.0.0.1", 1099);
                //Create an instance of MonitorDashBoardEvents and lookup for MonitorDetails in Server rmi
                MonitorDashBoardEvents MDE = (MonitorDashBoardEvents) myReg.lookup("MonitorDetails");
                liveSensors = MDE.getLiveSensors();
                MDE.setMonitorCount();

                liveSensorsArray = liveSensors.split("-");
                for (int i = 0; i < liveSensorsArray.length; i++) {
                    sensorListModel.addElement(liveSensorsArray[i]);
                }
                jList1.setModel(sensorListModel);
                while (true) {
                    lblMonitorCount.setText(Integer.toString(MDE.getConnectedMonitors()));

                    if (MDE.updateLiveSensors(liveSensors)) {
                        liveSensors = MDE.getLiveSensors();
                        sensorListModel.removeAllElements();
                        liveSensorsArray = liveSensors.split("-");
                        for (int i = 0; i < liveSensorsArray.length; i++) {
                            sensorListModel.addElement(liveSensorsArray[i]);

                        }
                        //Adding sensor names in to the jlist
                        jList1.setModel(sensorListModel);

                        //Thread.sleep(3000);
                        // }
                        liveSensors = MDE.getLiveSensors();
                    }
                }

            } catch (RemoteException | NotBoundException ex) {
                Logger.getLogger(MonitorDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    };

    //This thread is used to call getSensorDetails() method in server side inorder to update sensor values every one hour
    Thread t2 = new Thread() {
        @Override
        public void run() {

            SimpleDateFormat SysFormat = new SimpleDateFormat("HH:mm:ss");
            String SensorDetailList = "";
            String[] SensorDetailArray;

            try {
                ta_SensorDetails.setText("");
                //create a remote object registry that accepts address and port
                Registry myReg = LocateRegistry.getRegistry("127.0.0.1", 1099);
                //Create an instance of MonitorDashBoardEvents and lookup for MonitorDetails in Server rmi
                MonitorDashBoardEvents MDE = (MonitorDashBoardEvents) myReg.lookup("MonitorDetails");
                SensorDetailList = MDE.getSensorDetails();

                SensorDetailList = SensorDetailList.substring(1, (SensorDetailList.length() - 3));
                SensorDetailArray = SensorDetailList.split(", ");
                String alertString = "";

                //This will loc will be used to set alert and display the output in to textarea
                for (int i = 0; i < SensorDetailArray.length; i++) {
                    if (SensorDetailArray[i].toUpperCase().contains("TEMPERATURE")) {
                        ta_SensorDetails.append(SensorDetailArray[i] + "(Celcius)\n");
                    } else if (SensorDetailArray[i].toUpperCase().contains("RAINFALL")) {
                        ta_SensorDetails.append(SensorDetailArray[i] + "(mm)\n");
                    } else if (SensorDetailArray[i].toUpperCase().contains("HUMIDITY")) {
                        ta_SensorDetails.append(SensorDetailArray[i] + "(%)\n");
                    } else if (SensorDetailArray[i].toUpperCase().contains("AIR PRESSURE")) {
                        ta_SensorDetails.append(SensorDetailArray[i] + "(%)\n");
                    }

                    if (SensorDetailArray[i].toUpperCase().contains("RAINFALL") && Float.parseFloat(SensorDetailArray[i].split("=: ")[1]) > 60) {
                        JOptionPane.showMessageDialog(null, "WARNING SENSOR VALUE IS HIGH\n" + SensorDetailArray[i], "WARNING", JOptionPane.WARNING_MESSAGE);

                    } else if (SensorDetailArray[i].toUpperCase().contains("TEMPERATURE") && (Float.parseFloat(SensorDetailArray[i].split("=: ")[1]) > 35) || Float.parseFloat(SensorDetailArray[i].split("=: ")[1]) < 20) {
                        JOptionPane.showMessageDialog(null, "WARNING SENSOR VALUE IS HIGH\n" + SensorDetailArray[i], "WARNING", JOptionPane.WARNING_MESSAGE);

                    }

                }

                while (true) {
                    Date currentTime = new Date();

                    if ("00".equals(SysFormat.format(currentTime).split(":")[1]) && "00".equals(SysFormat.format(currentTime).split(":")[2])) {
                        System.out.println("WADA WADA");
                        ta_SensorDetails.setText("");
                        //create a remote object registry that accepts address and port
                        //Registry myReg = LocateRegistry.getRegistry("127.0.0.1", 1099);
                        //Create an instance of MonitorDashBoardEvents and lookup for MonitorDetails in Server rmi
                        // MonitorDashBoardEvents MDE = (MonitorDashBoardEvents) myReg.lookup("MonitorDetails");
                        SensorDetailList = MDE.getSensorDetails();

                        SensorDetailList = SensorDetailList.substring(1, (SensorDetailList.length() - 3));
                        SensorDetailArray = SensorDetailList.split(", ");

                        //This will loc will be used to set alert and display the output in to textarea
                        for (int i = 0; i < SensorDetailArray.length; i++) {
                            if (SensorDetailArray[i].toUpperCase().contains("TEMPERATURE")) {
                                ta_SensorDetails.append(SensorDetailArray[i] + "(Celcius)\n");
                            } else if (SensorDetailArray[i].toUpperCase().contains("RAINFALL")) {
                                ta_SensorDetails.append(SensorDetailArray[i] + "(mm)\n");
                            } else if (SensorDetailArray[i].toUpperCase().contains("HUMIDITY")) {
                                ta_SensorDetails.append(SensorDetailArray[i] + "(%)\n");
                            } else if (SensorDetailArray[i].toUpperCase().contains("AIR PRESSURE")) {
                                ta_SensorDetails.append(SensorDetailArray[i] + "(%)\n");
                            }

                            if (SensorDetailArray[i].toUpperCase().contains("RAINFALL") && Float.parseFloat(SensorDetailArray[i].split("=: ")[1]) > 60) {
                                JOptionPane.showMessageDialog(null, "WARNING SENSOR VALUE IS HIGH\n" + SensorDetailArray[i], "WARNING", JOptionPane.WARNING_MESSAGE);

                            } else if (SensorDetailArray[i].toUpperCase().contains("TEMPERATURE") && (Float.parseFloat(SensorDetailArray[i].split("=: ")[1]) > 35) || Float.parseFloat(SensorDetailArray[i].split("=: ")[1]) < 20) {
                                JOptionPane.showMessageDialog(null, "WARNING SENSOR VALUE IS HIGH\n" + SensorDetailArray[i], "WARNING", JOptionPane.WARNING_MESSAGE);

                            }

                        }
                        Thread.sleep(10000 * 5);
                    }

                }
            } catch (RemoteException | NotBoundException | NumberFormatException | HeadlessException | InterruptedException ex) {
            }

        }

    };

    // }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblMonitorCount = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        btn_GetSDetails = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        ta_SensorDetails = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("MONITORING STATION DASHBOARD");
        setResizable(false);
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel1MouseEntered(evt);
            }
        });
        jPanel1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPanel1FocusGained(evt);
            }
        });
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Connected Monitors :");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 430, 140, 30));

        lblMonitorCount.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblMonitorCount.setForeground(new java.awt.Color(255, 51, 51));
        jPanel1.add(lblMonitorCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 425, 30, 40));

        jScrollPane2.setViewportView(jList1);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 70, 160, 340));

        btn_GetSDetails.setText("Get Sensor Details");
        btn_GetSDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GetSDetailsActionPerformed(evt);
            }
        });
        jPanel1.add(btn_GetSDetails, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 420, 260, 42));

        ta_SensorDetails.setColumns(20);
        ta_SensorDetails.setRows(5);
        ta_SensorDetails.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ta_SensorDetailsFocusGained(evt);
            }
        });
        jScrollPane1.setViewportView(ta_SensorDetails);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 260, 340));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/mainDashboard.png"))); // NOI18N
        jLabel1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jLabel1FocusGained(evt);
            }
        });
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_GetSDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GetSDetailsActionPerformed
        ta_SensorDetails.setText("");
        String SensorDetailList = "";
        String[] SensorDetailArray;
        List<String> selected = new ArrayList<String>();
        selected = jList1.getSelectedValuesList();
        String[] SelectedArray = selected.toArray(new String[0]);

        if (!selected.isEmpty()) {
            try {
                Registry myReg = LocateRegistry.getRegistry("127.0.0.1", 1099);
                MonitorDashBoardEvents MDE = (MonitorDashBoardEvents) myReg.lookup("MonitorDetails");
                SensorDetailList = MDE.getSensorDetails();
                SensorDetailList = SensorDetailList.substring(1, (SensorDetailList.length() - 3));
                SensorDetailArray = SensorDetailList.split(", ");

                for (int i = 0; i < SensorDetailArray.length; i++) {

                    for (int r = 0; r < SelectedArray.length; r++) {
                        String SelectedSensorName = SelectedArray[r];
                        if (SelectedSensorName.contains(SensorDetailArray[i].split(" =:")[0])) {
                            if (SensorDetailArray[i].toUpperCase().contains("TEMPERATURE")) {
                                ta_SensorDetails.append(SensorDetailArray[i] + "(Celcius)\n");
                            } else if (SensorDetailArray[i].toUpperCase().contains("RAINFALL")) {
                                ta_SensorDetails.append(SensorDetailArray[i] + "(mm)\n");
                            } else if (SensorDetailArray[i].toUpperCase().contains("HUMIDITY")) {
                                ta_SensorDetails.append(SensorDetailArray[i] + "(%)\n");
                            } else if (SensorDetailArray[i].toUpperCase().contains("AIR PRESSURE")) {
                                ta_SensorDetails.append(SensorDetailArray[i] + "(%)\n");
                            }
                        }

                    }
                }

            } catch (RemoteException | NotBoundException ex) {
                Logger.getLogger(MonitorDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

//        String SensorDetailList = "";
//        String[] SensorDetailArray;
//        try {
//            //        String SensorDetailList = "";
////        try {
////            ta_SensorDetails.setText("");
////            Registry myReg = LocateRegistry.getRegistry("127.0.0.1", 1099);
////            MonitorDashBoardEvents MDE = (MonitorDashBoardEvents) myReg.lookup("MonitorDetails");
////            SensorDetailList = MDE.getSensorDetails();
////            ta_SensorDetails.append(SensorDetailList);
////
////        } catch (Exception ex) {
////            ex.printStackTrace();
////        }
//            //myMethod();
//
//            ta_SensorDetails.setText("");
//            //create a remote object registry that accepts address and port
//            Registry myReg = LocateRegistry.getRegistry("127.0.0.1", 1099);
//            //Create an instance of MonitorDashBoardEvents and lookup for MonitorDetails in Server rmi
//            MonitorDashBoardEvents MDE = (MonitorDashBoardEvents) myReg.lookup("MonitorDetails");
//            SensorDetailList = MDE.getSensorDetails();
//
//            SensorDetailList = SensorDetailList.substring(1, (SensorDetailList.length() - 3));
//            SensorDetailArray = SensorDetailList.split(", ");
//            String alertString = "";
//
//            //This will loc will be used to set alert and display the output in to textarea
//            for (int i = 0; i < SensorDetailArray.length; i++) {
//                if (SensorDetailArray[i].toUpperCase().contains("TEMPERATURE")) {
//                    ta_SensorDetails.append(SensorDetailArray[i] + "(Celcius)\n");
//                } else if (SensorDetailArray[i].toUpperCase().contains("RAINFALL")) {
//                    ta_SensorDetails.append(SensorDetailArray[i] + "(mm)\n");
//                } else if (SensorDetailArray[i].toUpperCase().contains("HUMIDITY")) {
//                    ta_SensorDetails.append(SensorDetailArray[i] + "(%)\n");
//                } else if (SensorDetailArray[i].toUpperCase().contains("AIR PRESSURE")) {
//                    ta_SensorDetails.append(SensorDetailArray[i] + "(%)\n");
//                }
//
//                if (SensorDetailArray[i].toUpperCase().contains("RAINFALL") && Float.parseFloat(SensorDetailArray[i].split("=: ")[1]) > 60) {
//                    JOptionPane.showMessageDialog(null, "WARNING SENSOR VALUE IS HIGH\n" + SensorDetailArray[i], "WARNING", JOptionPane.WARNING_MESSAGE);
//
//                } else if (SensorDetailArray[i].toUpperCase().contains("TEMPERATURE") && (Float.parseFloat(SensorDetailArray[i].split("=: ")[1]) > 35) || Float.parseFloat(SensorDetailArray[i].split("=: ")[1]) < 20) {
//                    JOptionPane.showMessageDialog(null, "WARNING SENSOR VALUE IS HIGH\n" + SensorDetailArray[i], "WARNING", JOptionPane.WARNING_MESSAGE);
//
//                }
//
//            }
//        } catch (RemoteException ex) {
//            Logger.getLogger(MonitorDashboard.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NotBoundException ex) {
//            Logger.getLogger(MonitorDashboard.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }//GEN-LAST:event_btn_GetSDetailsActionPerformed

    private void jPanel1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPanel1FocusGained


    }//GEN-LAST:event_jPanel1FocusGained

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_formFocusGained

    private void jPanel1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseEntered

    }//GEN-LAST:event_jPanel1MouseEntered

    private void ta_SensorDetailsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ta_SensorDetailsFocusGained
        //myMethod();
    }//GEN-LAST:event_ta_SensorDetailsFocusGained

    private void jLabel1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jLabel1FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel1FocusGained

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MonitorDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MonitorDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MonitorDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MonitorDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MonitorDashboard().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_GetSDetails;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblMonitorCount;
    private javax.swing.JTextArea ta_SensorDetails;
    // End of variables declaration//GEN-END:variables
}
