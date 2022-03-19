import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class Bot extends TelegramLongPollingBot {

    HashMap<Long, ArrayList<String>> wordsWithKey = new HashMap<>();
    HashMap<Long, ArrayList<String>> doubleWordsWithKey = new HashMap<>();
    HashMap<Long, Integer> numberForGenerateWord = new HashMap<>();
    HashMap<Long,Message> uniqueMessage = new HashMap<>();
    Buttons buttons = new Buttons();
    UserCreator userCreator = new UserCreator();
    Points points = new Points();
    Information information = new Information();
    QuestionsParser questionsParser = new QuestionsParser();
    HashMap<Long,Boolean> firstPermissionMode = new HashMap<>();
    HashMap<Long,Boolean> secondPermissionMode = new HashMap<>();


    @Override
    public String getBotUsername() {
        return "FIrstJavaTest_bot";
    }

    @Override
    public String getBotToken() {
        return "5154928569:AAHrksvlAFGLxWOuokYPDLXMvh81zdEnzaY";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                Long chatId = message.getChatId();
                if (((firstPermissionMode.get(chatId) != null && firstPermissionMode.get(chatId))
                        || (secondPermissionMode.get(chatId) != null && secondPermissionMode.get(chatId)))
                && !message.getText().equals("Готов")) {
                    if(message.getText().equals("Выйти в главное меню")
                    || message.getText().equals("/start")) {
                        sendMsg(message,chatId,"Главное меню");
                        firstPermissionMode.put(chatId,false);
                        secondPermissionMode.put(chatId,false);
                    }
                    else {
                        firstMode(message, chatId);
                    }
                }
                else {
                    if (message.getText().equals("Выйти в главное меню") || message.getText().equals("/start")) {
                        sendMsg(message, chatId, "Привет, друг!");
                    } else if (message.getText().equals("Английский->Русский")) {
                        if(userCreator.checkChatId(chatId)) {
                            sendMsg(message, chatId, "Запущен 1ый режим");
                            createWords(chatId);
                            firstPermissionMode.put(chatId, true);
                            secondPermissionMode.put(chatId, false);
                        }
                        else {
                            sendMsg(message,chatId,"Для начала давай познакомимся. Нажми 'Регистрация'");
                        }
                    } else if (message.getText().equals("Русский->Английский")) {
                        if(userCreator.checkChatId(chatId)) {
                            sendMsg(message, chatId, "Запущен 2ой режим");
                            createWords(chatId);
                            secondPermissionMode.put(chatId, true);
                            firstPermissionMode.put(chatId, false);
                        }
                        else {
                            sendMsg(message,chatId,"Для начала давай познакомимся. Нажми 'Регистрация'");
                        }
                    } else if (message.getText().equals("Регистрация")) {
                        try {
                            sendMsg(message, chatId, userCreator.createUser(message));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else if (message.getText().equals("Готов")) {
                        sendMsg(message, chatId, "Тогда начинаем!");
                        deleteMessage(chatId);
                        sendFirstMode(message, chatId);
                    } else if(message.getText().equals("Помощь")) {
                        sendMsg(message,chatId,information.helpInfo());
                    }
                    else if(message.getText().equals("Информация")) {
                        sendMsg(message,chatId,information.readMe());
                        sendMsg(message,chatId,"Лучшие результаты:");
                        sendMsg(message,chatId,information.bestUsers());
                    }
                }
            } else if (update.hasCallbackQuery()) {
                System.out.println("я тут");
                try {
                    AnswerCallbackQuery answer = new AnswerCallbackQuery();
                    answer.setText(update.getCallbackQuery().getData());
                    answer.setCallbackQueryId(update.getCallbackQuery().getId());
                    answer.setShowAlert(true);
                    try {
                        execute(answer);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFirstMode(Message message, Long chatId) {
        try {
            ArrayList<String> containerForWords = wordsWithKey.get(chatId);
            if (containerForWords.size() == 0) {
                sendMsg(message, chatId, "Отличная работа! Вот тебе следующая партия слов, как будешь готов нажми 'Готов'!");
                createWords(chatId);
                firstPermissionMode.put(chatId, false);
                secondPermissionMode.put(chatId,false);
            } else {
                ArrayList<String> containerForDoubleWords = doubleWordsWithKey.get(chatId);
                if (containerForDoubleWords != null) {
                    containerForDoubleWords.clear();
                    doubleWordsWithKey.put(chatId, containerForDoubleWords);
                }
                createDouble(chatId);
                createInlineButton(chatId, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createInlineButton(Long chatId, Message message) {
        try {
            ArrayList<String> containerForDoubleWords = doubleWordsWithKey.get(chatId);
            String answer = containerForDoubleWords.get(1);
            String question = containerForDoubleWords.get(0);
            if(secondPermissionMode.get(chatId)) {
                answer = containerForDoubleWords.get(0);
                question = containerForDoubleWords.get(1);
                System.out.println(containerForDoubleWords.get(0));
            }
            System.out.println(containerForDoubleWords.get(1));
            try {
                execute(buttons.createInlineButtons(answer, message, question));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void firstMode(Message message, Long chatId) {
        try {
            String answer = message.getText();
            answer = answer.toLowerCase();
            System.out.println(doubleWordsWithKey);
            int j = 1;
            if(secondPermissionMode.get(chatId)) {
                j = 0;
            }
            ArrayList<String> containerForDoubleWords = doubleWordsWithKey.get(chatId);
            String prepareForPossitiveAnswer = containerForDoubleWords.get(j).replaceAll(";", ",");
            String[] possitiveAnswer = prepareForPossitiveAnswer.split(",");
            boolean itsCorrect = false;
            for (int i = 0; i < possitiveAnswer.length; i++) {
                possitiveAnswer[i] = possitiveAnswer[i].replaceAll(";", "").trim();
                if (answer.equals(possitiveAnswer[i])) {
                    sendMsg(message, chatId, "Верно");
                    sendMsg(message,chatId,points.givePoint(chatId));
                    removeDoubleFromWords(chatId);
                    sendFirstMode(message, chatId);
                    itsCorrect = true;
                }
            }
            if (!itsCorrect) {
                sendMsg(message, chatId, "Неверно! Попробуй еще раз или подсмотри ответ.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeDoubleFromWords(Long chatId) {
        int i = numberForGenerateWord.get(chatId);
        try {
            ArrayList<String> containerForWords = wordsWithKey.get(chatId);
            if (i % 2 == 0) {
                containerForWords.remove(i);
                containerForWords.remove(i);
            } else {
                containerForWords.remove(i);
                containerForWords.remove(i - 1);
            }
            wordsWithKey.put(chatId, containerForWords);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDouble(Long chatId) {
        try {
            ArrayList<String> containerForWords = wordsWithKey.get(chatId);
            ArrayList<String> containerForDoubleWords = new ArrayList<>();
            numberForGenerateWord.put(chatId,(int)(Math.random()*containerForWords.size()));
            int i = numberForGenerateWord.get(chatId);
            if (i % 2 == 0) {
                containerForDoubleWords.add(containerForWords.get(i));
                containerForDoubleWords.add(containerForWords.get(i + 1));
                doubleWordsWithKey.put(chatId, containerForDoubleWords);
            } else {
                containerForDoubleWords.add(containerForWords.get(i - 1));
                containerForDoubleWords.add(containerForWords.get(i));
                doubleWordsWithKey.put(chatId, containerForDoubleWords);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createWords(Long chatId) {
        try {
            wordsWithKey.put(chatId, questionsParser.createResultWords());
            ArrayList<String> containerForWords = wordsWithKey.get(chatId);
            StringBuilder words = new StringBuilder();
            for (int i = 0; i < containerForWords.size(); i += 2) {
                words.append(containerForWords.get(i)).append(" - ").append(containerForWords.get(i + 1)).append("\n");
            }
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(words.toString());
            try {
                Message messageWithWords = execute(sendMessage);
                uniqueMessage.put(chatId,messageWithWords);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteMessage(Long chatId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        if(uniqueMessage.get(chatId) != null) {
            deleteMessage.setMessageId(uniqueMessage.get(chatId).getMessageId());
            deleteMessage.setChatId(chatId.toString());
            try {
                execute(deleteMessage);
                uniqueMessage.remove(chatId);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMsg(Message message, Long chatId, String text) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(text);
            try {
                switch (message.getText()) {
                    case "/start", "Выйти в главное меню" -> buttons.createButtons(sendMessage, "Помощь", "Информация", "Регистрация",
                            "Английский->Русский", "Русский->Английский");
                }
                switch (text) {
                    case "Запущен 1ый режим", "Запущен 2ой режим" -> buttons.createButtons(sendMessage, "Готов", "Выйти в главное меню");
                }
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}