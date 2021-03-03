package edu.utec.tools.katalon.runner.launcher.cmd;

import java.util.HashMap;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.utec.tools.common.FileHelper;
import edu.utec.tools.common.LoggerHelper;
import edu.utec.tools.katalon.runner.common.KatalonRunnerAsserts;
import edu.utec.tools.katalon.runner.exec.KatalonRunner;
import edu.utec.tools.katalon.runner.report.ReportHelper;

public class KatalonRunnerCmdEntrypoint {

  private static final Logger logger = LoggerFactory.getLogger(KatalonRunnerCmdEntrypoint.class);

  public static void main(String[] args) throws Exception {
    ArgumentsHelper argumentsHelper = new ArgumentsHelper();
    CommandLine cmd = argumentsHelper.getArguments(args);

    logger.info("Parameters:");

    logger.info(argumentsHelper.simplePrint(cmd));

    if (cmd.hasOption("debug")) {
      LoggerHelper.setDebugLevel();
    }

    String baseKatalonProjectAbsolutePath = cmd.getOptionValue("katalon_project_path");
    FileHelper.assertExist(baseKatalonProjectAbsolutePath);
    String executionType = cmd.getOptionValue("execution_type");
    KatalonRunnerAsserts.assertKatalonSutdioExecutionPropertiesExistence(
        baseKatalonProjectAbsolutePath, executionType);

    String reportType = cmd.getOptionValue("report_type");
    if (reportType == null) {
      reportType = "default";
    }
    String reportLocation = cmd.getOptionValue("report_location");
    FileHelper.createIfNotExist(reportLocation);

    String profile = cmd.getOptionValue("profile");
    if (profile == null) {
      profile = "default";
    }

    String regexFile = cmd.getOptionValue("rxf");
    String regexFolder = cmd.getOptionValue("rxd");

    if (regexFile != null && regexFile.isEmpty()) {
      regexFile = null;
    }

    if (regexFolder != null && regexFolder.isEmpty()) {
      regexFolder = null;
    }

    if (regexFile != null && regexFolder != null) {
      throw new Exception("Just one regex is allowed: File or folder.");
    }

    KatalonRunnerAsserts.assertKatalonSutdioProfileExistence(baseKatalonProjectAbsolutePath,
        profile);

    KatalonRunner katalonRunner = new KatalonRunner();
    HashMap<String, Object> reportStats = katalonRunner.execute(baseKatalonProjectAbsolutePath,
        executionType, profile, regexFile, regexFolder);

    String status = (String) reportStats.get("status");

    if (reportType.contentEquals("default")) {
      ReportHelper.createDefaultReport(reportStats, reportLocation, "katalon");
    } else if (reportType.contentEquals("html-compact")) {
      ReportHelper.createCompactHtmlReport(reportStats, reportLocation,
          "/report/html/compact/index.html", "katalon");
    } else if (reportType.contentEquals("html-uncompact")) {
      ReportHelper.createUnCompactHtmlReport(reportStats, reportLocation, "/report/html/uncompact",
          "katalon");
    }

    logger.info("By JRichardsz");
    if (status.contentEquals("success")) {
      logger.info("Test cases was completed successfully.");
      System.exit(0);
    } else {
      logger.error("Test cases ended with error. Check logs and reports.");
      System.exit(1);
    }

  }

}
