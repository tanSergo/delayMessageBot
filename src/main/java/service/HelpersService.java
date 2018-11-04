package service;

import parser.MessageParser;
import parser.RegExpParser;

import java.util.Map;

public class HelpersService {

    public Map<String, String> parseParameters(String txt) {
        MessageParser parser = new RegExpParser();
        return parser.parseParameters(txt);
    }
}
