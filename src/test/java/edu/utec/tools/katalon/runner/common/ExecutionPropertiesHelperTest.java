package edu.utec.tools.katalon.runner.common;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import com.jayway.jsonpath.JsonPath;
import edu.utec.test.common.TestHelper;


public class ExecutionPropertiesHelperTest {

  @Before
  public void setup() {
    new VariablePlaceHolderHelper();
  }

  @Test
  public void cloneAndEvaluate() throws Exception {

    HashMap<String, Object> variables = new HashMap<String, Object>();
    variables.put("uuid", UUID.randomUUID().toString());
    variables.put("BROWSERSTACK_USER", "jane");
    variables.put("BROWSERSTACK_PASSWORD", "doe");

    String tempfilePath = System.getProperty("java.io.tmpdir");
    String initialFilePath =
        tempfilePath + File.separator + String.format("%s.execution.properties", "ini");
    String newExecutionPropertiesPath =
        tempfilePath + File.separator + String.format("%s.execution.properties", "new");

    // create initial file
    String content = TestHelper.getFileAsString(this, "textWithSeveralVariables.txt");
    Files.write(Paths.get(initialFilePath), content.getBytes());
    // clone it
    ExecutionProperiesHelper.cloneAndEvaluate(initialFilePath, variables,
        newExecutionPropertiesPath);

    // assert
    String newFileContent = new String(Files.readAllBytes(Paths.get(newExecutionPropertiesPath)));
    String url = JsonPath.read(newFileContent, "$.system.Remote.remoteWebDriverUrl");
    assertEquals(url, "http://jane:doe@hub-cloud.browserstack.com/wd/hub");
  }

}
