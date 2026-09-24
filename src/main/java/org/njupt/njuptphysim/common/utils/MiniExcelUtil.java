package org.njupt.njuptphysim.common.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 轻量 xlsx(.xlsx) 读取工具（仅依赖 JDK，不引入 POI）。
 *
 * <p>满足批量导入场景：读取第一个工作表，返回按行拆分的字符串矩阵。
 * 支持：共享字符串(t="s")、内联字符串(t="inlineStr")、布尔、数值单元格；
 * 单元格留空时以空串占位。日期等特殊格式会被读成 Excel 序列数值文本，
 * 因此导入模板中请统一使用文本/数字列（如年份用 2026、日期用 2026-09-16 文本）。</p>
 */
public class MiniExcelUtil {

    private MiniExcelUtil() {
    }

    /**
     * 读取 xlsx 第一个工作表的全部行
     * @param bytes xlsx 文件字节
     * @return 行列表，每行为单元格字符串列表（列号对齐，空单元格为 ""）
     */
    public static List<List<String>> read(byte[] bytes) throws IOException {
        Map<Integer, String> sharedStrings = new HashMap<>();
        String firstSheetName = null;
        byte[] firstSheetBytes = null;
        String workbookXml = null;
        String workbookRelsXml = null;

        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8);
        ZipEntry entry;
        while ((entry = zip.getNextEntry()) != null) {
            String name = entry.getName();
            switch (name) {
                case "xl/sharedStrings.xml":
                    sharedStrings = parseSharedStrings(new String(readAll(zip), StandardCharsets.UTF_8));
                    break;
                case "xl/workbook.xml":
                    workbookXml = new String(readAll(zip), StandardCharsets.UTF_8);
                    break;
                case "xl/_rels/workbook.xml.rels":
                    workbookRelsXml = new String(readAll(zip), StandardCharsets.UTF_8);
                    break;
                default:
                    if (name.startsWith("xl/worksheets/") && name.endsWith(".xml")) {
                        //记录字典序第一个工作表，作为解析不到 workbook 关系时的兜底
                        if (firstSheetBytes == null || name.compareTo(firstSheetName) < 0) {
                            firstSheetBytes = readAll(zip);
                            firstSheetName = name;
                        }
                    }
            }
        }
        zip.close();

        //按 workbook.xml 第一个 sheet 的关系定位真正的工作表文件
        String relTarget = resolveFirstSheetTarget(workbookXml, workbookRelsXml);
        if (relTarget != null) {
            String sheetPath = relTarget.startsWith("/") ? relTarget.substring(1) : "xl/" + relTarget;
            byte[] located = findEntry(bytes, sheetPath);
            if (located != null) {
                firstSheetBytes = located;
            }
        }
        if (firstSheetBytes == null) {
            throw new IOException("xlsx 中未找到工作表");
        }
        return parseSheet(new String(firstSheetBytes, StandardCharsets.UTF_8), sharedStrings);
    }

    /** 按 workbook.xml 第一个 sheet 的 r:id，从 rels 中解析出工作表文件路径 */
    private static String resolveFirstSheetTarget(String workbookXml, String relsXml) {
        try {
            if (workbookXml == null || relsXml == null) {
                return null;
            }
            Document wb = newDocumentBuilder().parse(new InputSource(new StringReader(workbookXml)));
            NodeList sheets = wb.getElementsByTagName("sheet");
            if (sheets.getLength() == 0) {
                return null;
            }
            Element sheet = (Element) sheets.item(0);
            String rid = sheet.getAttribute("r:id");
            if (rid == null || rid.isBlank()) {
                return null;
            }
            Document rels = newDocumentBuilder().parse(new InputSource(new StringReader(relsXml)));
            NodeList relNodes = rels.getElementsByTagName("Relationship");
            for (int i = 0; i < relNodes.getLength(); i++) {
                Element rel = (Element) relNodes.item(i);
                if (rid.equals(rel.getAttribute("Id")) && !rel.getAttribute("Target").isBlank()) {
                    return rel.getAttribute("Target");
                }
            }
        } catch (Exception ignored) {
            //解析失败时走字典序兜底
        }
        return null;
    }

    private static byte[] findEntry(byte[] bytes, String entryName) throws IOException {
        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8);
        try {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().equals(entryName)) {
                    return readAll(zip);
                }
            }
        } finally {
            zip.close();
        }
        return null;
    }

    /** 解析 sharedStrings.xml：<si> 下所有 <t> 文本拼接（兼容富文本 run） */
    private static Map<Integer, String> parseSharedStrings(String xml) throws IOException {
        Map<Integer, String> result = new HashMap<>();
        try {
            Document doc = newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            NodeList siList = doc.getElementsByTagName("si");
            for (int i = 0; i < siList.getLength(); i++) {
                StringBuilder sb = new StringBuilder();
                NodeList ts = ((Element) siList.item(i)).getElementsByTagName("t");
                for (int j = 0; j < ts.getLength(); j++) {
                    sb.append(ts.item(j).getTextContent());
                }
                result.put(i, sb.toString());
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("sharedStrings.xml 解析失败: " + e.getMessage(), e);
        }
        return result;
    }

    /** 解析工作表：按行输出，列号依据单元格 r 属性（如 B3 → 第2列）对齐 */
    private static List<List<String>> parseSheet(String xml, Map<Integer, String> sharedStrings) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        try {
            Document doc = newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            NodeList rowList = doc.getElementsByTagName("row");
            for (int i = 0; i < rowList.getLength(); i++) {
                Element rowEl = (Element) rowList.item(i);
                NodeList cellList = rowEl.getElementsByTagName("c");
                List<String> row = new ArrayList<>();
                for (int j = 0; j < cellList.getLength(); j++) {
                    Element cell = (Element) cellList.item(j);
                    int colIndex = colLettersToIndex(cell.getAttribute("r"));
                    while (row.size() < colIndex) {
                        row.add("");
                    }
                    row.add(cellValue(cell, sharedStrings));
                }
                rows.add(row);
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("工作表解析失败: " + e.getMessage(), e);
        }
        return rows;
    }

    private static String cellValue(Element cell, Map<Integer, String> sharedStrings) {
        String type = cell.getAttribute("t");
        if ("inlineStr".equals(type)) {
            NodeList ts = cell.getElementsByTagName("t");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ts.getLength(); i++) {
                sb.append(ts.item(i).getTextContent());
            }
            return sb.toString();
        }
        NodeList vs = cell.getElementsByTagName("v");
        if (vs.getLength() == 0) {
            return "";
        }
        String v = vs.item(0).getTextContent();
        if ("s".equals(type)) {
            try {
                return sharedStrings.getOrDefault(Integer.parseInt(v.trim()), "");
            } catch (NumberFormatException e) {
                return "";
            }
        }
        if ("b".equals(type)) {
            return "1".equals(v.trim()) ? "TRUE" : "FALSE";
        }
        return v == null ? "" : v.trim();
    }

    /** 列字母转 0 基下标：A→0, B→1, ... AA→26 */
    private static int colLettersToIndex(String cellRef) {
        if (cellRef == null || cellRef.isEmpty()) {
            return 0;
        }
        int index = 0;
        for (char c : cellRef.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                index = index * 26 + (c - 'A' + 1);
            } else if (c >= 'a' && c <= 'z') {
                index = index * 26 + (c - 'a' + 1);
            } else {
                break;
            }
        }
        return index - 1;
    }

    private static javax.xml.parsers.DocumentBuilder newDocumentBuilder() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //禁用 DTD 防止 XXE
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        return dbf.newDocumentBuilder();
    }

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
        return out.toByteArray();
    }
}
