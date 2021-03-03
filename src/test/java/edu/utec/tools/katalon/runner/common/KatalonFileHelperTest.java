package edu.utec.tools.katalon.runner.common;

import static org.junit.Assert.assertEquals;
import java.io.File;
import org.junit.Before;
import org.junit.Test;

public class KatalonFileHelperTest {
  @Before
  public void setup() {
    new KatalonFileHelperTest();
  }

  @Test
  public void getTestCaseIdFromPath() throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    File scriptsFolder = new File(classLoader.getResource(
        "edu/utec/tools/katalon/runner/common/KatalonFileHelperTest.KatalonStudioProject/Scripts")
        .getFile());
    String absoluteKatalonProjectPath = scriptsFolder.getParentFile().getAbsolutePath();
    File script = new File(classLoader.getResource(
        "edu/utec/tools/katalon/runner/common/KatalonFileHelperTest.KatalonStudioProject/Scripts/acme/looney-tunes/Script1612800416623.groovy")
        .getFile());
    String absoluteScriptPath = script.getAbsolutePath();

    String testCaseId =
        KatalonFileHelper.getTestCaseIdFromPath(absoluteKatalonProjectPath, absoluteScriptPath);
    assertEquals(testCaseId, "acme/looney-tunes");
  }

  @Test
  public void getTestCaseNameFromPath() throws Exception {
    String absoluteScriptPath =
        "/tmp/katalon_project/Scripts/Gestión Académica del Alumno/Curricula/ConsolidadoMatricula/ConsolidadoMatricula01/Script1611261793511.groovy";
    String testCaseName = KatalonFileHelper.getTestCaseNameFromPath(absoluteScriptPath);
    assertEquals(testCaseName, "ConsolidadoMatricula01");
  }
}
