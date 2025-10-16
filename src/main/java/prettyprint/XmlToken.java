package prettyprint;

public class XmlToken {
    public String content;
    public TokenType type;

    public XmlToken(String content, TokenType type) {
        this.content = content;
        this.type = type;
    }

    public boolean isEmpty() {
        return content.trim().isEmpty();
    }

    public enum TokenType {
        XML_DECLARATION,
        COMMENT,
        CDATA,
        OPENING_TAG,
        CLOSING_TAG,
        SELF_CLOSING_TAG,
        TEXT
    }
}