package edu.utec.tools.common;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHelper {

  private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);

  public static ArrayList<File> listFileTree(String rootDirectory, File currentDirectory,
      String ext, Pattern regexFilePattern, Pattern regexFolderPattern) {
    ArrayList<File> fileTree = new ArrayList<File>();

    if (currentDirectory == null || currentDirectory.listFiles() == null) {
      return fileTree;
    }
    for (File entry : currentDirectory.listFiles()) {
      if (entry.isFile() && entry.getName().endsWith(ext)) {

        if (regexFilePattern == null && regexFolderPattern == null) {
          fileTree.add(entry);
          logger.debug("no regex are deteted. File was added:" + entry.getName());
          continue;
        }

        if (regexFilePattern != null) {
          Matcher regexFile = regexFilePattern.matcher(entry.getName());
          if (regexFile.matches()) {// filter files using regex
            fileTree.add(entry);
            logger
                .debug("regex " + regexFilePattern.pattern() + " file matched: " + entry.getName());
          } else {
            logger.debug(
                "regex " + regexFilePattern.pattern() + " file don't match: " + entry.getName());
          }
        } else if (regexFolderPattern != null) {
          String relativeFolder = currentDirectory.getAbsolutePath()
              .replace(rootDirectory + File.separator + "Scripts" + File.separator, "");
          Matcher regexFolder = regexFolderPattern.matcher(relativeFolder);
          if (regexFolder.matches()) {// filter files using regex
            fileTree.add(entry);
            logger.debug("regex " + regexFolder.pattern() + "  folder match: " + relativeFolder);
          } else {
            logger
                .debug("regex " + regexFolder.pattern() + " folder don't match: " + relativeFolder);
          }
        }
      } else {
        fileTree
            .addAll(listFileTree(rootDirectory, entry, ext, regexFilePattern, regexFolderPattern));
      }
    }
    Collections.sort(fileTree);
    return fileTree;
  }

  public static ArrayList<File> listFileTree(String rootDir, File dir, String ext) {
    return listFileTree(rootDir, dir, ext, null, null);
  }

  public static void mapToJsonFile(Map<?, ?> obj, String fileAbsolutePath) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
    try {
      writer.writeValue(new File(fileAbsolutePath), obj);
    } catch (Exception e) {
      throw new Exception("Failed to generate json from map", e);
    }
  }

  public static String getFileAsStringFromClasspath(String file) {
    InputStream classPathFileStream = FileHelper.class.getResourceAsStream(file);
    Scanner scanner = new Scanner(classPathFileStream);
    scanner.useDelimiter("\\A");
    String fileContent = scanner.hasNext() ? scanner.next() : "";
    scanner.close();
    return fileContent;
  }

  public static void assertExist(String path) throws Exception {
    if (!new File(path).exists()) {
      throw new Exception("File or folder does not exist: " + path);
    }
  }

  public static void createIfNotExist(String path) throws Exception {
    try {
      File theDir = new File(path);
      if (!theDir.exists()) {
        theDir.mkdirs();
      }
    } catch (Exception e) {
      throw new Exception("Failure at folder creation: " + path);
    }
  }
}
