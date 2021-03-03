package edu.utec.tools.katalon.runner.exec;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.driver.internal.DriverCleanerCollector;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.main.TestCaseMain;
import com.kms.katalon.core.main.TestResult;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testcase.TestCaseBinding;
import com.kms.katalon.core.webui.driver.DriverFactory;
import edu.utec.tools.common.ExceptionHelper;
import edu.utec.tools.common.FileHelper;
import edu.utec.tools.common.TimeHelper;
import edu.utec.tools.katalon.runner.common.ExecutionProperiesHelper;
import edu.utec.tools.katalon.runner.common.MetadataHelper;
import edu.utec.tools.katalon.runner.common.StringHelper;
import edu.utec.tools.katalon.runner.common.VariablePlaceHolderHelper;

public class KatalonRunner {

  private final Logger logger = LoggerFactory.getLogger(KatalonRunner.class);

  public HashMap<String, Object> execute(String katalonProjectAbsolutePath, String executionType,
      String profile, String regexFile, String regexFolder) throws Exception {

    Date dateExecution = new Date();

    System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
    System.setProperty("org.eclipse.jetty.LEVEL", "OFF");

    String executionUuid = UUID.randomUUID().toString();
    MDC.put("executionUuid", executionUuid);
    logger.info("Katalon Runner execution uuid: " + executionUuid);

    DriverCleanerCollector.getInstance()
        .addDriverCleaner(new com.kms.katalon.core.webui.contribution.WebUiDriverCleaner());
    DriverCleanerCollector.getInstance()
        .addDriverCleaner(new com.kms.katalon.core.mobile.contribution.MobileDriverCleaner());

    // sample: ../Executions/local-firefox/execution.properties
    String executionPropertiesPath = katalonProjectAbsolutePath + File.separator + String.format(
        "Executions%s%s%sexecution.properties", File.separator, executionType, File.separator);

    File scriptsDir = new File(katalonProjectAbsolutePath + File.separator + "Scripts");
    if (!scriptsDir.exists()) {
      throw new Exception(
          "main folder does not exist: " + katalonProjectAbsolutePath + File.separator + "Scripts");
    }

    Pattern regexFilePattern = null;
    if (regexFile != null) {
      regexFilePattern = Pattern.compile(regexFile);
    }

    Pattern regexFolderPattern = null;
    if (regexFolder != null) {
      regexFolderPattern = Pattern.compile(regexFolder);
    }

    ArrayList<File> scriptFiles = FileHelper.listFileTree(scriptsDir.getAbsolutePath(), scriptsDir,
        ".groovy", regexFilePattern, regexFolderPattern);

    // update profile file with variables
    replaceVariablesInProfile(katalonProjectAbsolutePath, profile);
    logger.info("profile was updated");

    ArrayList<HashMap<String, Object>> scripts =
        MetadataHelper.getKatalonScripts(scriptFiles, katalonProjectAbsolutePath);

    logger.info(String.format("Detected %s valid katalon scripts", scripts.size()));
    logger.info(scripts.toString());

    HashMap<String, Object> globalVariables = new HashMap<String, Object>();
    globalVariables.put("katalonProjectAbsolutePath", StringHelper
        .scapeBackSlashForWindowsPath(System.getProperty("os.name"), katalonProjectAbsolutePath));    
    globalVariables.put("profile", profile);
    globalVariables.put("executionUuid", executionUuid);

    HashMap<String, Object> reportStats = new HashMap<String, Object>();
    ArrayList<HashMap<String, Object>> reportRows = new ArrayList<HashMap<String, Object>>();

    long globalStart = new Date().getTime();
    int passedCount = 0;
    int failedCount = 0;

    for (HashMap<String, Object> script : scripts) {
      long start = new Date().getTime();
      long end = 0l;
      ArrayList<String> errorLog = null;
      boolean isSucess = false;
      try {
        executeSingleKatalonScript(script, globalVariables, executionPropertiesPath);
        end = new Date().getTime();
        isSucess = true;
        passedCount++;
      } catch (StepFailedException e) {
        failedCount++;
        logger.error("Failed to execute test:" + script.get("absolutePath"), e);
        isSucess = false;
        end = new Date().getTime();
        errorLog = ExceptionHelper.summarizeCausesAsArray(e);
      } catch (Exception e) {
        failedCount++;
        logger.error("Failed to execute test:" + script.get("absolutePath"), e);
        isSucess = false;
        end = new Date().getTime();
        errorLog = ExceptionHelper.summarizeCausesAsArray(e);
      }



      HashMap<String, Object> testCaseStats = new HashMap<String, Object>();
      testCaseStats.put("status", (isSucess ? "passed" : "failed"));
      testCaseStats.put("durationMillis", end - start);
      testCaseStats.put("duration", TimeHelper.elapsedMillisToHumanExpression(start, end));
      testCaseStats.put("name", (String) script.get("testCaseId"));
      if (errorLog != null) {
        testCaseStats.put("errorLog", errorLog);
      }
      reportRows.add(testCaseStats);
    }

    long globalEnd = new Date().getTime();

    reportStats.put("reportName", "Katalon Report");
    reportStats.put("date", getDateAsString(dateExecution));
    reportStats.put("durationMillis", globalEnd - globalStart);
    reportStats.put("duration", TimeHelper.elapsedMillisToHumanExpression(globalStart, globalEnd));
    reportStats.put("total", scripts.size());
    reportStats.put("passed", passedCount);
    reportStats.put("failed", failedCount);
    reportStats.put("pending", scripts.size() - passedCount - failedCount);

    if (scripts.size() == passedCount) {
      reportStats.put("status", "success");
    } else {
      reportStats.put("status", "failed");
    }

    reportStats.put("reportRows", reportRows);

    return reportStats;
  }

  private void replaceVariablesInProfile(String katalonProjectAbsolutePath, String profile)
      throws Exception {
    String katalonProfileAbsolutePath =
        String.format("%s/Profiles/%s.glbl", katalonProjectAbsolutePath, profile);
    logger.debug("Replacing variables in :" + katalonProfileAbsolutePath);
    try {
      String initialFileContent =
          new String(Files.readAllBytes(Paths.get(katalonProfileAbsolutePath)));
      String evaluatedContent = VariablePlaceHolderHelper.evaluate(initialFileContent, null);
      Files.write(Paths.get(katalonProfileAbsolutePath), evaluatedContent.getBytes());
    } catch (Exception e) {
      throw new Exception(
          "Failure to perform variables replacement into profile: " + katalonProfileAbsolutePath,
          e);
    }
  }

  private String getDateAsString(Date date) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
    return simpleDateFormat.format(date);
  }

  private void executeSingleKatalonScript(HashMap<String, Object> script,
      HashMap<String, Object> globalVariables, String executionPropertiesPath) throws Exception {

    String testCaseId = (String) script.get("testCaseId");
    logger.info(String.format("Running script: %s", script.get("absolutePath")));
    logger.info(String.format("Script testCaseId: %s", testCaseId));

    HashMap<String, Object> localVariables = new HashMap<String, Object>();
    localVariables.putAll(globalVariables);
    String scriptUuid = UUID.randomUUID().toString();
    localVariables.put("scriptUuid", scriptUuid);
    MDC.put("scriptUuid", scriptUuid);
    logger.info(String.format("Script uuid: %s", scriptUuid));

    // expose script data as variables to be used in
    // execution.properties or profiles
    for (Entry<String, Object> entry : script.entrySet()) {
      localVariables.put(entry.getKey(), entry.getValue());
    }

    // creating new execution.propreties evaluating variables
    String tempfilePath = System.getProperty("java.io.tmpdir");
    String newExecutionPropertiesPath =
        tempfilePath + File.separator + String.format("%s.execution.properties", scriptUuid);
    ExecutionProperiesHelper.cloneAndEvaluate(executionPropertiesPath, localVariables,
        newExecutionPropertiesPath);

    logger.info("New execution.properties: " + newExecutionPropertiesPath);

    RunConfiguration.setExecutionSettingFile(newExecutionPropertiesPath);
    TestCaseMain.beforeStart();
    TestResult result = null;

    try {
      result = TestCaseMain.runTestCase(testCaseId, new TestCaseBinding(testCaseId, null),
          FailureHandling.STOP_ON_FAILURE, true);
      logger.info("Testcase result status: " + result.getTestStatus().getStatusValue());
    } catch (Exception e) {
      exitBrowserOnError();
      throw new Exception("Testcase ended with rare error.", e);
    }

    if (!result.getTestStatus().getStatusValue().toString().contentEquals("PASSED")) {
      exitBrowserOnError();
      throw new Exception("Testcase ended with error.", result.getCause());
    }

  }

  private void exitBrowserOnError() {
    WebDriver driver = DriverFactory.getWebDriver();
    try {
      driver.close();
    } catch (Exception e) {
      logger.debug("Rare error at close browser after errors in test cases", e);
    }
  }


}
