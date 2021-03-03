package edu.utec.tools.katalon.runner.report;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.utec.tools.common.FileHelper;
import org.codehaus.jackson.map.ObjectMapper;

public class ReportHelper {

  private static final Logger logger = LoggerFactory.getLogger(ReportHelper.class);

  public static void createDefaultReport(HashMap<String, Object> report, String reportLocation,
      String prefix) throws Exception {
    String reportAbsoluthePath = reportLocation + File.separator + prefix + "_report.json";
    try {
      FileHelper.mapToJsonFile(report, reportAbsoluthePath);
      logger.debug("Report was created: " + reportAbsoluthePath);
    } catch (Exception e) {
      throw new Exception("Failed to create default report", e);
    }
  }

  public static void createCompactHtmlReport(HashMap<String, Object> report, String reportLocation,
      String reportTemplateLocation, String type) throws Exception {
    String reportTemplate = FileHelper.getFileAsStringFromClasspath(reportTemplateLocation);
    ObjectMapper objectMapper = new ObjectMapper();

    String finalReport = reportTemplate.replace("%s",
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(report));
    String reportAbsoluthePath = reportLocation + File.separator + type + "_report.html";
    FileOutputStream out = new FileOutputStream(reportAbsoluthePath);
    out.write(finalReport.getBytes());
    out.close();
    logger.info("Report was created: " + reportAbsoluthePath);
  }

  public static void createUnCompactHtmlReport(HashMap<String, Object> report,
      String reportLocation, String reportInternalAssertsLocation, String type) throws Exception {

    String indexHtml = FileHelper.getFileAsStringFromClasspath(
        reportInternalAssertsLocation + File.separator + "index.html");
    String chartJs = FileHelper
        .getFileAsStringFromClasspath(reportInternalAssertsLocation + File.separator + "Chart.js");
    String styleCss = FileHelper
        .getFileAsStringFromClasspath(reportInternalAssertsLocation + File.separator + "style.css");
    String jsTemplate = FileHelper
        .getFileAsStringFromClasspath(reportInternalAssertsLocation + File.separator + "main.js");

    ObjectMapper objectMapper = new ObjectMapper();

    String mainJs = jsTemplate.replace("%s",
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(report));

    String reportAbsoluthePath = reportLocation + File.separator + type + "_report.html";

    Files.write(Paths.get(reportAbsoluthePath), indexHtml.getBytes());
    Files.write(Paths.get(reportLocation + File.separator + "Chart.js"), chartJs.getBytes());
    Files.write(Paths.get(reportLocation + File.separator + "style.css"), styleCss.getBytes());
    Files.write(Paths.get(reportLocation + File.separator + "main.js"), mainJs.getBytes());

    logger.info("Report was created: " + reportAbsoluthePath);
  }
}
