package com.highway.iot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class BackupH2 {
    public static void main(String[] args) {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:file:./data/iot_simulator_db", "sa", "password");
            Statement stat = conn.createStatement();
            stat.execute("SCRIPT TO '../iot_simulator_db_backup.sql'");
            System.out.println("Backup successful to iot_simulator_db_backup.sql");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
