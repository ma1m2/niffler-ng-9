package guru.qa.niffler.jupiter.extension;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

//hw 3.2
public class UsersQueueExtension implements
        BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {


  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

  public record StaticUser(String username, String password, String friend, String income, String outcome) {
  }

  private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

  static {
    EMPTY_USERS.add(new StaticUser("bee", "12345", null, null, null));
    WITH_FRIEND_USERS.add(new StaticUser("duck", "12345", "dima", null, null));
    WITH_INCOME_REQUEST_USERS.add(new StaticUser("dima", "12345", null, "bee", null));
    WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("barsik", "12345", null, null, "bill"));
  }

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface UserType {
    Type value() default Type.EMPTY;

    enum Type {
      EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public void beforeTestExecution(ExtensionContext context) {
  }

  @Override
  @SuppressWarnings("unchecked")
  public void afterTestExecution(ExtensionContext context) {
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
            && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
  }

  @Override
  public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return (StaticUser) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class)
            .get(
                    AnnotationSupport.findAnnotation(parameterContext.getParameter(), UserType.class).get()
            );
  }

}
