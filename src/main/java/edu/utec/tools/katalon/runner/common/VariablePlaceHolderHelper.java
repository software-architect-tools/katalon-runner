package edu.utec.tools.katalon.runner.common;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VariablePlaceHolderHelper {

  private static final Logger logger = LoggerFactory.getLogger(VariablePlaceHolderHelper.class);

  public static String evaluate(String content, HashMap<String, Object> variables) {

    String regex = "(\\$\\{[\\w\\^\\$\\s]+\\})";
    Matcher m = Pattern.compile(regex).matcher(content);
    while (m.find()) {

      String key = m.group(0).replace("${", "").replace("}", "");
      logger.debug(String.format("variable was detected: %s in raw string", key));

      if (key == null || key.equals("")) {
        continue;
      }

      if (variables != null && variables.get(key) != null) {
        logger.debug("variable " + key + " exist in global context: "+(String) variables.get(key));
        content = content.replace(m.group(0), (String) variables.get(key));
      } else if (System.getenv(key) != null) {
        logger.debug("variable " + key + " exist in environment context");
        content = content.replace(m.group(0), System.getenv(key));
      } else {
        logger
            .debug("variable " + key + " does not exist in environment context nor global context");
      }
    }

    return content;
  }

}
