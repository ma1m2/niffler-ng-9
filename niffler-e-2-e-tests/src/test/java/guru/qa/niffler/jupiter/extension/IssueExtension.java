package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.GhApiClient;
import guru.qa.niffler.jupiter.annotation.DisableByIssue;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;


//3.2 video 26:00
public class IssueExtension implements ExecutionCondition {

  private final GhApiClient ghApiClient = new GhApiClient();

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    Optional<DisableByIssue> annotation;

    annotation = AnnotationSupport.findAnnotation(
            context.getRequiredTestClass(),
            DisableByIssue.class,
            context.getEnclosingTestClasses()
    );

    if (context.getTestMethod().isPresent() && annotation.isEmpty()) {
      annotation = AnnotationSupport.findAnnotation(
              context.getRequiredTestMethod(),
              DisableByIssue.class
      );
    }

    return annotation.map(
            byIssue -> "open".equals(ghApiClient.issueState(byIssue.value()))
                    ? ConditionEvaluationResult.disabled("Disabled by issue #" + byIssue.value())
                    : ConditionEvaluationResult.enabled("Issue closed")
    ).orElseGet(
            () -> ConditionEvaluationResult.enabled("Annotation @DisabledByIssue not found")
    );

/*    return AnnotationSupport.findAnnotation(
            context.getRequiredTestMethod(),
            DisableByIssue.class
    ).or(() -> AnnotationSupport.findAnnotation(
                    context.getRequiredTestClass(),
                    DisableByIssue.class,
                    context.getEnclosingTestClasses()
                    //SearchOption.INCLUDE_ENCLOSING_CLASSES //deprecated
            )
    ).map(
            byIssue -> "open".equals(ghApiClient.issueState(byIssue.value()))
            ? ConditionEvaluationResult.disabled("Disable by issue " + byIssue.value())
                    : ConditionEvaluationResult.enabled("Issue closed")
    ).orElseGet(
            () -> ConditionEvaluationResult.enabled("Annotation @DisabledByIssue not found")
    );*/
  }
}
