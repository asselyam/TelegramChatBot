package com.example.kazstorebot;

import com.example.kazstorebot.repositories.MenuRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import static com.example.kazstorebot.MenuItem.Category.*;
import static com.example.kazstorebot.MenuItem.Size.*;

@SpringBootApplication
public class KazStoreBotApplication {

    public static void main(String[] args) throws TelegramApiException {
        SpringApplication app = new SpringApplication(KazStoreBotApplication.class);
        ConfigurableApplicationContext context = app.run(args);
        MenuRepository repo = context.getBean(MenuRepository.class);

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            botsApi.registerBot(new MyBot(repo));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Bean
    CommandLineRunner init(MenuRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new MenuItem(null, "Маргарита", 2000, PIZZA, MEDIUM));
                repo.save(new MenuItem(null, "Пепперони", 2200, PIZZA, MEDIUM));
                repo.save(new MenuItem(null, "Чизбургер", 1400, BURGER, MEDIUM));
                repo.save(new MenuItem(null, "Классический Бургер", 1200, BURGER, MEDIUM));
                repo.save(new MenuItem(null, "Филадельфия", 2500, SUSHI, MEDIUM));
                repo.save(new MenuItem(null, "Калефорния", 2700, SUSHI, MEDIUM));
                repo.save(new MenuItem(null, "Острые Крылышки", 3000, WINGS, MEDIUM));
                repo.save(new MenuItem(null, "Чесночные Крылышки", 3200, WINGS, MEDIUM));
                repo.save(new MenuItem(null, "Классические Крылышки", 2800, WINGS, MEDIUM));
                repo.save(new MenuItem(null, "Фри", 500, FRIES, MEDIUM));
                repo.save(new MenuItem(null, "Кетчуп", 150, SAUCES, MEDIUM));
                repo.save(new MenuItem(null, "Сырный Соус", 150, SAUCES, MEDIUM));
                repo.save(new MenuItem(null, "Барбекю Соус", 150, SAUCES, MEDIUM));
                repo.save(new MenuItem(null, "Кола", 800, DRINK, LARGE));
                repo.save(new MenuItem(null, "Фанта", 800, DRINK, LARGE));
                repo.save(new MenuItem(null, "Спрайт", 800, DRINK, LARGE));
                repo.save(new MenuItem(null, "Ванильное мороженное", 400, DESSERT, MEDIUM));
                repo.save(new MenuItem(null, "Шоколадное мороженное", 400, DESSERT, MEDIUM));
            }
        };
    }


}
