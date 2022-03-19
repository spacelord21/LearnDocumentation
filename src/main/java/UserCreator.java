import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.*;


public class UserCreator {

    static Connection dbConnection;
    private static final String url = "jdbc:mysql://localhost/store";
    private static final String user = "root";
    private static final String password = "2100";



    public static Connection getDbConnection() throws SQLException {
        dbConnection = DriverManager.getConnection(url,user,password);
        return dbConnection;
    }

    public String createUser(Message message) throws SQLException{
        Long chatId = message.getChatId();
        String userName = message.getFrom().getFirstName();
        String query = "insert into users(chatId,name,level,points)" +
                "values(" + chatId + ",'" + userName + "'," + "1,0);";
        Statement statement = getDbConnection().createStatement();
        if(!checkChatId(chatId)) {
            statement.executeUpdate(query);
            return "Добро пожаловать, "+ userName + "!";
        }
        else {
            query = "select level, points from users where chatId="+chatId+";";
            ResultSet resultSet = statement.executeQuery(query);
            int level = 1;
            int points = 0;
            while(resultSet.next()) {
                level = resultSet.getInt(1);
                points = resultSet.getInt(2);
            }
            return userName + ", ты уже числишься в моих рядах." +
                    "Твой уровень: " + level + ", у тебя " + points + " очков!";
        }
    }

    public boolean checkChatId(Long chatId) throws SQLException{
        String query = "select chatId from users where chatId="+chatId+";";
        Statement statement = getDbConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        if(!resultSet.next()) {
            return false;
        }
        while(resultSet.next()) {
            int existChatId = resultSet.getInt(1);
            if(existChatId == chatId) {
                return false;
            }
        }
        return true;
    }

}
