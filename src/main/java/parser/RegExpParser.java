package parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpParser implements MessageParser {
    @Override
    public Map<String, String> parseParameters(String txt) {
        Map<String, String> params = new HashMap<>();
        // /new "Mom's Birthday" 2018-10-31T22:31 @reinhagenau "Happy Birthday!"
        Pattern cmd = Pattern.compile("(\\G/[shnue][a-z]*)\\s*");
        Matcher m = cmd.matcher(txt.trim());
        String command = "undefined";
        if (m.find())
            command = m.group(1);
        // Проверяем, пришла ли команда на создание нового события
        if (command.equals("/new") || command.equals("/update")) {
            Pattern p = Pattern.compile("" +
                    "(\\G/[shnue][a-z]*)\\s*" +
                    "(.*)\\s*" +
                    "(\\d{4}?-\\d{2}?-\\d{2}?T\\d{2}?:\\d{2}?)\\s*" +
                    "@(\\w*)\\s*" +
                    "(.*)\\s*" +
                    "");
            Matcher matcher = p.matcher(txt.trim());
            if (matcher.find()) {
                params.put("command", matcher.group(1));
                params.put("messageId", matcher.group(2).trim().replaceAll("\"", ""));
                params.put("date", matcher.group(3));
                params.put("dstUsername", matcher.group(4));
                params.put("textMessage", matcher.group(5).trim().replaceAll("\"", ""));
//            System.out.println(str);
            }
        }
        else {
            params.put("command", command);
        }
        return params;
    }
}
