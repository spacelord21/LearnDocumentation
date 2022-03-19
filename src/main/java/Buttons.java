import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class Buttons {
    public void createButtons(SendMessage sendMessage, String...text) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRowFirst = new KeyboardRow();
        for (int i = 0; i < 2; i++) {
            keyboardRowFirst.add(new KeyboardButton(text[i]));
        }
        keyboardRowList.add(keyboardRowFirst);
        if (text.length > 3) {
            KeyboardRow keyboardRowSecond = new KeyboardRow();
            for (int i = 2; i < text.length; i++) {
                keyboardRowSecond.add(new KeyboardButton(text[i]));
            }
            keyboardRowList.add(keyboardRowSecond);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    public SendMessage createInlineButtons(String answer, Message message, String question) {
        answer = answer.replaceAll(";", "");
        question = question.replaceAll(";", "");
        if(answer.length() > 35) {
            String[] remakeAnswer = answer.split(",");
            answer = remakeAnswer[0];
        }
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> firstRowButton = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Я забыв");
        button.setCallbackData(answer);
        firstRowButton.add(button);
        inlineButtons.add(firstRowButton);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(inlineButtons);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(question);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }
}
