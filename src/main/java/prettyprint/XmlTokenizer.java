// XmlTokenizer.java
package prettyprint;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlTokenizer {
    private Pattern pattern = Pattern.compile(
            "(<\\?xml[^?]*\\?>)|" +        // XML декларация
                    "(<!\\[CDATA\\[.*?\\]\\]>)|" + // CDATA
                    "(<!--.*?-->)|" +              // Комментарий
                    "(</[^>]+>)|" +               // Закрывающий тег
                    "(<[^>]+/>)|" +               // Самозакрывающийся тег
                    "(<[^>]+>)|" +                // Открывающий тег
                    "([^<]+)"                     // Текст
    );

    public List<XmlToken> tokenize(String xml) {
        List<XmlToken> tokens = new ArrayList<>();
        Matcher matcher = pattern.matcher(xml);

        while (matcher.find()) {
            String content = matcher.group(0).trim();
            if (content.isEmpty()) continue;

            XmlToken.TokenType type = getTokenType(content);
            tokens.add(new XmlToken(content, type));
        }

        return tokens;
    }

    private XmlToken.TokenType getTokenType(String content) {
        if (content.startsWith("<?xml")) {
            return XmlToken.TokenType.XML_DECLARATION;
        }
        if (content.startsWith("<!--")) {
            return XmlToken.TokenType.COMMENT;
        }
        if (content.startsWith("<![CDATA[")) {
            return XmlToken.TokenType.CDATA;
        }
        if (content.startsWith("</")) {
            return XmlToken.TokenType.CLOSING_TAG;
        }
        if (content.endsWith("/>")) {
            return XmlToken.TokenType.SELF_CLOSING_TAG;
        }
        if (content.startsWith("<")) {
            return XmlToken.TokenType.OPENING_TAG;
        }
        return XmlToken.TokenType.TEXT;
    }
}