package service;

import dao.DelayMessageDao;
import org.quartz.Scheduler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import quartz.DelayBotJob;

import java.util.Map;

public class DelayMessageService {

    private DelayMessageDao dao = new DelayMessageDao();
    private QuartzService quartzService = new QuartzService();

    public SendMessage delayMessage(Message msg, Map<String, String> parameters) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId());
//        s.setText("Hello from " + msg.getFrom().getUserName() + "! ");
        // Добавляем событие в базу
        if (dao.addEvent(msg.getChatId(), parameters)) {
            // Записываем id отправителя для текущего ответа бота
            s.setChatId(msg.getChatId());
            // Добавляем задание для кварца
            try {
                Scheduler scheduler = quartzService.createAndStartScheduler();
                quartzService.fireJob(parameters.get("messageId"), scheduler, DelayBotJob.class);
                s.setText("New event '" + parameters.get("messageId") + "' created!");
            } catch (Exception e) {
                System.out.println("Problems with event creation\n" + e);
                s.setText(" !!! Could not create new event (db)");
            }
        } else {
            s.setText(" !!! Could not create new event (db)");
        }
        return s;
    }

    public SendMessage helloMessage(Message msg) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId());
        s.setText("Приветствую, " + msg.getFrom().getFirstName() + "! Это простой бот для отправки сообщений с задержкой!" +
                " Вы можете лишь получать сообщения от этого бота. " +
                "Для отправки требуются специальные права, полученные у создателя бота. Для просмотра списка доступных команд и их синтаксиса отправьте /help");
        return s;
    }

    public SendMessage accessDeniedMessage(Message msg) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId());
        s.setText("Ошибка доступа! Вы можете лишь получать сообщения от этого бота.");
        return s;
    }

    public boolean isAccessPermissionOk(Integer id) {
        return dao.getDelayWriters().contains(id);
    }

    public boolean isResponseEmpty(SendMessage response) {
        return response.getChatId() == null && response.getText() == null;
    }

    public SendMessage getDefaultMessage(Message msg) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId());
        s.setText("Что-то не так. Проверьте правильность запроса с помощью /help.");
        return s;
    }

    public SendMessage helpMessage(Message msg) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId());
        s.setText("Для создания отложенного сообщения отправьте: \n" +
                "\"/new messageId date dstUsername textMessage\"\n" +
                "Например: /new \"Mom's Birthday\" 2018-10-31T22:31 @reinhagenau \"Happy Birthday!\"\n" +
                "/new - имя команды\nmessageId - уникальный идентификатор создаваемого сообщения\n" +
                "messageId - уникальный илентификатор создаваемого события. Использование нескольких слов допустимо лишь в кавычках (\"Brother's birthday\", \"День свадьбы\" и т.д.). " +
                    "Кавычки внутри уже выделенной последовательности слов не допускаются\n" +
                "date - требуемая дата отправки отложенного сообщения. Синтаксис ГГГГ-ММ-ДДТчч:мм, где ГГГГ - год из четырех цифр, ММ - месяц из двух цифр, ДД - день из двух цифр, " +
                "T - разделитель между датой и временем (латинская заглавная буква), чч - часы из двух цифр (24-часовой формат), мм - минуты из двух цифр. Пример: 2020-12-09T16:05\n" +
                "dstUsername - логин пользователя, которому необходимо доставить отложенное сообщение (записывается с символом '@')\n" +
                "textMessage - текстовое сообщение, которо нужно передать с задержкой. Использование нескольких слов допустимо лишь в кавычках, как и messageId\n\n" +
                "Для изменения отложенного сообщения отправьте новые данные \n" +
                "\"/update messageId date dstUsername textMessage\"\n" +
                "с тем же синтаксисом, что и /new, описанный выше, указав при этом messageId того сообщения, которое хотите изменить\n\n" +
                "Для получения созданных событий, чье пробуждение уже не в прошлом отправьте \n" +
                "/events\n\n" +
                "Для получения помощи отправьте /help\n\n" +
                "/start используется для запуска подключения к боту. Отправляется автоматически при нажатии на START при первом посещении бота.");
        return s;
    }

    public SendMessage getEvents(Message msg) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId());
        String events = dao.getAllEvents(msg.getChatId());
        s.setText(events);
        return s;
    }

    public SendMessage updateEvent(Message msg, Map<String, String> parameters) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId());
        if (dao.updateEvent(parameters)) {
            if (quartzService.rescheduleEvent(parameters))
                s.setText("Задание1 '" + parameters.get("messageId") + "' обновлено!");
            else s.setText("Задание1 '" + parameters.get("messageId") + "' не обновлено!");
        } else {
            s.setText("Задание2 '" + parameters.get("messageId") + "' не обновлено!");
        }
        return s;
    }
}
