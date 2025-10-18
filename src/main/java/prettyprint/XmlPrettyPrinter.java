package prettyprint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class XmlPrettyPrinter {

    public static String formatString(String xml) {
        return formatString(xml, false, 2);
    }

    public static String formatString(String xml, boolean autoCloseTags) {
        return formatString(xml, autoCloseTags, 2);
    }

    public static String formatString(String xml, boolean autoCloseTags, int indentSpaces) {
        if (autoCloseTags) {
            RepairXmlFormatter formatter = new RepairXmlFormatter(indentSpaces);
            return formatter.format(xml);
        } else {
            SimpleXmlFormatter formatter = new SimpleXmlFormatter(indentSpaces);
            return formatter.format(xml);
        }
    }

    public static String formatFile(String filePath) throws IOException {
        return formatFile(filePath, false, 2);
    }

    public static String formatFile(String filePath, boolean autoCloseTags) throws IOException {
        return formatFile(filePath, autoCloseTags, 2);
    }

    public static String formatFile(String filePath, boolean autoCloseTags, int indentSpaces) throws IOException {
        String xmlContent = Files.readString(Path.of(filePath));
        return formatString(xmlContent, autoCloseTags, indentSpaces);
    }

    public static void formatFileInPlace(String filePath, boolean autoCloseTags, int indentSpaces) throws IOException {
        String xmlContent = Files.readString(Path.of(filePath));
        String formattedXml = formatString(xmlContent, autoCloseTags, indentSpaces);
        Files.writeString(Path.of(filePath), formattedXml);
    }

    public static void formatFileToFile(String inputFilePath, String outputFilePath) throws IOException {
        formatFileToFile(inputFilePath, outputFilePath, false, 2);
    }

    public static void formatFileToFile(String inputFilePath, String outputFilePath, boolean autoCloseTags) throws IOException {
        formatFileToFile(inputFilePath, outputFilePath, autoCloseTags, 2);
    }

    public static void formatFileToFile(String inputFilePath, String outputFilePath, boolean autoCloseTags, int indentSpaces) throws IOException {
        String xmlContent = Files.readString(Path.of(inputFilePath));
        String formattedXml = formatString(xmlContent, autoCloseTags, indentSpaces);
        Files.writeString(Path.of(outputFilePath), formattedXml);
    }


}