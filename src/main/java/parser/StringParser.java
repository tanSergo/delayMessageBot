package parser;

import java.util.HashMap;
import java.util.Map;

public class StringParser implements MessageParser {
    @Override
    public Map<String, String> parseParameters(String txt) {
        Map<String, String> params = new HashMap<>();
        String[] split = txt.trim().split(" ");
        String comm = split[0];
        // Собираем имя команды
        if (!(comm.equals("/start") || comm.equals("/help") || comm.equals("/new") || comm.equals("/events") || comm.equals("/update")))
            return null;
        else params.put("command", comm);

        // Собираем название события messageId. Может быть с кавычками (несколько слов) или без (одно слово)
        if (txt.contains("\"")) {
            if (txt.indexOf("\"") < txt.indexOf("@")) { // Если кавычки появляются до параметра получателя, то название события состоит из нескольких слов
                int start = txt.indexOf("\""); // индекс первого вхождения кавычки
                int end = txt.substring(start + 1).indexOf("\"") + txt.substring(0, start + 1).length(); // Индекс второго вхождения, поскольку часть с первой кавычкой мы вырезали
                params.put("messageId", txt.substring(start + 1, end));
            } else {
                params.put("messageId", split[1]);
            }
        } else {
            params.put("messageId", split[1]);
        }

        // Собираем дату события date.
        if (txt.contains("\"")) {
            if (txt.indexOf("\"") < txt.indexOf("@")) { // Если кавычки появляются до параметра получателя, то дата идет после второй кавычки
                int start = txt.indexOf("\""); // индекс первого вхождения кавычки
                int end = txt.substring(start + 1).indexOf("\"") + txt.substring(0, start + 1).length(); // Индекс второго вхождения, поскольку часть с первой кавычкой мы вырезали
                params.put("date", txt.substring(end + 1).trim().split(" ")[0]);
            } else {
                params.put("date", split[2]);
            }
        } else {
            params.put("date", split[2]);
        }

        // Собираем получателя сообщения dstUsername.
        params.put("dstUsername", txt.substring(
                txt.indexOf("@") + 1,
                txt.substring(txt.indexOf("@")).indexOf(" ") + txt.substring(0, txt.indexOf("@")).length()
                )
        ); // имя пользователя идет со следующей позиции после '@' и до пробела

        // Собираем текст сообщения textMessage.
        if (txt.contains("\"")) {
            if (txt.lastIndexOf("\"") > txt.indexOf("@")) { // Если кавычки появляются после параметра получателя, то сообщение состоит из нескольких слов
                int end = txt.lastIndexOf("\""); // индекс последнего вхождения кавычки
                int start = txt.substring(0, end).lastIndexOf("\""); // Индекс предпоследнего вхождения, поскольку часть с последней кавычкой мы вырезали
                params.put("textMessage", txt.substring(start + 1, end));
            } else {
                params.put("textMessage", txt.substring(txt.indexOf("@")).trim().split(" ")[1]);
            }
        } else {
            params.put("textMessage", split[4]);
        }

        return params;
    }
}
