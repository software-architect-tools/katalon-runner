package edu.utec.tools.katalon.runner.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import edu.utec.test.common.TestHelper;
import edu.utec.tools.common.FileHelper;
import edu.utec.tools.common.LoggerHelper;

public class MetadataHelperTest {

  @Before
  public void setup() {
    new MetadataHelper();
  }

  @Test
  public void containsMeta() throws Exception {
    String rawScript = TestHelper.getFileAsString(this, "idealScript.txt");
    HashMap<String, Object> meta = MetadataHelper.getRawMeta(rawScript);
    assertNotNull(meta);
    assertEquals("acme test case", (String) meta.get("name"));
    assertEquals("001", (String) meta.get("id"));
    assertEquals("true", (String) meta.get("disable"));
  }

  @Test
  public void getScripts() throws Exception {
    LoggerHelper.setDebugLevel();
    ClassLoader classLoader = getClass().getClassLoader();
    File scriptsFolder = new File(classLoader
        .getResource(
            "edu/utec/tools/katalon/runner/common/MetadataHelperTest.KatalonStudioProject/Scripts")
        .getFile());

    File scriptsDir = new File(scriptsFolder.getAbsolutePath());
    assertTrue(scriptsDir.exists());

    String absoluteKatalonProjectPath = scriptsDir.getParentFile().getAbsolutePath();

    ArrayList<File> scriptsFiles =
        FileHelper.listFileTree(scriptsDir.getAbsolutePath(), scriptsDir, ".groovy", null, null);
    ArrayList<HashMap<String, Object>> scripts =
        MetadataHelper.getKatalonScripts(scriptsFiles, absoluteKatalonProjectPath);
    assertEquals(Integer.valueOf(3), Integer.valueOf(scripts.size()));
    assertEquals("test case 1", (String) scripts.get(0).get("name"));
    assertEquals("test1", (String) scripts.get(0).get("testCaseId"));

    assertEquals("test case 2", (String) scripts.get(1).get("name"));
    assertEquals("test case 3", (String) scripts.get(2).get("name"));
  }

}
