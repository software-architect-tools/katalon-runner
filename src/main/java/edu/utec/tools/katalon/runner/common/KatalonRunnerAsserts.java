package edu.utec.tools.katalon.runner.common;

import java.io.File;

public class KatalonRunnerAsserts {

  public static void assertKatalonSutdioExecutionPropertiesExistence(
      String baseKatalonProjectAbsolutePath, String executionType) throws Exception {
    String path = String.format("%s/Executions/%s/execution.properties",
        baseKatalonProjectAbsolutePath, executionType);
    if (!new File(path).exists()) {
      throw new Exception(String.format(
          "Execution properties Executions/%s/execution.properties does not exist."
              + " Check the Execution folder in the Katalon Project directory: %s",
          executionType, baseKatalonProjectAbsolutePath));
    }
  }

  public static void assertKatalonSutdioProfileExistence(String baseKatalonProjectAbsolutePath,
      String profile) throws Exception {
    String path = String.format("%s/Profiles/%s.glbl", baseKatalonProjectAbsolutePath, profile);
    if (!new File(path).exists()) {
      throw new Exception(String.format(
          "Profile %s.glbl does not exist."
              + " Check the Profiles folder in the Katalon Project directory: %s",
          profile, baseKatalonProjectAbsolutePath));
    }
  }

}
