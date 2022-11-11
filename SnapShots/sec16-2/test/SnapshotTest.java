package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import main.State;
import main.Snapshot;

public class SnapshotTest {
  @Test public void 状態がどのように変わるか見てみるよ(){
    final String itemId = "item 123";
    Snapshot joining = Snapshot.joining(itemId);
    assertEquals(new Snapshot(itemId, 0, 0, State.JOINING), joining);

    Snapshot bidding = joining.bidding(123, 234);
    assertEquals(new Snapshot(itemId, 123, 234, State.BIDDING), bidding);

    assertEquals(new Snapshot(itemId, 456, 234, State.WINNING), bidding.winning(456));
    assertEquals(new Snapshot(itemId, 123, 234, State.LOST), bidding.closed());
    assertEquals(new Snapshot(itemId, 678, 234, State.WON), bidding.winning(678).closed());
  }
}
