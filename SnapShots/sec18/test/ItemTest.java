package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import org.junit.Test;

import main.Item;

public class ItemTest {
  @Test public void アイテムの簡単なテストだ(){
    Item i1 = new Item("hoge", 13);
    Item i2 = new Item("hoge", 13);
    Item i3 = new Item("hoge", 15);
    Item i4 = new Item("fuga", 13);
    assertEquals(i1, i2);
    assertNotSame(i1, i3);
    assertNotSame(i1, i4);
  }
}