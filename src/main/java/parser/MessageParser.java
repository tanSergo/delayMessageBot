package parser;

import java.util.Map;

public interface MessageParser {
    Map<String, String> parseParameters(String txt);
}
