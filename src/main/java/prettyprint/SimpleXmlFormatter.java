package prettyprint;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleXmlFormatter extends XmlFormatter {

    private int indentSpaces;

    public SimpleXmlFormatter() {
        super();
    }
    public SimpleXmlFormatter(int indentSpaces) {
        super(indentSpaces);
    }

    @Override
    public String format(String xml) {
        List<XmlToken> tokens = tokenizer.tokenize(xml);
        StringBuilder result = new StringBuilder();
        int indentLevel = 0;
        boolean hasInlineText = false;

        for (int i = 0; i < tokens.size(); i++) {
            XmlToken token = tokens.get(i);
            switch (token.type) {
                case XML_DECLARATION:
                    result.append(token.content).append("\n");
                    break;

                case COMMENT:
                case CDATA:
                    result.append(getIndent(indentLevel)).append(token.content).append("\n");
                    break;

                case CLOSING_TAG:
                    indentLevel--;
                    if (hasInlineText) {
                        result.append(token.content).append("\n");
                        hasInlineText = false;
                    } else {
                        result.append(getIndent(indentLevel)).append(token.content).append("\n");
                    }
                    break;

                case SELF_CLOSING_TAG:
                    result.append(getIndent(indentLevel)).append(token.content).append("\n");
                    break;

                case OPENING_TAG:
                    result.append(getIndent(indentLevel)).append(token.content);
                    indentLevel++;

                    boolean nextIsText = isNextNonEmptyTokenText(tokens, i);
                    if (nextIsText) {
                        hasInlineText = true;
                    } else {
                        result.append("\n");
                    }
                    break;

                case TEXT:
                    String escapedText = escapeText(token.content.trim());
                    if (!escapedText.isEmpty()) {
                        if (hasInlineText) {
                            result.append(escapedText);
                            if (!isNextTokenClose(tokens, i)){
                                result.append(getIndent(indentLevel)).append("\n");
                            }
                        } else {
                            result.append(getIndent(indentLevel)).append(escapedText).append("\n");
                        }
                    }
                    break;
            }
        }

        return result.toString().trim();
    }

    private boolean isNextTokenClose(List<XmlToken> tokens, int currentIndex) {
        XmlToken nextToken = tokens.get(currentIndex + 1);
        if (nextToken.type == XmlToken.TokenType.CLOSING_TAG) {
            return true;
        }
        return false;
    }
}