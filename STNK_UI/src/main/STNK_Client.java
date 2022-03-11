/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.*;
import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Elvansen
 */

class myThread_Client extends Thread {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/stnk";
    static final String USER = "root";
    static final String PASS = "";

    static Connection conn;
    static Statement stmt;
    static ResultSet rs;
    static InputStreamReader inputStreamReader = new InputStreamReader(System.in);
    static BufferedReader input = new BufferedReader(inputStreamReader);

    InetAddress IP;
    int PORT = 6789;
    BufferedReader InFromServer;
    DataOutputStream OutToServer;
    String terima;

    myThread_Client(Socket soc) {
        Socket sc = soc;
        try {
            //soc.setSoTimeout(10000);
            OutToServer = new DataOutputStream(sc.getOutputStream());
            InFromServer = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            Class.forName(JDBC_DRIVER);
            
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public void connect(Socket soc){
        Socket sc = soc;
        try {
            //soc.setSoTimeout(10000);
            OutToServer = new DataOutputStream(sc.getOutputStream());
            InFromServer = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            System.out.println(ex);
        }
    }

    public String[] login(String username, String password) {
        String outServer = null;
        outServer = String.format("%s|%s", username, password);

        String[] inServer = gate(outServer, 1);

        return inServer;
    }

    public String[] signup(String username, String password) {
        String outServer = null;
        outServer = String.format("%s|%s", username, password);

        String[] inServer = gate(outServer, 2);

        return inServer;
    }
    
    public String[] logOut() {
        String outServer = null;

        String[] inServer = gate("LogOut", 4);
        
        return inServer;
    }

    public String[] search(String Nopol, String NIK){
        String[] inServer = new String[50];

        String outServer = null;
        outServer = String.format("%s|%s", Nopol, NIK);

        inServer = gate(outServer, 3);

        
        return inServer;
    }

    public String[] print(String Nopol, String NIK){
        String[] inServer = new String[5];

        String outServer = null;
        outServer = String.format("%s|%s", Nopol, NIK);

        inServer = gate(outServer, 5);
        
        return inServer;
    }
    
    /**
    *
    *
    *
    *
    */
    
    
    private String[] gate(String outServer, int Type) {
        String temp = null;
        String[] inServer = null;

        outServer = bungkus(outServer, Type);

        try {
            OutToServer.writeBytes(outServer + "\n");
            temp = InFromServer.readLine();
        } catch (IOException e) {
            System.err.println(e);
            
        }

        inServer = bongkar(temp);
        
        if(inServer[1].equalsIgnoreCase("030700")){
            inServer = pisah(inServer[2]);
            inServer[3] = "error";
        }else inServer = pisah(inServer[2]);

        return inServer;
    }

    private String[] bongkar(String paket) {
        String[] terbongkar = new String[5];

        terbongkar = paket.split("~#");

        return terbongkar;
    }

    public String[] pisah(String string) {
        String[] hasil = new String[50];
        char[] input = string.toCharArray();
        int wordStart = 0, wordEnd = 0, i = 0;
        while (wordStart < input.length) {
            wordEnd = wordStart;
            while (input[wordEnd] != '|') {
                wordEnd++;
                if (wordEnd == input.length) {
                    break;
                }
            }
            hasil[i] = string.substring(wordStart, wordEnd);
            wordStart = wordEnd + 1;
            i++;
        }
        i = 0;
        while (i < hasil.length) {
            System.out.print(hasil[i] + "\n");
            i++;
        }
        return hasil;
    }

    public String bungkus(String message, int type) {
        String result = null;
        String head = null, tail = null;
        switch (type) {
            case 1:
                head = "0200~#030200~#";
                break;
            case 2:
                head = "0200~#030400~#";
                break;
            case 3:
                head = "0200~#030500~#";
                break;
            case 4:
                head = "0200~#030300~#";
                break;
            case 5:
                head = "0200~#030600~#";
                break;
        }

        try {
            InetAddress ip;

            ip = InetAddress.getLocalHost();
            System.out.println("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            System.out.print("Current MAC address : ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
            }
            System.out.println(sb.toString());

            tail = "~#" + ip.getHostAddress() + "~#" + sb.toString() + "~#";

        } catch (UnknownHostException e) {

            e.printStackTrace();

        } catch (SocketException e) {

            e.printStackTrace();

        }
        if (head == null || tail == null) {
            result = null;
        } else {
            result = head + message + tail;
        }

        return result;
    }

    static void Insert_data(String[] string) {

        String sql_user = "SELECT * FROM user";
        try {
            boolean valid = true;
            rs = stmt.executeQuery(sql_user);

            if (string[0].equals("null")) {
                valid = false;
            }

            while (rs.next()) {
                String id_user = rs.getString("id");

                if (string[0].equals(id_user)) {
                    valid = false;
                }
            }
            if (valid) {
                String id = string[0];
                String nama = string[1];
                String nik = string[2];
                String alamat = string[3];

                String sql = "INSERT INTO user (id, nama,NIK,alamat) VALUE('%s', '%s','%s', '%s')";
                sql = String.format(sql, id, nama, nik, alamat);
                System.out.println("data berhasil Disimpan");

                stmt.execute(sql);
            } else {
                System.out.println("data sudah ada atau tidak ditemukan");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
