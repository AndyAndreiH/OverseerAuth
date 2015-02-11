package io.github.andyandreih.overseerauth;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;

import javax.xml.transform.Result;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class DatabaseController {
    OverseerAuth mainClass = null;

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public DatabaseController()
    {
        useRemote = false;
    }

    public DatabaseController(boolean useRemoteDb)
    {
        useRemote = useRemoteDb;
    }

    private String dataPath;
    private boolean useRemote;

    private Database dbRemote;
    private Database dbLocal;

    public void initDb(String dataPath) {
        dbRemote = new MySQL(Logger.getLogger("Minecraft"),
                "auth",
                "localhost", 3306, "minecraft",
                "root", "");
        dbLocal = new SQLite(Logger.getLogger("Minecraft"),
                "auth",
                dataPath,
                "OverseerAuth");
    }

    public boolean openDb() {
        if(useRemote)
            return (dbLocal.open() && dbRemote.open());
        else
            return dbLocal.open();
    }

    public boolean closeDb() {
        if(useRemote)
            return (dbLocal.close() && dbRemote.close());
        else
            return dbLocal.close();
    }

    public void generateTable() {
        if(!dbLocal.isTable("users")) {
            try {
                dbLocal.query("CREATE TABLE users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "uuid VARCHAR(60) UNIQUE," +
                        "joinDate DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "userName VARCHAR(30) UNIQUE," +
                        "password VARCHAR(100)," +
                        "salt VARCHAR(100));");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertUser(String userName, String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(userName));
        Map<String, UUID> userUUID = null;
        try {
            userUUID = fetcher.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SecureRandom saltGen = new SecureRandom();
        int salt = saltGen.nextInt();
        String processedPass = password + ":" + salt;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] passBytes = processedPass.getBytes("UTF-8");
        byte[] securePassBytes = md.digest(passBytes);
        String securePass = bytesToHex(securePassBytes);
        try {
            dbLocal.query("INSERT INTO users (uuid,userName,password,salt) VALUES ('" +
                userUUID.get(userName) +
                "','" +
                userName +
                "','" +
                securePass +
                "','" +
                salt +
                "');");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getUser(String uuid) {
        Map<String, String> userData = new HashMap<String, String>();
        try {
            ResultSet result = dbLocal.query("SELECT * FROM users WHERE uuid=\"" + uuid + "\"");
            while(result.next()) {
                userData.put("id", String.valueOf(result.getInt("id")));
                userData.put("uuid", result.getString("uuid"));
                userData.put("joinDate", result.getDate("joinDate").toString());
                userData.put("userName", result.getString("userName"));
                userData.put("password", result.getString("password"));
                userData.put("salt", result.getString("salt"));
            }
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userData;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
