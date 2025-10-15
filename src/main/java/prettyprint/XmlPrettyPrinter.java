package prettyprint;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlPrettyPrinter {
    private int indentSpaces;

    public XmlPrettyPrinter() {
        this.indentSpaces = 2;
    }

    public XmlPrettyPrinter(int indentSpaces) {
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
        Stack<String> tagStack = new Stack<>();
        int indentLevel = 0;

        while (matcher.find()) {
            String token = matcher.group(0);

            if (token.startsWith("<?xml")) {
                result.append(token).append("\n");
            } else if (token.startsWith("<!--") || token.startsWith("<![CDATA[")) {
                result.append(getIndent(indentLevel)).append(token).append("\n");
            } else if (token.startsWith("</")) {

                handleClosingTag(token, result, tagStack);
                indentLevel = tagStack.size(); // Обновляем отступ
            } else if (token.endsWith("/>")) {
                result.append(getIndent(indentLevel)).append(token).append("\n");
            } else if (token.startsWith("<") && !token.startsWith("</")) {
                result.append(getIndent(indentLevel)).append(token).append("\n");
                String openTagName = getTagName(token);
                tagStack.push(openTagName);
                indentLevel = tagStack.size();
            } else {
                // ТЕКСТ
                String text = token.trim();
                if (!text.isEmpty()) {
                    result.append(getIndent(indentLevel)).append(text).append("\n");
                }
            }
        }

        closeAllRemainingTags(result, tagStack);

        return result.toString();
    }

    private void handleClosingTag(String closingTag, StringBuilder result,
                                  Stack<String> tagStack) {
        String closingTagName = getTagName(closingTag);

        if (!tagStack.isEmpty() && tagStack.peek().equals(closingTagName)) {
            tagStack.pop();
            result.append(getIndent(tagStack.size())).append(closingTag).append("\n");
        } else if (tagStack.contains(closingTagName)) {
            closeTagsUntil(result, tagStack, closingTagName);

            if (!tagStack.isEmpty() && tagStack.peek().equals(closingTagName)) {
                tagStack.pop();
                result.append(getIndent(tagStack.size())).append(closingTag).append("\n");
            }
        } /*else {

        }*/
    }

    private void closeTagsUntil(StringBuilder result, Stack<String> tagStack, String targetTag) {
        // Закрываем все теги до целевого (не включая его)
        while (!tagStack.isEmpty() && !tagStack.peek().equals(targetTag)) {
            String tagToClose = tagStack.pop();
            result.append(getIndent(tagStack.size())).append("</").append(tagToClose).append(">\n");
        }
    }

    private void closeAllRemainingTags(StringBuilder result, Stack<String> tagStack) {
        while (!tagStack.isEmpty()) {
            String tagToClose = tagStack.pop();
            result.append(getIndent(tagStack.size())).append("</").append(tagToClose).append(">\n");
        }
    }

    private String getIndent(int level) {
        return " ".repeat(level * indentSpaces);
    }

    private String getTagName(String tag) {
        return tag.replaceAll("[</>]", "").split("\\s+")[0].trim();
    }
}