package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import main.State;
import main.Snapshot;
import main.Item;

public class SnapshotTest {
  @Test public void 状態がどのように変わるか見てみるよ(){
    final Item item = new Item("item 123", 789);
    Snapshot joining = Snapshot.joining(item);
    assertEquals(new Snapshot(item, 0, 0, State.JOINING), joining);

    Snapshot bidding = joining.bidding(123, 234);
    assertEquals(new Snapshot(item, 123, 234, State.BIDDING), bidding);

    assertEquals(new Snapshot(item, 456, 234, State.WINNING), bidding.winning(456));
    assertEquals(new Snapshot(item, 123, 234, State.LOST), bidding.closed());
    assertEquals(new Snapshot(item, 678, 234, State.WON), bidding.winning(678).closed());
    assertEquals(new Snapshot(item, 0, 0, State.FAILED), bidding.failed().closed());
  }
  @Test public void 状態の名前表現もテストするよ(){
    assertEquals("参加要請中", State.JOINING.name);
    assertEquals("入札参加中", State.BIDDING.name);
    assertEquals("一位入札中", State.WINNING.name);
    assertEquals("落札失敗", State.LOST.name);
    assertEquals("落札成功", State.WON.name);
    assertEquals("入札脱落中", State.LOSING.name);
    assertEquals("エラー発生", State.FAILED.name);
  }
  @Test public void クリエイトもテストするよ(){
    final Item item = new Item("item-54321", 2500);
    String fail = "item-54321 0 0 FAILED";
    Snapshot ss = Snapshot.create(item, fail);
    assertEquals("エラー発生", ss.state.name);
  }
}
