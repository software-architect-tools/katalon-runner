package edu.utec.tools.katalon.runner.common;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import edu.utec.test.common.TestHelper;

public class StringHelperTest {

  @Before
  public void setup() {
    new StringHelper();
  }

  @Test
  public void containsMetaIdeal() throws Exception {
    String rawScript = TestHelper.getFileAsString(this, "idealScript.txt");
    assertNotNull(StringHelper.getHeadMultilineBlock(rawScript, "/*", "*/"));
  }

  @Test
  public void notContainsMeta() throws Exception {
    // has one empty line at the start
    String rawScript = TestHelper.getFileAsString(this, "wrongMetaScript1.txt");
    assertNull(StringHelper.getHeadMultilineBlock(rawScript, "/*", "*/"));

    // does not start with /*
    rawScript = TestHelper.getFileAsString(this, "wrongMetaScript2.txt");
    assertNull(StringHelper.getHeadMultilineBlock(rawScript, "/*", "*/"));

    // does not end with */
    rawScript = TestHelper.getFileAsString(this, "wrongMetaScript3.txt");
    assertNull(StringHelper.getHeadMultilineBlock(rawScript, "/*", "*/"));

  }

  @Test
  public void scapeBackSlashForWindowsPath() throws Exception {
    TestHelper.setEnvironmentVariable("os.name", "Windows");
    assertEquals("D:/AutomatizacionWeb/web-testing-automation",
        StringHelper.scapeBackSlashForWindowsPath("Windows",
            "D:\\\\AutomatizacionWeb\\\\web-testing-automation"));

  }
}
