package org.example;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.FakeDataGeneratorClass.FakeDataGenerator;
import org.example.FakeDataGeneratorClass.FieldType;
import org.example.FakeDataGeneratorClass.Pairs;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
public class TelegramBotUpdateHandler {
    private final static ConcurrentHashMap<Long,State> usersState = new ConcurrentHashMap<>();
    private static ArrayList<Pairs> pairs = new ArrayList<>();
    private static ArrayList<String> commands = new ArrayList<>();
    private final TelegramBot bot = new TelegramBot(ResourceBundle.getBundle("settings").getString("bot.token"));

    private void deleteFile(Path path) throws IOException {
        Files.delete(path);
    }
    public void handle(Update update) throws IOException {
        Message message = update.message();
        CallbackQuery callbackQuery = update.callbackQuery();
        if(message != null){
            Chat chat = message.chat();
            Long chatId = chat.id();
            if(message.text().equals("/start")){
                SendMessage sendMessage = new SendMessage(chatId, "Welcome!\nTo generate fake date send /generate command");
                bot.execute(sendMessage);
            }
            else if(message.text().equals("/generate")){
                var sendMessage = new SendMessage(chatId,"Send File Name");
                bot.execute(sendMessage);
                usersState.put(chatId,State.FILE_NAME);

            }
            else if(State.FILE_NAME.equals(usersState.get(chatId))){
                String text = message.text().replaceAll("\\s+","");
                commands.add(text);
                var sendMessage = new SendMessage(chatId,"Send File format\nLike json,sql or csv");
                bot.execute(sendMessage);
                usersState.put(chatId,State.TYPE);

            }
            else if(State.TYPE.equals(usersState.get(chatId))){
                if(message.text().equals("json")|| message.text().equals("sql")  || message.text().equals("csv")){
                    commands.add(message.text());
                    var sendMessage = new SendMessage(chatId,"Send Row count");
                    bot.execute(sendMessage);
                    usersState.put(chatId,State.ROW_COUNT);
                }
                else{
                    SendMessage sendMessage =new SendMessage(chatId,"\uD83D\uDFE5 Error:Make sure you wrote the format of the file correctly\n/generate!");
                    bot.execute(sendMessage);
                }

            }
            else if(State.ROW_COUNT.equals(usersState.get(chatId))){
                    commands.add(message.text());
                    var sendMessage = new SendMessage(chatId,"Choose fields");
                    sendMessage.replyMarkup(getInlineMarkupKeyboard());
                    bot.execute(sendMessage);
                    usersState.put(chatId,State.FIELDS);
            }
            else{
                DeleteMessage deleteMessage = new DeleteMessage(chatId,message.messageId());
                bot.execute(deleteMessage);
            }
        }
        else{
            FieldType[] fieldTypes = FieldType.values();
            String data = callbackQuery.data();
            Chat chat = callbackQuery.message().chat();
            Long chatID = chat.id();
            if(data.equals("g")){
                try{
                    FakeDataGenerator fakeDataGenerator = new FakeDataGenerator(commands.get(0),Integer.parseInt(commands.get(2)),commands.get(1),pairs);
                    fakeDataGenerator.generateDataBasedOnType();
                    SendDocument sendDocument = new SendDocument(chatID, fakeDataGenerator.getPath().toFile());
                    bot.execute(sendDocument);
                    pairs.clear();
                    commands.clear();
                    bot.execute(new DeleteMessage(chatID,callbackQuery.message().messageId()));
                    deleteFile(fakeDataGenerator.getPath());

                }catch (IOException e){
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(callbackQuery.id());
                    answerCallbackQuery.text("\uD83D\uDFE5Error: Something went wrong, please try again!");
                    answerCallbackQuery.showAlert(true);
                    bot.execute(answerCallbackQuery);
                }
            }
            else{
                FieldType fieldType = fieldTypes[Integer.parseInt(data)];
                pairs.add(new Pairs(fieldType.name().toLowerCase(),fieldType));
            }


        }
    }
    private Keyboard getInlineMarkupKeyboard() {
        FieldType[] fieldTypes = FieldType.values();
        InlineKeyboardButton[][] buttons = new InlineKeyboardButton[10][2];
        for (int i = 0; i < fieldTypes.length/2; i++) {
            InlineKeyboardButton button1 = new InlineKeyboardButton(fieldTypes[i * 2].name());
            InlineKeyboardButton button2 = new InlineKeyboardButton(fieldTypes[i * 2 + 1].name());
            button1.callbackData((i*2)+"");
            button2.callbackData(""+ (i*2+1));
            buttons[i][0] = button1;
            buttons[i][1] = button2;
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(buttons);
        InlineKeyboardButton generate = new InlineKeyboardButton("✔️ Generate");
        inlineKeyboardMarkup = inlineKeyboardMarkup.addRow(generate);
        generate.callbackData("g");
        return inlineKeyboardMarkup;
    }

}
enum State{
    FILE_NAME,
    TYPE,
    ROW_COUNT,
    FIELDS
}
