package edu.utec.tools.katalon.runner.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import com.jayway.jsonpath.JsonPath;
import edu.utec.test.common.TestHelper;


public class VariablePlaceHolderHelperTest {

  @Before
  public void setup() {
    new VariablePlaceHolderHelper();
  }
  
  @Test
  public void evaluateSimple() throws Exception {
    String contentWithJocker = TestHelper.getFileAsString(this, "textWithOneVar.txt");
    HashMap<String, Object> variables = new HashMap<String, Object>();
    variables.put("uuid", UUID.randomUUID().toString());
    String contentEvaluated = VariablePlaceHolderHelper.evaluate(contentWithJocker, variables);
    String uuid = JsonPath.read(contentEvaluated, "$.build");
    assertThat(uuid.length()).isGreaterThan(16);
  }
  
  @Test
  public void evaluateComplex() throws Exception {
    String contentWithJocker = TestHelper.getFileAsString(this, "textWithSeveralVariables.txt");
    HashMap<String, Object> variables = new HashMap<String, Object>();
    variables.put("uuid", UUID.randomUUID().toString());
    variables.put("BROWSERSTACK_USER", "jane");
    variables.put("BROWSERSTACK_PASS", "doe");
    String contentEvaluated = VariablePlaceHolderHelper.evaluate(contentWithJocker, variables);
    String uuid = JsonPath.read(contentEvaluated, "$.build");
    assertThat(uuid.length()).isGreaterThan(16);
    String url = JsonPath.read(contentEvaluated, "$.remoteWebDriverUrl");
    assertEquals(url, "http://jane:doe@hub-cloud.browserstack.com/wd/hub");
  }

  @Test
  public void evaluateComplexEnv() throws Exception {
    String contentWithJocker = TestHelper.getFileAsString(this, "textWithSeveralVariables.txt");
    HashMap<String, Object> variables = new HashMap<String, Object>();
    variables.put("uuid", UUID.randomUUID().toString());
    //add env variables
    TestHelper.setEnvironmentVariable("BROWSERSTACK_USER", "jane");
    TestHelper.setEnvironmentVariable("BROWSERSTACK_PASS", "doe");
    
    String contentEvaluated = VariablePlaceHolderHelper.evaluate(contentWithJocker, variables);
    String uuid = JsonPath.read(contentEvaluated, "$.build");
    assertThat(uuid.length()).isGreaterThan(16);
    String url = JsonPath.read(contentEvaluated, "$.remoteWebDriverUrl");
    assertEquals(url, "http://jane:doe@hub-cloud.browserstack.com/wd/hub");
  }

}
