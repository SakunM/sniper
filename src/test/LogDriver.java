package test;

import java.io.File;
import java.io.IOException;
import java.util.logging.LogManager;

import org.apache.commons.io.FileUtils;

import static org.junit.Assert.assertThat;

import static org.hamcrest.core.Is.is;
import org.hamcrest.Matcher;

public class LogDriver {
  private final File logFile = new File("auction.log");
  public void hasEntry(Matcher<String> matcher) throws IOException {
    String failMsg = FileUtils.readFileToString(logFile);
    System.out.println(failMsg);
    assertThat(failMsg, is(matcher));
  }
  public void clearLog(){
    logFile.delete();
    LogManager.getLogManager().reset();
  }
}

