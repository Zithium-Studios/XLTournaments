/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.storage.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.zithium.tournaments.XLTournamentsPlugin;
import net.zithium.tournaments.storage.StorageHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.*;

public class MySQLHandler implements StorageHandler {

    private HikariDataSource hikari;

    @Override
    public boolean onEnable(XLTournamentsPlugin plugin) {
        FileConfiguration config = plugin.getConfig();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://%host%:%port%/%database%?useSSL=%ssl%"
                .replace("%host%", config.getString("storage.mysql.host"))
                .replace("%port%", String.valueOf(config.getInt("storage.mysql.port")))
                .replace("%database%", config.getString("storage.mysql.database"))
                .replace("%ssl%", String.valueOf(config.getBoolean("storage.mysql.use_ssl"))));

        hikariConfig.setUsername(config.getString("storage.mysql.username"));
        hikariConfig.setPassword(config.getString("storage.mysql.password"));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikari = new HikariDataSource(hikariConfig);

        createQueueTable();
        return true;
    }

    @Override
    public void onDisable() {
        if (hikari != null) hikari.close();
    }

    @Override
    public void createQueueTable() {
        try (Connection connection = hikari.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS `action_queue` (uuid varchar(255) NOT NULL, action varchar(255));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTournamentTable(String identifier) {
        try (Connection connection = hikari.getConnection();
             Statement statement = connection.createStatement()) {
             statement.execute("CREATE TABLE IF NOT EXISTS `" + identifier + "` (uuid varchar(255) NOT NULL PRIMARY KEY, score decimal NOT NULL);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addParticipant(String identifier, UUID uuid) {
        try (Connection connection = hikari.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            try (PreparedStatement selectStmt = connection.prepareStatement(
                    "SELECT * FROM `" + identifier + "` WHERE uuid = ? FOR UPDATE")) {
                selectStmt.setString(1, uuid.toString());
                ResultSet rs = selectStmt.executeQuery();

                if (!rs.next()) {
                    try (PreparedStatement insertStmt = connection.prepareStatement(
                            "REPLACE INTO `" + identifier + "` (uuid, score) VALUES (?, 0)")) {
                        insertStmt.setString(1, uuid.toString());
                        insertStmt.executeUpdate();
                    }
                }

                connection.commit(); // Commit transaction
            } catch (SQLException e) {
                connection.rollback(); // Rollback on error
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updateParticipant(String identifier, UUID uuid, int score) {
        try (Connection connection = hikari.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            try (PreparedStatement selectStmt = connection.prepareStatement(
                    "SELECT * FROM `" + identifier + "` WHERE uuid = ? FOR UPDATE")) {
                selectStmt.setString(1, uuid.toString());
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    try (PreparedStatement updateStmt = connection.prepareStatement(
                            "UPDATE `" + identifier + "` SET score = ? WHERE uuid = ?")) {
                        updateStmt.setInt(1, score);
                        updateStmt.setString(2, uuid.toString());
                        updateStmt.executeUpdate();
                    }
                }

                connection.commit(); // Commit transaction
            } catch (SQLException e) {
                connection.rollback(); // Rollback on error
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void clearParticipants(String identifier) {
        try (Connection connection = hikari.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM `" + identifier + "`;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearParticipant(String identifier, UUID uuid) {
        try (Connection connection = hikari.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM `" + identifier + "` WHERE uuid='" + uuid.toString() + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getPlayerQueueActions(String uuid) {
        try (Connection connection = hikari.getConnection();
             Statement statement = connection.createStatement()) {
            List<String> actions = new ArrayList<>();
            ResultSet rs = statement.executeQuery("SELECT action FROM action_queue WHERE uuid='" + uuid + "';");
            while (rs.next()) {
                actions.add(rs.getString("action"));
            }
            return actions;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public void addActionToQueue(String uuid, String action) {
        try (Connection connection = hikari.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO action_queue (uuid, action) VALUES('" + uuid + "','" + action + "');");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeQueueActions(String uuid) {
        try (Connection connection = hikari.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM action_queue WHERE uuid='" + uuid + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<UUID, Integer> getTopPlayers(String identifier) {
        try (Connection connection = hikari.getConnection();
             Statement statement = connection.createStatement()) {
            Map<UUID, Integer> players = new LinkedHashMap<>();
            ResultSet rs = statement.executeQuery("SELECT * FROM `" + identifier + "` ORDER BY score DESC");
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                int score = rs.getInt("score");
                players.put(uuid, score);
            }
            return players;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<UUID, Integer> getTopPlayersByScore(String identifier, int score) {
        try (Connection connection = hikari.getConnection();
             Statement statement = connection.createStatement()) {
            Map<UUID, Integer> players = new LinkedHashMap<>();
            ResultSet rs = statement.executeQuery("SELECT uuid,score FROM `" + identifier + "` WHERE score>=" + score + ";");
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                int s = rs.getInt("score");
                players.put(uuid, s);
            }
            return players;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getPlayerScore(String identifier, String uuid) {
        try (Connection connection = hikari.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT score FROM `" + identifier + "` WHERE uuid='" + uuid + "';");
            if (rs.next()) {
                return rs.getInt("score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void setPlayerScore(String identifier, String uuid, int score) {
        try (Connection connection = hikari.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            try (PreparedStatement selectStmt = connection.prepareStatement(
                    "SELECT * FROM `" + identifier + "` WHERE uuid = ? FOR UPDATE")) {
                selectStmt.setString(1, uuid);
                ResultSet rs = selectStmt.executeQuery();

                try (PreparedStatement replaceStmt = connection.prepareStatement(
                        "REPLACE INTO `" + identifier + "` (uuid, score) VALUES (?, ?)")) {
                    replaceStmt.setString(1, uuid);
                    replaceStmt.setInt(2, score);
                    replaceStmt.executeUpdate();
                }

                connection.commit(); // Commit transaction
            } catch (SQLException e) {
                connection.rollback(); // Rollback on error
                Bukkit.getServer().getLogger().severe("There was an error while attempting to set the player score. Rolling back.");
            }
        } catch (SQLException e) {
            Bukkit.getServer().getLogger().severe("There was an error while attempting to execute the setPlayerScore SQL statement.");
        }
    }


}
