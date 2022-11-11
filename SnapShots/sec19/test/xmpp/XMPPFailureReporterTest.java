package test.xmpp;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.verify;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import main.xmpp.XMPPFailureReporter;

@RunWith(MockitoJUnitRunner.class) public class XMPPFailureReporterTest {
  @Mock Logger logger;
  @InjectMocks XMPPFailureReporter reporter;

  @Test public void 翻訳不可能なメッセージが来たらログに書き込む() {
    reporter.cannotTranslateMessage("sniper id", "bad message", new Exception("bad"));
    verify(logger).severe("<sniper id> Could not translate message [bad message] because [java.lang.Exception: bad].");
  }

  @AfterClass public static void resetLogging(){ LogManager.getLogManager().reset();}
}

