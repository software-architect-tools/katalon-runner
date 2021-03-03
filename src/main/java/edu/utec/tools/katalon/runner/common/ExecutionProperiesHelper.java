package edu.utec.tools.katalon.runner.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ExecutionProperiesHelper {

  public static void cloneAndEvaluate(String absolutePathInitialFile,
      HashMap<String, Object> variables, String absolutePathNewFile) throws IOException {

    String initialFileContent = new String(Files.readAllBytes(Paths.get(absolutePathInitialFile)));
    String evaluatedContent = VariablePlaceHolderHelper.evaluate(initialFileContent, variables);
    Files.write(Paths.get(absolutePathNewFile), evaluatedContent.getBytes());
  }
}
