package prettyprint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLPrettyPrinter {
    private int indentSpaces;

    public XMLPrettyPrinter() {
        this.indentSpaces = 2;
    }
    public XMLPrettyPrinter(int indentSpaces) {
        this.indentSpaces = indentSpaces;
    }

    public String format(String xml) {
        Pattern pattern = Pattern.compile(
                "(<\\?xml[^?]*\\?>)|" +        // XML декларация
                        "(<!\\[CDATA\\[.*?\\]\\]>)|" + // CDATA
                        "(<!--.*?-->)|" +              // Комментарий
                        "(</[^>]+>)|" +               // Закрывающий тег
                        "(<[^>]+/>)|" +               // Самозакрывающийся тег
                        "(<[^>]+>)|" +                // Открывающий тег
                        "([^<]+)"                     // Текст
        );

        Matcher matcher = pattern.matcher(xml);
        StringBuilder result = new StringBuilder();
        int indentLevel = 0;
        boolean expectText = false;

        while (matcher.find()) {
            String token = matcher.group(0);

            if (token.startsWith("<?xml")) {
                result.append(token).append("\n");
            } else if (token.startsWith("<!--")) {
                result.append(getIndent(indentLevel)).append(token).append("\n");
            } else if (token.startsWith("<![CDATA[")) {
                result.append(getIndent(indentLevel)).append(token).append("\n");
            } else if (token.startsWith("</")) {
                indentLevel--;
                result.append(getIndent(indentLevel)).append(token).append("\n");
            } else if (token.endsWith("/>")) {
                result.append(getIndent(indentLevel)).append(token).append("\n");
            } else if (token.startsWith("<") && !token.startsWith("</")) {
                result.append(getIndent(indentLevel)).append(token);
                indentLevel++;
                String remaining = xml.substring(matcher.end());
                if (!remaining.trim().startsWith("<") && !remaining.trim().isEmpty()) {
                    expectText = true;
                }
                else {
                    result.append("\n");
                }
            } else {
                if (expectText) {
                    result.append(token);
                    expectText = false;
                }
            }

        }

        return result.toString();
    }

    private String getIndent(int level) {
        return " ".repeat(level * indentSpaces);
    }

}
