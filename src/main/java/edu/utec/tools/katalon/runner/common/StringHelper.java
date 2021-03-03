package edu.utec.tools.katalon.runner.common;

public class StringHelper {

  public static String getHeadMultilineBlock(String rawScript, String startsWith, String endsWith) {

    // must start with
    if (!rawScript.startsWith(startsWith)) {
      return null;
    }

    // must end with
    if (rawScript.indexOf(endsWith) < 0) {
      return null;
    }

    String rawPayload =
        rawScript.substring(rawScript.indexOf(startsWith), rawScript.indexOf(endsWith) + 2);
    return rawPayload;
  }

  public static String scapeBackSlashForWindowsPath(String os, String filePath) {
    if (os != null && os.startsWith("Win")) {
      return filePath.replace("\\\\", "/");
    } else {
      return filePath;
    }
  }
}
