package prettyprint;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class RepairXmlFormatter extends XmlFormatter {
    private Deque<String> tagDeque;
    private Deque<Boolean> inlineTagDeque;

    public RepairXmlFormatter() {
        super();
        this.tagDeque = new ArrayDeque<>();
        this.inlineTagDeque = new ArrayDeque<>();
    }

    public RepairXmlFormatter(int indentSpaces) {
        super(indentSpaces);
        this.tagDeque = new ArrayDeque<>();
        this.inlineTagDeque = new ArrayDeque<>();
    }

    @Override
    public String format(String xml) {
        tagDeque.clear();
        inlineTagDeque.clear();
        List<XmlToken> tokens = tokenizer.tokenize(xml);
        StringBuilder result = new StringBuilder();
        boolean hasInlineText = false;

        for (int i = 0; i < tokens.size(); i++) {
            XmlToken token = tokens.get(i);
            int indentLevel = tagDeque.size();

            switch (token.type) {
                case XML_DECLARATION:
                    result.append(token.content).append("\n");
                    break;

                case COMMENT:
                case CDATA:
                    result.append(getIndent(indentLevel)).append(token.content).append("\n");
                    break;

                case CLOSING_TAG:
                    processClosingTag(token, result);
                    break;

                case SELF_CLOSING_TAG:
                    result.append(getIndent(indentLevel)).append(token.content).append("\n");
                    break;

                case OPENING_TAG:
                    hasInlineText = processOpeningTag(token, result, indentLevel, tokens, i);
                    break;

                case TEXT:
                    String escapedText = escapeText(token.content.trim());
                    if (!escapedText.isEmpty()) {
                        if (hasInlineText) {
                            result.append(escapedText);
                            closeInlineTagIfNeeded(result);
                        } else {
                            result.append(getIndent(indentLevel)).append(escapedText).append("\n");
                        }
                    }
                    break;
            }
        }

        closeAllRemainingTags(result);
        return result.toString().trim();
    }

    private boolean processOpeningTag(XmlToken openingToken, StringBuilder result, int indentLevel, List<XmlToken> tokens, int currentIndex) {
        if (result.length() > 0 && result.charAt(result.length() - 1) != '\n') {
            result.append("\n");
        }

        result.append(getIndent(indentLevel)).append(openingToken.content);

        String openTagName = getTagName(openingToken.content);
        tagDeque.offerLast(openTagName);

        boolean hasInlineText = isNextNonEmptyTokenText(tokens, currentIndex);
        inlineTagDeque.offerLast(hasInlineText);

        if (!hasInlineText) {
            result.append("\n");
        }

        return hasInlineText;
    }

    private void closeInlineTagIfNeeded(StringBuilder result) {
        if (!inlineTagDeque.isEmpty() && inlineTagDeque.peekLast() != null && inlineTagDeque.peekLast()) {
            inlineTagDeque.pollLast();
            if (!tagDeque.isEmpty()) {
                String tagToClose = tagDeque.pollLast();
                result.append("</").append(tagToClose).append(">\n");
            }
        }
    }

    private void processClosingTag(XmlToken closingToken, StringBuilder result) {
        String closingTagName = getTagName(closingToken.content);

        if (tagDeque.isEmpty()) {
            return;
        }

        if (!inlineTagDeque.isEmpty() && inlineTagDeque.peekLast() != null &&
                inlineTagDeque.peekLast() && tagDeque.peekLast().equals(closingTagName)) {
            inlineTagDeque.pollLast();
            tagDeque.pollLast();
            return;
        }

        if (tagDeque.peekLast().equals(closingTagName)) {
            if (!inlineTagDeque.isEmpty()) {
                inlineTagDeque.pollLast();
            }
            tagDeque.pollLast();
            result.append(getIndent(tagDeque.size())).append(closingToken.content).append("\n");
        } else if (tagDeque.contains(closingTagName)) {
            closeTagsUntil(result, closingTagName);

            if (!tagDeque.isEmpty() && tagDeque.peekLast().equals(closingTagName)) {
                if (!inlineTagDeque.isEmpty()) {
                    inlineTagDeque.pollLast();
                }
                tagDeque.pollLast();
                result.append(getIndent(tagDeque.size())).append(closingToken.content).append("\n");
            }
        }
    }

    private void closeTagsUntil(StringBuilder result, String targetTag) {
        while (!tagDeque.isEmpty() && !tagDeque.peekLast().equals(targetTag)) {
            String tagToClose = tagDeque.pollLast();
            if (!inlineTagDeque.isEmpty()) {
                inlineTagDeque.pollLast();
            }
            result.append(getIndent(tagDeque.size())).append("</").append(tagToClose).append(">\n");
        }
    }

    private void closeAllRemainingTags(StringBuilder result) {
        while (!tagDeque.isEmpty()) {
            String tagToClose = tagDeque.pollLast();
            if (!inlineTagDeque.isEmpty()) {
                inlineTagDeque.pollLast();
            }
            result.append(getIndent(tagDeque.size())).append("</").append(tagToClose).append(">\n");
        }
    }



    private String getTagName(String tag) {
        return tag.replaceAll("[</>]", "").split("\\s+")[0].trim();
    }

}