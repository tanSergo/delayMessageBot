package controller;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import service.DelayMessageService;
import service.HelpersService;

import java.util.Map;


public class DelayMessageController {

    private DelayMessageService messageService = new DelayMessageService();
    private HelpersService helperService = new HelpersService();

    public SendMessage handleRequest(Update u) {

        System.out.println("Received message " + u.toString());

        Message msg = u.getMessage();
        String txt = msg.getText();
        SendMessage response = new SendMessage();

        // Проверяем, есть ли у пользователя права на использование бота
        if (!messageService.isAccessPermissionOk(msg.getFrom().getId())) {
            return messageService.accessDeniedMessage(msg);
        } else {
            // Проверяем валидность запроса пользователя
            Map<String, String> parameters = helperService.parseParameters(txt);
            if (parameters.isEmpty())
                return messageService.getDefaultMessage(msg);

// TODO: 05.11.2018 Сделать нормальное логирование и провести поиск багов

            switch (parameters.get("command")) {
                case "/start":
                    response = messageService.helloMessage(msg);
                    break;
                case "/help":
                    response = messageService.helpMessage(msg);
                    break;
                case "/new":
                    response = messageService.delayMessage(msg, parameters);
                    break;
                case "/events":
                    response = messageService.getEvents(msg);
                    break;
                case "/update":
                    response = messageService.updateEvent(msg, parameters);

                default: // Если ответ пуст, то отсылаем сообщение о возникших проблемах
                    if (messageService.isResponseEmpty(response))
                        response = messageService.getDefaultMessage(msg);
                    break;
            }

            return response;
        }
    }
}
