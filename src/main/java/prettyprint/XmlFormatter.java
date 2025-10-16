// BaseXmlFormatter.java
package prettyprint;

import java.util.List;

public abstract class XmlFormatter {
    protected int indentSpaces;
    protected XmlTokenizer tokenizer;

    public XmlFormatter() {
        this.indentSpaces = 2;
        this.tokenizer = new XmlTokenizer();
    }

    public XmlFormatter(int indentSpaces) {
        this.indentSpaces = indentSpaces;
        this.tokenizer = new XmlTokenizer();
    }

    public abstract String format(String xml);

    public String escapeText(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }


    protected String getIndent(int level) {
        return " ".repeat(level * indentSpaces);
    }

    protected boolean isNextNonEmptyTokenText(List<XmlToken> tokens, int currentIndex) {
        for (int i = currentIndex + 1; i < tokens.size(); i++) {
            XmlToken nextToken = tokens.get(i);
            if (nextToken.type == XmlToken.TokenType.TEXT) {
                String text = nextToken.content.trim();
                return !text.isEmpty(); // Возвращаем true только если текст не пустой
            } else if (nextToken.type == XmlToken.TokenType.OPENING_TAG ||
                    nextToken.type == XmlToken.TokenType.CLOSING_TAG ||
                    nextToken.type == XmlToken.TokenType.SELF_CLOSING_TAG) {
                return false;
            }
        }
        return false;
    }

}