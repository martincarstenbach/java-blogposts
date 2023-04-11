import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleDriver;

public class SQLAccessDemo {
    
    public static String whoAmI() 
        throws SQLException
    {

        String username = "nobody";
        OracleDriver ora = new OracleDriver();

        try (
            Connection conn = ora.defaultConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select user from dual");
        ) {

            while (rs.next()) {
                username = rs.getString("user");
            }
        }

        return username;
    }
}