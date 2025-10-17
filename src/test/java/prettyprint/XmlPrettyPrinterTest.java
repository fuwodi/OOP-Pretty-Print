package prettyprint;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class XmlPrettyPrinterTest {

    // Тестирует базовое форматирование простой XML структуры с правильными отступами
    @Test
    public void testFormatSimpleXml() {
        String xml = "<root><child>text</child></root>";
        String expected = "<root>\n  <child>text</child>\n</root>";

        String result = XmlPrettyPrinter.formatString(xml);
        assertEquals(expected, result);
    }

    // Тестирует использование пользовательского размера отступа (4 пробела вместо 2)
    @Test
    public void testFormatWithIndentation() {
        String xml = "<a><b><c>text</c></b></a>";
        String result = XmlPrettyPrinter.formatString(xml, false, 4);

        assertTrue(result.contains("    <b>"), "Should use 4-space indentation");
    }

    // Тестирует функцию автоматического закрытия незакрытых тегов в некорректном XML
    @Test
    public void testFormatWithAutoClose() {
        String invalidXml = "<root><div><p>hello</div>";
        String result = XmlPrettyPrinter.formatString(invalidXml, true);

        // Проверяем, что теги корректно закрыты
        assertTrue(result.contains("</p>"));
        assertTrue(result.contains("</div>"));
        assertTrue(result.contains("</root>"));
    }

    // Тестирует корректную обработку XML декларации и комментариев при форматировании
    @Test
    public void testFormatWithCommentsAndDeclaration() {
        String xml = "<?xml version=\"1.0\"?><!-- comment --><root/>";
        String result = XmlPrettyPrinter.formatString(xml);

        assertTrue(result.contains("<?xml version=\"1.0\"?>"));
        assertTrue(result.contains("<!-- comment -->"));
        assertTrue(result.contains("<root/>"));
    }

    // Тестирует форматирование сложной вложенной структуры с несколькими уровнями
    @Test
    public void testComplexNestedStructure() {
        String xml = "<library><book><title>XML Guide</title><author><name>John</name><email>john@example.com</email></author></book></library>";
        String result = XmlPrettyPrinter.formatString(xml);

        assertTrue(result.contains("  <book>"));
        assertTrue(result.contains("    <title>XML Guide</title>"));
        assertTrue(result.contains("    <author>"));
        assertTrue(result.contains("      <name>John</name>"));
        assertTrue(result.contains("      <email>john@example.com</email>"));
    }

    // Тестирует корректное форматирование самозакрывающихся тегов
    @Test
    public void testSelfClosingTags() {
        String xml = "<root><br/><img src=\"test.jpg\"/></root>";
        String result = XmlPrettyPrinter.formatString(xml);

        assertTrue(result.contains("<br/>"));
        assertTrue(result.contains("<img src=\"test.jpg\"/>"));
        assertTrue(result.contains("  <br/>"));
    }

    // Тестирует сохранение содержимого CDATA секций без изменений
    @Test
    public void testCDATAHandling() {
        String xml = "<root><![CDATA[<some>unescaped content</some>]]></root>";
        String result = XmlPrettyPrinter.formatString(xml);

        assertTrue(result.contains("<![CDATA[<some>unescaped content</some>]]>"));
    }

    // Тестирует экранирование специальных XML символов в текстовом содержимом
    @Test
    public void testSpecialCharactersEscaping() {
        String xml = "<root>Text & \" '</root>";
        String result = XmlPrettyPrinter.formatString(xml);

        assertTrue(result.contains("&amp;"));
        assertTrue(result.contains("&quot;"));
        assertTrue(result.contains("&apos;"));
    }

    // Тестирует форматирование пустых тегов (без содержимого)
    @Test
    public void testEmptyTags() {
        String xml = "<root></root>";
        String expected = "<root>\n</root>";

        String result = XmlPrettyPrinter.formatString(xml);
        assertEquals(expected, result);
    }

    // Тестирует функцию авто-закрытия в сложном сценарии со смешанным содержимым
    @Test
    public void testMixedContentWithAutoClose() {
        String invalidXml = "<html><body><div>content<p>paragraph<span>text</span></div></body>";
        String result = XmlPrettyPrinter.formatString(invalidXml, true);

        // Проверяем корректное закрытие всех тегов
        assertTrue(result.contains("</span>"));
        assertTrue(result.contains("</p>"));
        assertTrue(result.contains("</div>"));
        assertTrue(result.contains("</body>"));
        assertTrue(result.contains("</html>"));
    }

    // Тестирует совместимость вызовов методов с разным количеством параметров
    @Test
    public void testDefaultParameters() {
        String xml = "<root><child>text</child></root>";

        // Тестируем вызовы с разным количеством параметров
        String result1 = XmlPrettyPrinter.formatString(xml);
        String result2 = XmlPrettyPrinter.formatString(xml, false);
        String result3 = XmlPrettyPrinter.formatString(xml, false, 2);

        assertEquals(result1, result2);
        assertEquals(result2, result3);
    }

    // Тестирует форматирование XML из файла (функционал чтения файлов)
    @Test
    public void testFileFormatting() throws IOException {
        // Создаем временный файл для тестирования
        String xmlContent = "<root><item>test</item></root>";
        Path tempFile = Files.createTempFile("test", ".xml");
        Files.writeString(tempFile, xmlContent);

        try {
            String result = XmlPrettyPrinter.formatFile(tempFile.toString());
            assertTrue(result.contains("<root>"));
            assertTrue(result.contains("<item>test</item>"));
            assertTrue(result.contains("</root>"));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    // Тестирует форматирование файла с включенной функцией авто-закрытия тегов
    @Test
    public void testFileFormattingWithAutoClose() throws IOException {
        String xmlContent = "<root><unclosed>text";
        Path tempFile = Files.createTempFile("test", ".xml");
        Files.writeString(tempFile, xmlContent);

        try {
            String result = XmlPrettyPrinter.formatFile(tempFile.toString(), true);
            assertTrue(result.contains("</unclosed>"));
            assertTrue(result.contains("</root>"));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    // Тестирует сложный сценарий авто-закрытия с перекрывающимися тегами
    @Test
    public void testComplexAutoCloseScenario() {
        // Тест из вашего примера
        String invalidXml = "<person> <name>Alice</name> <age>30 <city>Wonder land</city> </person> </age>";
        String result = XmlPrettyPrinter.formatString(invalidXml, true);

        // Ожидаем корректную структуру после авто-закрытия
        assertTrue(result.contains("<name>Alice</name>"));
        assertTrue(result.contains("<age>30</age>"));
        assertTrue(result.contains("<city>Wonder land</city>"));
        assertTrue(result.contains("</person>"));
    }

    // Тестирует производительность и корректность форматирования для очень глубокой вложенности
    @Test
    public void testDeeplyNestedStructure() {
        StringBuilder xml = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            xml.append("<level").append(i).append(">");
        }
        xml.append("content");
        for (int i = 9; i >= 0; i--) {
            xml.append("</level").append(i).append(">");
        }

        String result = XmlPrettyPrinter.formatString(xml.toString());

        // Проверяем, что форматирование работает для глубоко вложенных структур
        for (int i = 0; i < 10; i++) {
            String indent = " ".repeat(i * 2);
            assertTrue(result.contains(indent + "<level" + i + ">"));
        }
    }
}