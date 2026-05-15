package com.example.kazstorebot;

import com.example.kazstorebot.repositories.MenuRepository;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBot extends TelegramLongPollingBot {
    private final Map<String, List<MenuItem>> carts = new HashMap<>();
    private final MenuRepository repo;
    private final Map<String, User.UserState> userStates = new HashMap<>();
    private final Map<String, String> userNames = new HashMap<>();
    private final Map<String, String> userPhones = new HashMap<>();
    private final Map<String, String> userAddresses = new HashMap<>();
    private final String ADMIN_CHAT_ID = "-5239231920";

    public MyBot(MenuRepository repo) {
        this.repo = repo;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            if (!update.getMessage()
                    .getChat()
                    .isUserChat()) {
                return;
            }
            User.UserState state = userStates.getOrDefault(chatId, User.UserState.NONE);
            if (state != User.UserState.NONE) {
                handleOrder(update);
            } else {
                handleStart(update);
            }
        }

        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        }
    }

    private void handleStart(Update update) {
        String chatId = update.getMessage().getChatId().toString();

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton menuBtn = new InlineKeyboardButton();
        menuBtn.setText("Меню товаров");
        menuBtn.setCallbackData("MENU");

        InlineKeyboardButton cartBtn = new InlineKeyboardButton();
        cartBtn.setText("Корзина");
        cartBtn.setCallbackData("CART");

        row1.add(menuBtn);
        row1.add(cartBtn);

        rows.add(row1);
        keyboard.setKeyboard(rows);
        sendMessage(chatId, "Выберете действие:", keyboard);
    }

    private void handleCallbackQuery(Update update) {
        String data = update.getCallbackQuery().getData();
        String dataChatId = update.getCallbackQuery().getMessage().getChatId().toString();
        carts.putIfAbsent(dataChatId, new ArrayList<>());

        if (data.equals("MENU")) {
            showCategories(dataChatId);
        }

        if (data.startsWith("ADD_")) {
            addToCart(data, dataChatId);
        }

        if (data.equals("CLEAR_CART")) {
            carts.get(dataChatId).clear();
            sendMessage(dataChatId, "Корзина очищена", null);
        }

        if (data.equals("ORDER")) {
            userStates.put(dataChatId, User.UserState.WAITING_NAME);
            sendMessage(dataChatId, "Введите ваше имя:", null);
        }

        if (data.equals("CART")) {
            showCart(dataChatId);
        }

        if (data.equals("СASH")) {
            sendMessage(dataChatId, "Заказ оформлен! Оплата при доставке.", null);
            sendMessage(ADMIN_CHAT_ID, "Новый заказ!", null);
        }

        if (data.equals("CARD")) {
            sendMessage(dataChatId,
                    """
                    Переведите оплату на номер
                    +7 777 777 77 77
                    Имя
                    Пожалуйста дождитесь подтверждения об оплате.
                    """, null);
            sendMessage(ADMIN_CHAT_ID, "Подвердите оплату от " + userNames.get(dataChatId), null);
        }

        try {
            MenuItem.Category category = MenuItem.Category.valueOf(data);
            List<MenuItem> items = repo.findByCategory(category);
            InlineKeyboardMarkup keyboardMarkup = createKeyboard(items);
            sendMessage(dataChatId, "Выберете товар:", keyboardMarkup);
        } catch (IllegalArgumentException e) {

        }
    }

    private void handleOrder(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String text = update.getMessage().getText();
        User.UserState state = userStates.getOrDefault(chatId, User.UserState.NONE);

        if (state == User.UserState.WAITING_NAME) {
            userNames.put(chatId, text);
            userStates.put(chatId, User.UserState.WAITING_PHONE);
            sendMessage(chatId, "Введите номер телефона:", null);
            return;
        }

        if (state == User.UserState.WAITING_PHONE) {
            userPhones.put(chatId, text);
            userStates.put(chatId, User.UserState.WAITING_ADDRESS);
            sendMessage(chatId, "Введите адрес доставки:", null);
            return;
        }

        if (state == User.UserState.WAITING_ADDRESS) {
            userAddresses.put(chatId, text);
            userStates.put(chatId, User.UserState.NONE);

            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton cashPaymentBtn = new InlineKeyboardButton();
            cashPaymentBtn.setText("Оплата наличными");
            cashPaymentBtn.setCallbackData("CASH");

            InlineKeyboardButton cardPaymentBtn = new InlineKeyboardButton();
            cardPaymentBtn.setText("Оплата Kaspi");
            cardPaymentBtn.setCallbackData("CARD");

            row.add(cashPaymentBtn);
            row.add(cardPaymentBtn);
            rows.add(row);
            keyboard.setKeyboard(rows);
            sendMessage(chatId, "Выберете способ оплаты:", keyboard);
        }
    }

    private void sendOrderToAdmin() {
        
    }

    private void showCategories(String dataChatId) {
        InlineKeyboardMarkup menuKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton pizza = new InlineKeyboardButton();
        pizza.setText("Пицца");
        pizza.setCallbackData(MenuItem.Category.PIZZA.toString());

        InlineKeyboardButton burger = new InlineKeyboardButton();
        burger.setText("Бургер");
        burger.setCallbackData(MenuItem.Category.BURGER.toString());

        InlineKeyboardButton sushi = new InlineKeyboardButton();
        sushi.setText("Суши");
        sushi.setCallbackData(MenuItem.Category.SUSHI.toString());

        InlineKeyboardButton wings = new InlineKeyboardButton();
        wings.setText("Крылышки");
        wings.setCallbackData(MenuItem.Category.WINGS.toString());

        row1.add(pizza);
        row1.add(burger);
        row1.add(sushi);
        row1.add(wings);

        List<InlineKeyboardButton> row2 = new ArrayList<>();

        InlineKeyboardButton fries = new InlineKeyboardButton();
        fries.setText("Фри");
        fries.setCallbackData(MenuItem.Category.FRIES.toString());

        InlineKeyboardButton sauces = new InlineKeyboardButton();
        sauces.setText("Соусы");
        sauces.setCallbackData(MenuItem.Category.SAUCES.toString());

        InlineKeyboardButton drinks = new InlineKeyboardButton();
        drinks.setText("Напитки");
        drinks.setCallbackData(MenuItem.Category.DRINK.toString());

        InlineKeyboardButton desserts = new InlineKeyboardButton();
        desserts.setText("Десерт");
        desserts.setCallbackData(MenuItem.Category.DESSERT.toString());

        row2.add(fries);
        row2.add(sauces);
        row2.add(drinks);
        row2.add(desserts);

        rows.add(row1);
        rows.add(row2);
        menuKeyboard.setKeyboard(rows);
        sendMessage(dataChatId, "Выберете из списка товаров...", menuKeyboard);
    }

    private void addToCart(String data, String dataChatId) {
        Long itemId = Long.parseLong(data.replace("ADD_", ""));
        MenuItem item = repo.findById(itemId).orElse(null);

        if (item != null) {
            carts.get(dataChatId).add(item);
            sendMessage(dataChatId, item.getName() + " добавлен(а) в корзину", null);
        }
    }

    private void showCart(String dataChatId) {
        List<MenuItem> userCart = carts.get(dataChatId);

        if (userCart == null || userCart.isEmpty()) {
            sendMessage(dataChatId, "Корзина пустая", null);
        } else {
            StringBuilder text = new StringBuilder("Ваша корзина:\n");
            int total = 0;
            for (MenuItem item: userCart) {
                text.append(item.getName())
                        .append(" - ")
                        .append(item.getPrice())
                        .append(" тг\n");
                total += item.getPrice();
            }
            text.append("\nИтого: ").append(total).append(" тг");

            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            InlineKeyboardButton clearBtn = new InlineKeyboardButton();
            clearBtn.setText("Очистить корзину");
            clearBtn.setCallbackData("CLEAR_CART");

            InlineKeyboardButton orderBtn = new InlineKeyboardButton();
            orderBtn.setText("Оформить заказ");
            orderBtn.setCallbackData("ORDER");

            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(clearBtn);
            row.add(orderBtn);
            rows.add(row);

            keyboardMarkup.setKeyboard(rows);
            sendMessage(dataChatId, text.toString(), keyboardMarkup);
        }
    }

    private InlineKeyboardMarkup createKeyboard(List<MenuItem> items) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (MenuItem item: items) {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(item.getName() + " - " + item.getPrice());
            btn.setCallbackData("ADD_" + item.getId());
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(btn);
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private void sendMessage(String chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        if (keyboard != null) {
            message.setReplyMarkup(keyboard);
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "KazStore_bot";
    }

    @Override
    public String getBotToken() {
        return "REMOVED";
    }
}
