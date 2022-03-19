import java.sql.*;

public class Information {

    static Connection dbConnection;
    private static final String url = "jdbc:mysql://localhost/store";
    private static final String user = "root";
    private static final String password = "2100";

    public static Connection getDbConnection() throws SQLException {
        dbConnection = DriverManager.getConnection(url,user,password);
        return dbConnection;
    }

    public String helpInfo() {
        return "Если есть проблемы или предложения пишите - @spacelord_21";
    }
    public String bestUsers() throws SQLException {
        String query = "select name,level,points from users;";
        Statement statement = getDbConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        int i = 1;
        StringBuilder tableWithResults = new StringBuilder();
        while(resultSet.next()) {
            String name = resultSet.getString(1);
            int level = resultSet.getInt(2);
            int points = resultSet.getInt(3);
            tableWithResults.append(i)
                    .append(". ")
                    .append(name)
                    .append(" level: ")
                    .append(level)
                    .append(" points: ")
                    .append(points)
                    .append("\n");
            i++;
            if(i==10) break;
        }
        return tableWithResults.toString();
    }

    public String readMe() {
        return "Бот создан преимущественно в учебно-практических целях изучения языка программирования Java. " +
                "В этих словах могут быть опечатки, неточности и т.д. При возможности буду чистить их и улучшать. " +
                "Если тебе нужны какие-то конкретные слова, то ты можешь написать мне - @spacelord_21, и, возможно, " +
                "я помогу, но не обещаю :) Удачи! \n" +
                "Слова взяты с сайта: http://wordsteps.com/";
    }
}
