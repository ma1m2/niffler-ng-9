package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UsersQueueExtension.class)
//3.2 video 54:50
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
