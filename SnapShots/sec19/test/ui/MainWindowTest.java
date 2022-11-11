package test.ui;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

import main.Portfolio;
import main.Item;
import main.ui.UserListener;
import main.ui.MainWindow;
import main.ui.TableModel;

public class MainWindowTest {
  @BeforeClass public static void setupKeyBoardLayout(){ System.setProperty("com.objogate.wl.keyboard", "US");}
  public final Portfolio pf = new Portfolio();
  public final MainWindow window = new MainWindow(pf);
  public final SwingTestDriver driver = new SwingTestDriver(1000);
  @Test public void 参加ボタンを押すとユーザーリスナに報告するよ(){
    final ValueMatcherProbe<Item> itemProbe = new ValueMatcherProbe<>(equalTo(new Item("an item-id", 789)), "item request");
    window.addListener(
      new UserListener(){
        @Override public void joinAuction(Item item){ itemProbe.setReceivedValue(item);}
      }
    );
    driver.入札開始("an item-id", 789); driver.check(itemProbe);
  }

}