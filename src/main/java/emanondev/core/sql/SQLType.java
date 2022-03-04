package emanondev.core.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum SQLType {
    MYSQL("jdbc:mysql://", "com.mysql.jdbc.Driver"),
    MARIADB("jdbc:mariadb://", "org.mariadb.jdbc.Driver"),
    POSTGREESQL("jdbc:postgresql://", "org.postgresql.Driver");

    public final String PATH;
    public final String DRIVER;

    SQLType(String path, String driver) {
        this.PATH = path;
        this.DRIVER = driver;
    }

    public String getUrl(String host, int port, String database) {
        return PATH + host + ":" + port + "/" + database;
    }

    public Connection getConnection(String host, String user, String password, String database, int port) throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER);
        return DriverManager.getConnection(getUrl(host, port, database), user, password);
    }
}
