package service;

import dao.DelayMessageDao;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class DelayMessageService {

    private DelayMessageDao dao = new DelayMessageDao();

    public SendMessage delayMessage(Message msg) {
        SendMessage s = new SendMessage();
        s.setChatId(dao.findId());
        System.out.println("send message in delayMessage: '" + s + "'");
        s.setText("Hello from " + msg.getFrom().getUserName() + "! This is simple delay message bot! Reply you message: '" + msg + "'");
        return s;
    }

    public SendMessage helloMessage(Message msg) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId());
        s.setText("Приветствую, " + msg.getFrom().getFirstName() + "! Это простой бот для отправки сообщений с задержкой!" +
                " Вы можете лишь получать сообщения от этого бота. " +
                "Для отправки требуются специальные права, полученные у создателя бота.");
        return s;
    }

    public SendMessage accessDeniedMessage(Message msg) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId());
        s.setText("Ошибка доступа! Вы можете лишь получать сообщения от этого бота.");
        return s;
    }

    public boolean isAccessPermissionOk(Integer id) {
        return dao.getDelayMasters().contains(id);
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
                "messageId - уникальный илентификатор создаваемого события. Использование нескольких слов допустимо лишь в кавычках (\"Brother's birthday\", \"День свадьбы\" и т.д.)\n" +
                "date - требуемая дата отправки отложенного сообщения. Синтаксис ГГГГ-ММ-ДДТчч:мм, где ГГГГ - год из четырех цифр, ММ - месяц из двух цифр, ДД - день из двух цифр, " +
                "T - разделитель между датой и временем (латинская заглавная буква), чч - часы из двух цифр (24-часовой формат), мм - минуты из двух цифр. Пример: 2020-12-09T16:05\n" +
                "dstUsername - логин пользователя, которому необходимо доставить отложенное сообщение (записывается с символом '@')\n" +
                "textMessage - текстовое сообщение, которо нужно передать с задержкой. Использование нескольких слов допустимо лишь в кавычках, как и messageId");
        return s;
    }

    public SendMessage getEvents(Message msg) {
        return null;
    }

    public SendMessage updateEvent(Message msg) {
        return null;
    }
}
