package io.github.andyandreih.overseerauth;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;

import java.sql.*;
import java.util.logging.Logger;

public class DatabaseController
{
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

    public void initDb(String dataPath)
    {
        dbRemote = new MySQL(Logger.getLogger("Minecraft"),
                "auth",
                "localhost", 3306, "minecraft",
                "root", "");
        dbLocal = new SQLite(Logger.getLogger("Minecraft"),
                "auth",
                dataPath,
                "OverseerAuth");
    }

    public boolean openDb()
    {
        if(useRemote)
            return (dbLocal.open() && dbRemote.open());
        else
            return dbLocal.open();
    }

    public boolean closeDb()
    {
        if(useRemote)
            return (dbLocal.close() && dbRemote.close());
        else
            return dbLocal.close();
    }

    public void generateTable()
    {
        if(!dbLocal.isTable("users"))
        {
            try
            {
                dbLocal.query("CREATE TABLE users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "uuid VARCHAR(60) UNIQUE," +
                        "joinDate DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "displayName VARCHAR(30) UNIQUE);");
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void insertUser(String uuid, String displayName)
    {
        try
        {
            dbLocal.query("INSERT INTO users (uuid,displayName) VALUES ('" +
                uuid +
                "','" +
                displayName +
                "')");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public ResultSet getUser(String uuid)
    {
        ResultSet result = null;
        try
        {
            result = dbLocal.query("SELECT * FROM users WHERE uuid='" + uuid + "'");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
}
