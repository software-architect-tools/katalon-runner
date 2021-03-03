package edu.utec.tools.katalon.runner.common;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KatalonFileHelper {

  private static final Logger logger = LoggerFactory.getLogger(KatalonFileHelper.class);

  /*
   * testCaseId for Katalon is the path from Scripts until name of groovy script
   */
  public static String getTestCaseIdFromPath(String absoluteKatalonProjectPath,
      String absoluteScriptPath) {

    String parentFolder = new File(absoluteScriptPath).getParentFile().getAbsolutePath();
    logger.debug("obtaining testCaseId replacing parent folder by scripts folder");
    logger.debug("katalon project path: " + absoluteKatalonProjectPath);
    logger.debug("katalon script path: " + absoluteScriptPath);
    logger.debug("parent folder: " + parentFolder);
    String absoluteScriptsFolder = new File(absoluteKatalonProjectPath).getAbsolutePath() + File.separator
        + "Scripts" + File.separator;
    logger.debug("scripts folder: " + absoluteScriptsFolder);
    String extractedTestCaseId = parentFolder.replace(absoluteScriptsFolder, "");
    logger.debug("testCaseId: " + extractedTestCaseId);
    return extractedTestCaseId;

    // return new File(absoluteScriptPath).getParentFile().getAbsolutePath()
    // .replace(absoluteKatalonProjectPath + File.separator + "Scripts" + File.separator, "");
  }

  /*
   * testCaseId for Katalon is the path from Scripts until name of groovy script
   */
  public static String getTestCaseNameFromPath(String absoluteScriptPath) {
    return new File(absoluteScriptPath).getParentFile().getName();
  }
}
