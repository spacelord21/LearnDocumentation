import java.sql.*;

public class Points {
    static Connection dbConnection;
    private static final String url = "jdbc:mysql://localhost/store";
    private static final String user = "root";
    private static final String password = "2100";
    private int points = 0;

    public static Connection getDbConnection() throws SQLException {
        dbConnection = DriverManager.getConnection(url,user,password);
        return dbConnection;
    }

    public String givePoint(long chatId) {
        String startQuery = "select points from users where chatId=" + chatId + ";";
        String levelUp = "";
        try {
            Statement statement = getDbConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(startQuery);
            while (resultSet.next()) {
                points = resultSet.getInt(1);
            }
            points += 1;
            levelUp = changeUserLevel(chatId);
            String resultQuery = "update users set points=" + points + " where chatId=" + chatId + ";";
            statement.executeUpdate(resultQuery);
            resultSet = statement.executeQuery(startQuery);
            while (resultSet.next()) {
                points = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "+1 очко в копилку. Всего у тебя очков - " + points + "!" + levelUp;
    }

    public boolean giveLevel(int points) {
        return points == 5 || points == 100 || points == 150 || points == 200 || points == 300;
    }

    public String changeUserLevel(long chatId) {
        int level = 0;
        String query = "select level from users where chatId=" + chatId + ";";
        try {
            Statement statement = getDbConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                level = resultSet.getInt(1);
            }
            if(giveLevel(points)) {
                level+=1;
                String finishQuery = "update users set level=" + level + " where chatId=" + chatId + ";";
                statement.executeUpdate(finishQuery);
                return "Поздравляю! Твой уровень увеличился! Текущий уровень: " + level;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getLevel(long chatId) {
        String query = "select points from users where chatId=" + chatId + ";";
        int level = 1;
        try {
            Statement statement = getDbConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                level = resultSet.getInt(1);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return level;
    }

}
