package org.example;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import lombok.Data;
import org.example.FakeDataGeneratorClass.FakeDataGenerator;
import org.example.FakeDataGeneratorClass.FieldType;
import org.example.FakeDataGeneratorClass.Pairs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Pairs> pairs = new ArrayList<>();
        pairs.add(new Pairs("age", FieldType.AGE));
        pairs.add(new Pairs("name",FieldType.FIRST_NAME));
        FakeDataGenerator fakeDataGenerator = new FakeDataGenerator("jsonData",100,"json",pairs);
    }
}