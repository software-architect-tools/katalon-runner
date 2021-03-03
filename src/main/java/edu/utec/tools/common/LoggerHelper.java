package edu.utec.tools.common;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class LoggerHelper {

  public static void setDebugLevel() {
    Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    root.setLevel(Level.DEBUG);
    LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
    loggerContext.getLogger("com.kms.katalon.core").setLevel(Level.DEBUG); 
    loggerContext.getLogger("com.kms").setLevel(Level.DEBUG); 
    loggerContext.getLogger("edu.utec").setLevel(Level.DEBUG); 
        
  }
}
