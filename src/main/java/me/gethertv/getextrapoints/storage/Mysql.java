package me.gethertv.getextrapoints.storage;
import me.gethertv.getextrapoints.GetExtraPoints;
import me.gethertv.getextrapoints.data.FindOneCallback;
import me.gethertv.getextrapoints.data.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

public class Mysql {
    private String host;
    private String username;
    private String password;
    private String database;
    private String port;
    private boolean ssl;
    private boolean isFinished;
    private Connection connection;

    public Mysql(String host, String username, String password, String database, String port, boolean ssl) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;
        this.port = port;
        this.ssl = ssl;

        openConnection();

        createTable();
    }

    private String getUsername() {
        return this.username;
    }

    private String getPassword() {
        return this.password;
    }

    private String getHost() {
        return this.host;
    }

    private String getPort() {
        return this.port;
    }

    private String getDatabase() {
        return this.database;
    }

    private boolean useSSL() {
        return this.ssl;
    }

    public boolean isConnected() {
        return (getConnection() != null);
    }

    public Connection getConnection() {
        validateConnection();
        return this.connection;
    }

    private void openConnection() {
        try {
            long l1 = System.currentTimeMillis();
            long l2 = 0L;
            //Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", getUsername());
            properties.setProperty("password", getPassword());
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("useSSL", String.valueOf(useSSL()));
            properties.setProperty("requireSSL", String.valueOf(useSSL()));
            properties.setProperty("verifyServerCertificate", "false");
            String str = "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDatabase();
            this.connection = DriverManager.getConnection(str, properties);
            l2 = System.currentTimeMillis();
            this.isFinished = true;
            System.out.println("[mysql] Connected successfully");
        } catch (ClassNotFoundException classNotFoundException) {
            this.isFinished = false;
            System.out.println("[mysql] Check your configuration.");
            Bukkit.getPluginManager().disablePlugin(GetExtraPoints.getInstance());
        } catch (SQLException sQLException) {
            this.isFinished = false;
            System.out.println("[mysql] (" + sQLException.getLocalizedMessage() + "). Check your configuration.");
            Bukkit.getPluginManager().disablePlugin(GetExtraPoints.getInstance());
        }
    }

    private void validateConnection() {
        if (!this.isFinished)
            return;
        try {
            if (this.connection == null) {
                System.out.println("[mysql] aborted. Connecting again");
                reConnect();
            }
            if (!this.connection.isValid(4)) {
                System.out.println("[mysql] timeout.");
                reConnect();
            }
            if (this.connection.isClosed()) {
                System.out.println("[mysql] closed. Connecting again");
                reConnect();
            }
        } catch (Exception exception) {
        }
    }

    private void reConnect() {
        System.out.println("[mysql] connection again");
        openConnection();
    }

    public void closeConnection() {
        if (getConnection() != null) {
            try {
                getConnection().close();
                System.out.println("[mysql] connection closed");
            } catch (SQLException sQLException) {
                System.out.println("[mysql] error when try close connection");
            }
        }
    }

    public int checkExists(String str) {
        int i = 0;
        try {
            ResultSet resultSet = getResult(str);
            if (resultSet.next()) {
                i++;
                resultSet.close();
                return i;
            }
        } catch (SQLException sQLException) {
            return i;
        }
        return i;
    }


    public void update(String paramString) {
        try {
            Connection connection = getConnection();
            if (connection != null) {
                Statement statement = getConnection().createStatement();
                statement.executeUpdate(paramString);
            }
        } catch (SQLException sQLException) {
            System.out.println("[mysql] wrong update : '" + paramString + "'!");
        }
    }

    public void createUser(Player player)
    {
        update("INSERT INTO extrapoints (uuid, name) VALUES ('"+player.getUniqueId()+"', '"+player.getName()+"')");
    }

    public void loadPlayer(Player player)
    {
        if(!playerExists(player.getUniqueId())) {
            createUser(player);
            GetExtraPoints.getInstance().getUserData().put(player.getUniqueId(), new User(player, 0.00));
            return;
        }

        String str = "SELECT * FROM extrapoints WHERE uuid = '"+player.getUniqueId()+"'";
        try {
            ResultSet resultSet = getResult(str);

            while (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                GetExtraPoints.getInstance().getUserData().put(player.getUniqueId(), new User(player, balance));
            }

        } catch (SQLException | NullPointerException sQLException) {
        }

    }

    public void addOfflineUser(String username, double balance, final FindOneCallback callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(GetExtraPoints.getInstance(), new Runnable() {
            @Override
            public void run() {
                if(playerExists(username))
                {
                    String update = "UPDATE extrapoints SET `balance` = balance+'"+balance+"' WHERE name = '"+username+"'";
                    update(update);
                    Bukkit.getScheduler().runTask(GetExtraPoints.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            callback.onQueryDone(true);
                        }
                    });
                }

                Bukkit.getScheduler().runTask(GetExtraPoints.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(false);
                    }
                });
            }
        });
    }
    public void setOfflineUser(String username, double balance, FindOneCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(GetExtraPoints.getInstance(), new Runnable() {
            @Override
            public void run() {
                if(playerExists(username))
                {
                    String update = "UPDATE extrapoints SET `balance` = '"+balance+"' WHERE name = '"+username+"'";
                    update(update);
                    Bukkit.getScheduler().runTask(GetExtraPoints.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            callback.onQueryDone(true);
                        }
                    });
                }

                Bukkit.getScheduler().runTask(GetExtraPoints.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(false);
                    }
                });
            }
        });
    }


    public void updatePlayer(Player player)
    {
        User user = GetExtraPoints.getInstance().getUserData().get(player.getUniqueId());
        String update = "UPDATE extrapoints SET `balance` = '"+user.getBalance()+"' WHERE uuid = '"+player.getUniqueId()+"'";
        update(update);
    }

    public void leavePlayer(Player player) {
        updatePlayer(player);
        GetExtraPoints.getInstance().getUserData().remove(player.getUniqueId());
    }

    public ResultSet getResult(String paramString) {
        ResultSet resultSet = null;
        Connection connection = getConnection();
        try {
            if (connection != null) {
                Statement statement = getConnection().createStatement();
                resultSet = statement.executeQuery(paramString);
            }
        } catch (SQLException sQLException) {
            System.out.println("[mysql] wrong when want get result: '" + paramString + "'!");
        }
        return resultSet;
    }


    public void createTable() {
        String create = "CREATE TABLE IF NOT EXISTS extrapoints (id INT(10) AUTO_INCREMENT, PRIMARY KEY (id),uuid VARCHAR(100), name VARCHAR(100), balance DOUBLE(11, 2) NOT NULL DEFAULT '0.00')";
        update(create);
    }

    public boolean playerExists(UUID uuid) {
        return (playerID(uuid) != 0);
    }

    public boolean playerExists(String username) {
        return (playerID(username) != 0);
    }

    private int playerID(UUID uuid) {
        return getInt("id", "SELECT id FROM extrapoints WHERE uuid='" + uuid.toString() + "'");
    }
    private int playerID(String username) {
        return getInt("id", "SELECT id FROM extrapoints WHERE name='" + username + "'");
    }


    private int getInt(String paramString1, String paramString2) {
        try {
            ResultSet resultSet = getResult(paramString2);
            if (resultSet.next()) {
                int i = resultSet.getInt(paramString1);
                resultSet.close();
                return i;
            }
        } catch (SQLException sQLException) {
            return 0;
        }
        return 0;
    }



}
