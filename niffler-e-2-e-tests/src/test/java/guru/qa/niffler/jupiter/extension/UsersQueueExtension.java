package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

//3.2 video 41:27
public class UsersQueueExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext
          .Namespace.create(UsersQueueExtension.class);

  public record StaticUser(String username, String password, boolean empty) {
  }

  private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> NON_EMPTY_USERS = new ConcurrentLinkedQueue<>();

  static {
    EMPTY_USERS.add(new StaticUser("poops", "12345", true));
    NON_EMPTY_USERS.add(new StaticUser("duck", "12345", false));
    NON_EMPTY_USERS.add(new StaticUser("anna", "12345", false));
  }

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface UserType {
    boolean empty() default true;
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    Arrays.stream(context.getRequiredTestMethod().getParameters())
            .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
            .findFirst()
            .map(p -> p.getAnnotation(UserType.class))
            .ifPresent(
                    ut -> {
                      Optional<StaticUser> user = Optional.empty();
                      StopWatch sw = StopWatch.createStarted();
                      while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                        user = ut.empty()
                                ? Optional.ofNullable(EMPTY_USERS.poll())
                                : Optional.ofNullable(NON_EMPTY_USERS.poll());
                      }
                      Allure.getLifecycle().updateTestCase(tc -> tc.setStart(new Date().getTime()));
                      user.ifPresentOrElse(
                              u -> {
                                context.getStore(NAMESPACE)
                                        .put(
                                                context.getUniqueId(),
                                                u
                                        );
                              },
                              ()->new IllegalStateException("Can't find user after 30 sec")
                      );
                    }
            );
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    StaticUser user = context.getStore(NAMESPACE).get(context.getUniqueId(), StaticUser.class);
    if (user.empty()) {
      EMPTY_USERS.add(user);
    }else {
      NON_EMPTY_USERS.add(user);
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
            && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
  }

  @Override
  public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), StaticUser.class);
  }
}
