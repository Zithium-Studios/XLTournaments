/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.storage.impl;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.storage.StorageHandler;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class SQLiteHandler implements StorageHandler {

    private File file;
    private Connection connection;

    @Override
    public boolean onEnable(XLTournamentsPlugin plugin) {
        file = new File(plugin.getDataFolder(), "database.db");
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    plugin.getLogger().info("Created database file: " + file.getAbsolutePath());
                }
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Error while creating database file", ex);
                return false;
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            if (connection != null) {
                connection.close();
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + file);
        } catch (SQLException | ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "Error while establishing database connection", ex);
        }

        createQueueTable();
        return true;
    }


    @Override
    public void onDisable() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            if(connection != null && !connection.isClosed()) {
                return connection;
            } else {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + file);
                return connection;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return connection;
    }

    @Override
    public void createQueueTable() {
        try {
            Connection connection = getConnection();
            String sql = "CREATE TABLE IF NOT EXISTS action_queue (uuid varchar(255) NOT NULL, action varchar(255));";
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void createTournamentTable(String identifier) {
        try {
            Connection connection = getConnection();
            String sql = "CREATE TABLE IF NOT EXISTS '" + identifier + "' (uuid varchar(255) NOT NULL PRIMARY KEY, score decimal NOT NULL);";
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void addParticipant(String identifier, UUID uuid) {
        try {
            Connection connection = getConnection();
            String sql = "INSERT OR IGNORE INTO '" + identifier + "' (uuid, score) VALUES ('" + uuid + "',0);";
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateParticipant(String identifier, UUID uuid, int score) {
        try {
            Connection connection = getConnection();
            String sql = "UPDATE '" + identifier + "' SET score = " + score + " WHERE uuid='" + uuid.toString() + "';";
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void clearParticipants(String identifier) {
        try {
            Connection connection = getConnection();
            String sql = "DELETE FROM '" + identifier + "';";
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void clearParticipant(String identifier, UUID uuid) {
        try {
            Connection connection = getConnection();
            String sql = "DELETE FROM '" + identifier + "' WHERE uuid='" + uuid.toString() + "';";
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<String> getPlayerQueueActions(String uuid) {
        try {
            Connection connection = getConnection();
            List<String> actions = new ArrayList<>();
            ResultSet rs = connection.createStatement().executeQuery("SELECT action FROM 'action_queue' WHERE uuid='" + uuid + "';");
            while (rs.next()) {
                actions.add(rs.getString("action"));
            }
            return actions;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public void addActionToQueue(String uuid, String action) {
        try {
            Connection connection = getConnection();
            String sql = "INSERT INTO action_queue (uuid, action) VALUES('" + uuid + "','" + action + "');";
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void removeQueueActions(String uuid) {
        try {
            Connection connection = getConnection();
            String sql = "DELETE FROM 'action_queue' WHERE uuid='" + uuid + "';";
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Map<UUID, Integer> getTopPlayers(String identifier) {
        try {
            Connection connection = getConnection();
            Map<UUID, Integer> players = new LinkedHashMap<>();
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM '" + identifier + "' ORDER BY score DESC");
            while(rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                int score = rs.getInt("score");
                players.put(uuid, score);
            }
            return players;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<UUID, Integer> getTopPlayersByScore(String identifier, int score) {
        try {
            Connection connection = getConnection();
            Map<UUID, Integer> players = new LinkedHashMap<>();
            ResultSet rs = connection.createStatement().executeQuery("SELECT uuid,score FROM '" + identifier + "' WHERE score>=" + score + ";");
            while(rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                int s = rs.getInt("score");
                players.put(uuid, s);
            }
            return players;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public int getPlayerScore(String identifier, String uuid) {
        try {
            Connection connection = getConnection();
            ResultSet rs = connection.createStatement().executeQuery("SELECT score FROM '" + identifier + "' WHERE uuid='" + uuid + "';");
            if(rs.next()) {
                return rs.getInt("score");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    @Override
    public void setPlayerScore(String identifier, String uuid, int score) {
        try {
            Connection connection = getConnection();
            connection.createStatement().execute("REPLACE INTO '" + identifier + "' (uuid, score) VALUES ('" + uuid + "'," + score + ");");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
