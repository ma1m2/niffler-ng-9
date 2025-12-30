package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.extension.UsersQueueExtensionOld.StaticUser;
import guru.qa.niffler.jupiter.extension.UsersQueueExtensionOld.UserType;
import guru.qa.niffler.jupiter.extension.UsersQueueExtensionOld;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UsersQueueExtensionOld.class)
//3.2 video 54:50
@Disabled
public class UsersQueueTest {

  @Test
  public void testWithEmptyUser0(@UserType(empty = true) StaticUser user) throws InterruptedException {
    Thread.sleep(1000);
    System.out.println(user);
  }

  @Test
  public void testWithEmptyUser1(@UserType(empty = true) StaticUser user) throws InterruptedException {
    Thread.sleep(1000);
    System.out.println(user);
  }

  @Test
  public void testWithEmptyUser2(@UserType(empty = false) StaticUser user) throws InterruptedException {
    Thread.sleep(1000);
    System.out.println(user);
  }

  @Test
  public void testWithEmptyUser3(@UserType(empty = false) StaticUser user) throws InterruptedException {
    Thread.sleep(1000);
    System.out.println(user);
  }
}
