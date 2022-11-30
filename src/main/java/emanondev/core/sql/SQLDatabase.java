package emanondev.core.sql;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLDatabase {
    private Connection con;
    // private final CorePlugin plugin;
    private final SQLType type;
    private final String host;
    private final String user;
    private final String password;
    private final String database;
    private final int port;

    public SQLDatabase(@NotNull SQLType type, @NotNull String host,@NotNull  String user, @NotNull String password, String database, int port) throws ClassNotFoundException, SQLException {
        this.type = type;
        this.host = host;
        this.user = user;
        this.password = password;
        this.database = database;
        this.port = port;
        connect();
    }

    public String getUrl() {
        return type.getUrl(host, port, database);
    }

    public SQLType getType() {
        return type;
    }

    public Connection getConnection() {
        return con;
    }

    public boolean isConnected() throws SQLException {
        if (con != null) {
            try {
                return !con.isClosed();
            } catch (SQLException e) {
                Bukkit.getConsoleSender()
                        .sendMessage(ChatColor.RED + "✗  " + ChatColor.WHITE + type.name() + " Connection:");
                Bukkit.getConsoleSender()
                        .sendMessage(ChatColor.RED + "✗  " + ChatColor.WHITE + "Error: " + e.getMessage());
                throw e;
            }
        }
        return false;
    }

    public boolean update(String command) throws ClassNotFoundException, SQLException {
        if (command == null) {
            return false;
        }
        boolean result = false;

        connect(false);
        try {
            Statement st = getConnection().createStatement();
            st.executeUpdate(command);
            st.close();
            result = true;
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + type.name() + " Update:");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Command: " + command);
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error: " + e.getMessage());
            disconnect(false);
            throw e;
        }
        disconnect(false);
        return result;
    }

    public ResultSet query(String command) throws SQLException, ClassNotFoundException {
        if (command == null) {
            return null;
        }
        connect(false);
        ResultSet rs = null;
        try {
            Statement st = getConnection().createStatement();
            rs = st.executeQuery(command);
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + type.name() + " Query:");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Command: " + command);
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error: " + e.getMessage());
            throw e;
        }
        return rs;
    }

    public void connect() throws ClassNotFoundException, SQLException {
        connect(true);
    }

    public void setConnection() throws ClassNotFoundException, SQLException {
        disconnect(false);

        try {
            con = type.getConnection(host, user, password, database, port);
        } catch (ClassNotFoundException | SQLException e) {
            Bukkit.getConsoleSender().sendMessage(
                    ChatColor.RED + "✗  " + ChatColor.WHITE + type.name() + " Connect Error: " + e.getMessage());
            throw e;
        }

    }

    private void connect(boolean message) throws ClassNotFoundException, SQLException {

        if (isConnected()) {
            if (message) {
                Bukkit.getConsoleSender().sendMessage(
                        ChatColor.RED + "✗  " + ChatColor.WHITE + type.name() + " Connect Error: Already connected");
            }
        } else {
            setConnection();
        }
    }

    public void disconnect() throws SQLException {
        disconnect(true);
    }

    public void disconnect(boolean message) throws SQLException {
        try {
            if (isConnected()) {
                con.close();

                if (message) {
                    Bukkit.getConsoleSender()
                            .sendMessage(ChatColor.GREEN + "✓  " + ChatColor.WHITE + type.name() + " disconnected.");
                }
            } else if (message) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "✗  " + ChatColor.WHITE + type.name()
                        + " Disconnect Error: No existing connection");
            }
        } catch (SQLException e) {
            if (message) {
                Bukkit.getConsoleSender().sendMessage(
                        ChatColor.RED + "✗  " + ChatColor.WHITE + type.name() + " Disconnect Error: " + e.getMessage());
            }
            throw e;
        }
        con = null;
    }

    public void reconnect() throws ClassNotFoundException, SQLException {
        disconnect();
        connect();
    }
}