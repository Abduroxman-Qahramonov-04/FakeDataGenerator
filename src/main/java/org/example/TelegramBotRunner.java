package org.example;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class TelegramBotRunner {
    private static final ResourceBundle settings = ResourceBundle.getBundle("settings");
    private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final ThreadLocal<TelegramBotUpdateHandler> telegramBotHandler = ThreadLocal.withInitial(TelegramBotUpdateHandler::new);

    public static void main(String[] args){
        TelegramBot bot = new TelegramBot(settings.getString("bot.token"));
        bot.setUpdatesListener((updates)->{
            for(Update update : updates){
                CompletableFuture.runAsync(()->{

                    try {
                        telegramBotHandler.get().handle(update);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },executorService);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;

        }, Throwable::printStackTrace);
    }
}
