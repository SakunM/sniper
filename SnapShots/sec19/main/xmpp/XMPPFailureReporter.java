package main.xmpp;

import static java.lang.String.format;
import java.util.logging.Logger;

public class XMPPFailureReporter implements FailureReporter {
  private final Logger logger;
  @Override public void cannotTranslateMessage(final String sniperId, final String failMsg, final Exception e) {
    logger.severe(format("<%s> Could not translate message [%s] because [%s].", sniperId, failMsg, e.toString()));
  }
  public XMPPFailureReporter(final Logger logger){ this.logger = logger;}
}