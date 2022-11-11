package main.xmpp;

public interface FailureReporter {
  void cannotTranslateMessage(String sniperId, String failMsg, Exception e);
  void cannotTranslateMessage2(String sniperId, String failMsg);
}