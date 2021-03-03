package edu.utec.tools.katalon.runner.common;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class MetadataHelper {

  public static ArrayList<HashMap<String, Object>> getKatalonScripts(ArrayList<File> scripts,
      String absoluteKatalonProjectPath) throws Exception {

    ArrayList<HashMap<String, Object>> katalonScripts = new ArrayList<HashMap<String, Object>>();
    for (File file : scripts) {
      String rawScript = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
      HashMap<String, Object> meta = MetadataHelper.getRawMeta(rawScript);
      if (meta == null) {
        meta = new HashMap<String, Object>();
      }
      meta.put("absolutePath", file.getAbsolutePath());
      meta.put("testCaseId", KatalonFileHelper.getTestCaseIdFromPath(absoluteKatalonProjectPath,
          file.getAbsolutePath()));
      meta.put("testCaseName", KatalonFileHelper.getTestCaseNameFromPath(file.getAbsolutePath()));
      
      if (meta.get("featureName") == null || ((String)meta.get("featureName")).isEmpty()) {
        meta.put("featureName",meta.get("testCaseId"));
      }
      
      katalonScripts.add(meta);

    }

    return katalonScripts;
  }

  public static HashMap<String, Object> getRawMeta(String rawScript) {
    String strMultiline = StringHelper.getHeadMultilineBlock(rawScript, "/*", "*/");
    return getMetadata(strMultiline);
  }

  private static String clearMetadata(String rawMeta) {
    rawMeta = rawMeta.replaceAll("^/\\*", "");
    rawMeta = rawMeta.replaceAll("\\*/", "");
    return rawMeta;
  }

  private static HashMap<String, Object> getMetadata(String rawMeta) {

    if (rawMeta == null) {
      return null;
    }
    // TODO: create default metadata if scripts does not have a valid metadata

    rawMeta = clearMetadata(rawMeta);
    HashMap<String, Object> meta = new HashMap<String, Object>();
    StringTokenizer st1 = new StringTokenizer(rawMeta, "\n");
    while (st1.hasMoreTokens()) {
      String rawLine = st1.nextToken();
      if (!rawLine.contains(":")) {
        continue;
      }
      rawLine = rawLine.replaceAll("\\s*\\*\\s*", "");
      String[] rawData = rawLine.trim().split(":");
      meta.put(rawData[0].trim(), rawData[1].trim());
    }
    return meta;
  }
}
