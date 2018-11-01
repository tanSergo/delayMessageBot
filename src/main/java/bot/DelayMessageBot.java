package bot;

import controller.DelayMessageController;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import util.ApplProps;

public class DelayMessageBot extends TelegramLongPollingBot {

    static {
        ApplProps.getProps("application.properties"); // чтение application.properties
    }

    public static void main(String[] args) {
        ApiContextInitializer.init(); // Инициализируем апи
        TelegramBotsApi botapi = new TelegramBotsApi();
        try {
            botapi.registerBot(new DelayMessageBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpdateReceived(Update update) {
        DelayMessageController controller = new DelayMessageController();
        SendMessage response = controller.handleRequest(update);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return ApplProps.get("bot.username");
        //возвращаем юзера
    }

    @Override
    public String getBotToken() {
        return ApplProps.get("bot.token");
        //Токен бота
    }
}
