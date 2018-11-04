package dao;

import util.ApplProps;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlDialectInspection", "Duplicates"})
public class DelayMessageDao {

    public Set<Integer> getDelayWriters() {
        Set< Integer> delayWriters = new HashSet<>();

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try
        {
            Class.forName (ApplProps.get("db.driver")).newInstance ();
            String userName = ApplProps.get("db.username");
            String password = ApplProps.get("db.password");
            String url = ApplProps.get("db.url");
            conn = DriverManager.getConnection (url, userName, password);
            statement = conn.createStatement();
            resultSet = statement.executeQuery("SELECT u.id AS writers FROM users u JOIN roles r ON u.role_id=r.id WHERE r.name='Administrator' OR r.name='Publisher'");

            while (resultSet.next()) {
                delayWriters.add(resultSet.getInt("writers")); // Записываем в сет результаты поиска в БД
            }
        }
        catch (Exception ex)
        {
            System.err.println ("Cannot connect to database server");
            ex.printStackTrace();
        }
        finally
        {
            try {
                resultSet.close();
                statement.close();
                conn.close();
            } catch (Exception ex) {
                System.out.println ("Error in connection termination!");
            }
        }
        return delayWriters;
    }
    // Пока заглушка
    public Long findId() {
        return 233812673L;
    }

    public String getEvents(Long chatId) {
        StringBuilder events = new StringBuilder("");
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try
        {
            Class.forName (ApplProps.get("db.driver")).newInstance ();
            String userName = ApplProps.get("db.username");
            String password = ApplProps.get("db.password");
            String url = ApplProps.get("db.url");
            conn = DriverManager.getConnection (url, userName, password);
            statement = conn.createStatement();
            resultSet = statement.executeQuery("select e.event_name as event, u.name as receiver, e.awakening as awakening, e.message as message from users u join events e on e.receiver_id=u.id " +
                    "WHERE e.sender_id='" + chatId + "' AND awakening >= curdate()");

            while (resultSet.next()) {
                events
                        .append("Event: ").append(resultSet.getString("event")).append("; ")
                        .append("Receiver: ").append(resultSet.getString("receiver")).append("; ")
                        .append("Awakening: ").append(resultSet.getString("awakening")).append("; ")
                        .append("Message: ").append(resultSet.getString("message")).append("; ")
                        .append("\n");
            }
        }
        catch (Exception ex)
        {
            System.err.println ("Cannot connect to database server");
            ex.printStackTrace();
        }
        finally
        {
            try {
                resultSet.close();
                statement.close();
                conn.close();
            } catch (Exception ex) {
                System.out.println ("Error in connection termination!");
            }
        }
        return events.toString();
    }
}
