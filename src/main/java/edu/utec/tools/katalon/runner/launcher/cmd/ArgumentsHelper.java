package edu.utec.tools.katalon.runner.launcher.cmd;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;

public class ArgumentsHelper {

  public CommandLine getArguments(String args[]) {
    Options options = new Options();

    Option o1 =
        new Option("k", "katalon_project_path", true, "katalon project absolute path location");
    o1.setRequired(true);
    options.addOption(o1);

    Option o2 = new Option("e", "execution_type", true, "Execution properties type");
    o2.setRequired(true);
    options.addOption(o2);

    Option o3 = new Option("d", "debug", false, "Show log as much as possible");
    o3.setRequired(false);
    options.addOption(o3);

    Option o4 =
        new Option("rt", "report_type", true, "Report type: default, html-compact, html-uncompact");
    o4.setRequired(false);
    options.addOption(o4);

    Option o5 = new Option("rl", "report_location", true, "Report folder location");
    o5.setRequired(true);
    options.addOption(o5);

    Option o6 = new Option("p", "profile", true, "Katalon studio profile");
    o6.setRequired(false);
    options.addOption(o6);

    Option o7 = new Option("rxf", "regex_file", true, "Regex to filter based on file names");
    o7.setRequired(false);
    options.addOption(o7);

    Option o8 =
        new Option("rxd", "regex_directory", true, "Regex to filter based on directory names");
    o8.setRequired(false);
    options.addOption(o8);

    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    CommandLine cmd;

    try {
      cmd = parser.parse(options, args);
      return cmd;
    } catch (ParseException e) {
      System.err.println(e.getMessage());
      formatter.printHelp("t-rext", options);
      System.exit(1);
      return null;
    }
  }

  public String simplePrint(CommandLine cmd)
      throws JsonGenerationException, JsonMappingException, IOException {

    List<String> exclusions = Arrays.asList("org.apache.commons.cli.Option.getId",
        "org.apache.commons.cli.Option.getArgName", "org.apache.commons.cli.Option.required",
        "org.apache.commons.cli.Option.type", "org.apache.commons.cli.Option.values",
        "org.apache.commons.cli.Option.getValuesList",
        "org.apache.commons.cli.Option.getValueSeparator", "org.apache.commons.cli.Option.getArgs");

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
      @Override
      public boolean hasIgnoreMarker(final AnnotatedMember m) {
        String absoluteName =
            String.format("%s.%s", m.getDeclaringClass().getCanonicalName(), m.getName());
        return exclusions.contains(absoluteName) || super.hasIgnoreMarker(m);
      }
    });

    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cmd.getOptions());
  }
}
