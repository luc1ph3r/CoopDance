package ru.tayrinn.telegram.coopdance;

import ru.tayrinn.telegram.coopdance.models.ChatDao;
import ru.tayrinn.telegram.coopdance.models.ChatMessage;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ChatDaoImpl implements ChatDao {
    private static final String SELECT_LAST_CHAT_MESSAGES_QUERY = "SELECT top ? * FROM CHAT_MESSAGES WHERE CHAT_ID = ?";
    private static final String DROP = "DROP TABLE IF EXISTS CHAT_MESSAGES";
    private static final String CREATE = "" +
            "CREATE TABLE CHAT_MESSAGES (" +
            "id INTEGER AUTO_INCREMENT PRIMARY KEY," +
            ChatMessage.KEY_CHAT_ID + " TEXT," +
            ChatMessage.KEY_TEXT + " TEXT," +
            ChatMessage.KEY_MESSAGE_ID + " TEXT," +
            ChatMessage.KEY_AUTHOR_USERNAME + " TEXT," +
            ChatMessage.KEY_IS_BOT + " INT," +
            ChatMessage.KEY_PAYLOAD + " TEXT," +
            ChatMessage.KEY_TIMESTAMP + " TIMESTAMP" +
            ")";

    private final Statement stmt;

    public ChatDaoImpl(DataSource dataSource) throws SQLException {
        stmt = dataSource.getConnection().createStatement();
        stmt.execute(CREATE);
    }

    @Override
    public void writeChatMessage(ChatMessage chatMessage) throws SQLException {
        stmt.execute("INSERT INTO CHAT_MESSAGES VALUES(" +
                chatMessage.getChatId() + ", " +
                chatMessage.getText() + ", " +
                chatMessage.getMessageId() + ", " +
                chatMessage.getAuthorUsername() + ", " +
                chatMessage.isBot()  + ", " +
                chatMessage.getPayload() + ", " +
                "now()" +
                ")");
    }

    @Override
    public List<ChatMessage> getLastChatMessages(String chatId, String authorName, Integer count) throws SQLException {
        ResultSet resultSet = stmt.executeQuery("SELECT * FROM CHAT_MESSAGES WHERE" +
                ChatMessage.KEY_CHAT_ID + " = " + chatId +
                "AND " + ChatMessage.KEY_AUTHOR_USERNAME + " = " + authorName);
        List<ChatMessage> result = new ArrayList<>(resultSet.getFetchSize());
        while (resultSet.next()) {
            ChatMessage msg = new ChatMessage();
            msg.setChatId(chatId);
            msg.setAuthorUsername(authorName);
            msg.setText(resultSet.getString(ChatMessage.KEY_TEXT));
            msg.setMessageId(resultSet.getString(ChatMessage.KEY_MESSAGE_ID));
            msg.setBot(resultSet.getInt(ChatMessage.KEY_IS_BOT));
            msg.setPayload(resultSet.getString(ChatMessage.KEY_PAYLOAD));
            result.add(msg);
        }

        resultSet.close();

        return result;
    }

    @Override
    public void updateChatMessage(ChatMessage chatMessage) {

    }

}