package net.zithium.tournaments.storage.impl;

import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.storage.StorageHandler;
import org.bukkit.Bukkit;

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
            Bukkit.getServer().getLogger().log(Level.SEVERE, "There was an error while attempting to shut down the SQLite database.", ex);
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
        String sql = "CREATE TABLE IF NOT EXISTS action_queue (uuid varchar(255) NOT NULL, action varchar(255));";
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void createTournamentTable(String identifier) {
        String sql = "CREATE TABLE IF NOT EXISTS ? (uuid varchar(255) NOT NULL PRIMARY KEY, score decimal NOT NULL);";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, identifier);
            stmt.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void addParticipant(String identifier, UUID uuid) {
        String sql = "INSERT OR IGNORE INTO '" + identifier + "' (uuid, score) VALUES (?, 0);";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void updateParticipant(String identifier, UUID uuid, int score) {
        String sql = "UPDATE '" + identifier + "' SET score = ? WHERE uuid = ?;";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, score);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
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
        String sql = "SELECT action FROM 'action_queue' WHERE uuid='" + uuid + "';";
        List<String> actions = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                actions.add(rs.getString("action"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return actions;
    }


    @Override
    public void addActionToQueue(String uuid, String action) {
        String sql = "INSERT INTO action_queue (uuid, action) VALUES(?, ?);";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            stmt.setString(2, action);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void removeQueueActions(String uuid) {
        String sql = "DELETE FROM action_queue WHERE uuid = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, uuid);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public Map<UUID, Integer> getTopPlayers(String identifier) {
        String sql = "SELECT uuid, score FROM '" + identifier + "' ORDER BY score DESC";
        Map<UUID, Integer> players = new LinkedHashMap<>();

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                int score = rs.getInt("score");
                players.put(uuid, score);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return players;
    }


    @Override
    public Map<UUID, Integer> getTopPlayersByScore(String identifier, int score) {
        String sql = "SELECT uuid, score FROM '" + identifier + "' WHERE score >= ?;";
        Map<UUID, Integer> players = new LinkedHashMap<>();

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, score);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    int s = rs.getInt("score");
                    players.put(uuid, s);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return players;
    }


    @Override
    public int getPlayerScore(String identifier, String uuid) {
        String sql = "SELECT score FROM '" + identifier + "' WHERE uuid = ?;";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("score");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }


    @Override
    public void setPlayerScore(String identifier, String uuid, int score) {
        String sql = "REPLACE INTO '" + identifier + "' (uuid, score) VALUES (?, ?);";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            stmt.setInt(2, score);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


}
