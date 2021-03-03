package edu.utec.tools.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Test;

public class FileHelperTest {

  @Before
  public void setup() {
    new FileHelper();
  }

  @Test
  public void getFileAsStringFromClasspath() throws Exception {
    String fileContent = FileHelper.getFileAsStringFromClasspath(
        "/edu/utec/tools/common/FileHelperTest.getFileAsStringFromClasspath.txt");
    assertEquals(
        "When I wrote this code, only God and I understood what I did. Now only God knows.",
        fileContent);
    String fileEmptyContent = FileHelper.getFileAsStringFromClasspath(
        "/edu/utec/tools/common/FileHelperTest.getFileAsStringFromClasspathEmpty.txt");
    assertEquals("", fileEmptyContent);
  }

  @Test
  public void mapToJsonFile() throws Exception {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("boolan", true);
    map.put("int", 5);
    map.put("string", "hello");

    String tempAbsolutePath =
        System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString();
    FileHelper.mapToJsonFile(map, tempAbsolutePath);

    assertEquals(true, new File(tempAbsolutePath).exists());
    new File(tempAbsolutePath).deleteOnExit();
  }

  @Test
  public void mapToJsonFileNull() throws Exception {
    try {
      FileHelper.mapToJsonFile(null, null);
    } catch (Exception e) {
      assertEquals(true, e.getMessage().startsWith("Failed to generate json from map"));
    }
  }

  @Test
  public void listFileTreeNoRegex() throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    File scriptsFolder = new File(
        classLoader.getResource("edu/utec/tools/common/FileHelperTest.KatalonStudioProject/Scripts")
            .getFile());

    assertTrue(scriptsFolder.exists());

    ArrayList<File> scriptsFiles = FileHelper.listFileTree(scriptsFolder.getAbsolutePath(),
        scriptsFolder, ".groovy", null, null);
    assertEquals(Integer.valueOf(3), Integer.valueOf(scriptsFiles.size()));
  }

  @Test
  public void listFileTreeEmpty() throws Exception {
    // null dir returns empty list
    ArrayList<File> nullDir = FileHelper.listFileTree(null, null, ".acme");
    assertEquals(0, nullDir.size());

    String tempAbsolutePath =
        System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString();
    File theDir = new File(tempAbsolutePath);
    if (!theDir.exists()) {
      theDir.mkdirs();
    }

    // folder without any files, must returns an empty list
    ArrayList<File> emptyDir =
        FileHelper.listFileTree(tempAbsolutePath, theDir, ".acme", null, null);
    assertEquals(0, emptyDir.size());
  }

  @Test
  public void listFileTreeWithNegateRegexFileName() throws Exception {

    LoggerHelper.setDebugLevel();
    ClassLoader classLoader = getClass().getClassLoader();

    File baseFolder = new File(classLoader
        .getResource("edu/utec/tools/common/FileHelperTest.KatalonStudioProject").getFile());

    assertTrue(baseFolder.exists());

    // give me just files which does not starts with "write"
    Pattern regexFilePattern = Pattern.compile("(?!^write).*");
    ArrayList<File> features = FileHelper.listFileTree(baseFolder.getAbsolutePath(), baseFolder,
        ".groovy", regexFilePattern, null);
    assertEquals(1, features.size());
  }

  @Test
  public void listFileTreeWithNegateRegexFolderName() throws Exception {

    LoggerHelper.setDebugLevel();
    ClassLoader classLoader = getClass().getClassLoader();

    File baseFolder = new File(classLoader
        .getResource("edu/utec/tools/common/FileHelperTest.KatalonStudioProject").getFile());

    assertTrue(baseFolder.exists());

    // give me just folder which does not starts with "test2"
    Pattern regexFolderPattern = Pattern.compile("(?!^test2).*");
    ArrayList<File> scripts = FileHelper.listFileTree(baseFolder.getAbsolutePath(), baseFolder,
        ".groovy", null, regexFolderPattern);
    assertEquals(1, scripts.size());
  }
  
  @Test
  public void listFileTreeWithIncludeRegexFileName() throws Exception {

    LoggerHelper.setDebugLevel();
    ClassLoader classLoader = getClass().getClassLoader();

    File baseFolder = new File(classLoader
        .getResource("edu/utec/tools/common/FileHelperTest.KatalonStudioProject").getFile());

    assertTrue(baseFolder.exists());

    // give me just files which starts with "tachikoma"
    Pattern regexFilePattern = Pattern.compile("^tachikoma.+");
    ArrayList<File> features = FileHelper.listFileTree(baseFolder.getAbsolutePath(), baseFolder,
        ".groovy", regexFilePattern, null);
    assertEquals(1, features.size());
  }
  
  @Test
  public void listFileTreeWithIncludeRegexFolderName() throws Exception {

    LoggerHelper.setDebugLevel();
    ClassLoader classLoader = getClass().getClassLoader();

    File baseFolder = new File(classLoader
        .getResource("edu/utec/tools/common/FileHelperTest.KatalonStudioProject").getFile());

    assertTrue(baseFolder.exists());

    // give me just folder which does not starts with "test2"
    Pattern regexFolderPattern = Pattern.compile("^test2.*");
    ArrayList<File> scripts = FileHelper.listFileTree(baseFolder.getAbsolutePath(), baseFolder,
        ".groovy", null, regexFolderPattern);
    assertEquals(2, scripts.size());
  }  
}
