package dao;

import util.ApplProps;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

    public Long findId(String username) {
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Long dstUserId = 0L;
        try
        {
            Class.forName (ApplProps.get("db.driver")).newInstance ();
            String userName = ApplProps.get("db.username");
            String password = ApplProps.get("db.password");
            String url = ApplProps.get("db.url");
            conn = DriverManager.getConnection (url, userName, password);
            statement = conn.createStatement();
            resultSet = statement.executeQuery("SELECT u.id AS user_id FROM users u JOIN events e ON u.id=e.receiver_id WHERE u.name='" + username +"'");
            while (resultSet.next()) {
                dstUserId = resultSet.getLong("user_id");
            }
        }
        catch (Exception ex)
        {
            System.err.println ("Cannot connect to database server");
            ex.printStackTrace();
        }
        finally {
            try {
                resultSet.close();
                statement.close();
                conn.close();
            } catch (Exception ex) {
                System.out.println("Error in connection termination! (getDelayWriters)");
            }
        }
        return dstUserId;
    }

    public String getAllEvents(Long chatId) {
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
                System.out.println ("Error in connection termination! (getAllEvents)");
            }
        }
        return events.toString();
    }

    public boolean addEvent(Long chatId, Map<String, String> parameters) {
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
//            java.sql.Timestamp a = java.sql.Timestamp.valueOf(LocalDateTime.parse(params.get("date")));

            statement.executeUpdate("insert into events (id, event_name, last_update, awakening, sender_id, receiver_id, message) " +
                    "values (" +
                    "(select count(a.id) from events a), " +
                    "'" + parameters.get("messageId") + "'," +
                    "'" + parameters.get("date") + "', " +
                    "'" + parameters.get("date") + "'," +
                    "" + chatId + ", " +
                    "" + findId(parameters.get("dstUsername")) + ", " +
                    "'" + parameters.get("textMessage") + "')" +
                    "");
        }
        catch (Exception ex)
        {
            System.err.println ("Cannot connect to database server");
            ex.printStackTrace();
            return false;
        }
        finally {
            try {
                resultSet.close();
                statement.close();
                conn.close();
            } catch (Exception ex) {
                System.out.println("Error in connection termination! (addEvent)");
            }
        }
        return true;
    }

    public String getEventAwakeningDate(String eventId) {
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String awakeningDate = "";
        try
        {
            Class.forName (ApplProps.get("db.driver")).newInstance ();
            String userName = ApplProps.get("db.username");
            String password = ApplProps.get("db.password");
            String url = ApplProps.get("db.url");
            conn = DriverManager.getConnection (url, userName, password);
            statement = conn.createStatement();
//            java.sql.Timestamp a = java.sql.Timestamp.valueOf(LocalDateTime.parse(params.get("date")));
            resultSet = statement.executeQuery("SELECT e.awakening AS awakening FROM events e WHERE e.event_name='" + eventId + "'");
            while (resultSet.next()) {
                awakeningDate = resultSet.getString("awakening");
            }
        }
        catch (Exception ex)
        {
            System.err.println ("Cannot connect to database server");
            ex.printStackTrace();
        }
        finally {
            try {
                resultSet.close();
                statement.close();
                conn.close();
            } catch (Exception ex) {
                System.out.println("Error in connection termination! (getAwakeningDate)");
            }
        }
        return awakeningDate;
    }

    public Map<String, String> getEvent(String eventId) {
        Map<String, String> event = new HashMap<>();
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
            resultSet = statement.executeQuery("select e.receiver_id as receiver, e.message as message from events e WHERE e.event_name='" + eventId + "'");

            while (resultSet.next()) {
                event.put("receiver_id", resultSet.getString("receiver"));
                event.put("message", resultSet.getString("message"));
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
                System.out.println ("Error in connection termination! (getEvent)");
            }
        }
        return event;
    }

    public boolean updateEvent(Map<String, String> p) {
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
            statement.executeUpdate("UPDATE events e SET e.receiver_id ='" + findId(p.get("dstUsername")) + "', " +
                    "e.awakening='" + p.get("date") + "'," +
                    "e.message = '" + p.get("textMessage") + "' WHERE e.event_name='" + p.get("messageId") + "'");
        }
        catch (Exception ex)
        {
            System.err.println ("Cannot connect to database server");
            ex.printStackTrace();
            return false;
        }
        finally
        {
            try {
                resultSet.close();
                statement.close();
                conn.close();
            } catch (Exception ex) {
                System.out.println ("Error in connection termination! (getEvent)");
            }
        }
        return true;
    }

    public Map<String, String> getScheduleDetails(String messageId) {
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Map<String, String> details = new HashMap<>();
        try
        {
            Class.forName (ApplProps.get("db.driver")).newInstance ();
            String userName = ApplProps.get("db.username");
            String password = ApplProps.get("db.password");
            String url = ApplProps.get("db.url");
            conn = DriverManager.getConnection (url, userName, password);
            statement = conn.createStatement();
            resultSet = statement.executeQuery("select event_name, scheduler_name, trigger_name, trigger_group from event_scheduler e WHERE e.event_name='" + messageId + "'");

            while (resultSet.next()) {
                details.put("scheduler_name", resultSet.getString("scheduler_name"));
                details.put("trigger_name", resultSet.getString("trigger_name"));
                details.put("trigger_group", resultSet.getString("trigger_group"));
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
                System.out.println ("Error in connection termination! (getScheduleDetails)");
            }
        }
        return details;
    }

    public boolean saveSchedulerDetails(String eventId, String schedulerName, String triggerKeyName, String triggerKeyGroup) {
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
//            java.sql.Timestamp a = java.sql.Timestamp.valueOf(LocalDateTime.parse(params.get("date")));

            statement.executeUpdate("insert into event_scheduler (id, event_name, scheduler_name, trigger_name, trigger_group) " +
                    "values (" +
                    "(select count(a.id) from event_scheduler a)," +
                    "'" + eventId + "'," +
                    "'" + schedulerName + "', " +
                    "'" + triggerKeyName + "'," +
                    "'" + triggerKeyGroup + "')");
        }
        catch (Exception ex)
        {
            System.err.println ("Cannot connect to database server");
            ex.printStackTrace();
            return false;
        }
        finally {
            try {
                resultSet.close();
                statement.close();
                conn.close();
            } catch (Exception ex) {
                System.out.println("Error in connection termination! (saveSchedulerDetails)");
            }
        }
        return true;
    }
}
