package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.Bubble;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@ParametersAreNonnullByDefault
public class StatConditions {

  @Nonnull
  public static WebElementCondition statBubble(Bubble expected) {
    return new WebElementCondition(expected.color().rgb + " - '" + expected.text() + "'") {
      @NotNull
      @Override
      public CheckResult check(Driver driver, WebElement element) {
        final String rgba = element.getCssValue("background-color");
        final String text = element.getText();
        return new CheckResult(
                expected.color().rgb.equals(rgba) && expected.text().equals(text),
                rgba + " - '" + text + "'"
        );
      }
    };
  }

  @Nonnull
  public static WebElementsCondition statBubble(Bubble... expected) {
    return statBubble(List.of(expected));
  }

  @Nonnull
  public static WebElementsCondition statBubble(List<Bubble> expected) {
    return new WebElementsCondition() {

      private final List<String> expectedElements = mapExpectedElements(toList(), expected);

      @NotNull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (expected.size() != elements.size()) {
          final String message = String.format("List size mismatch (expected: %s, actual: %s)", expected.size(), elements.size());
          return rejected(message, elements);
        }

        final List<String> actualElements = mapActualElements(toList(), elements);

        if (!expectedElements.equals(actualElements)) {
          final String message = String.format(
                  "Bubbles list mismatch (expected: %s, actual: %s)", expectedElements, actualElements
          );
          return rejected(message, actualElements);
        }
        return accepted();
      }

      @Nonnull
      @Override
      public String toString() {
        return expectedElements.toString();
      }
    };
  }

  @Nonnull
  public static WebElementsCondition statBubbleInAnyOrder(Bubble... expected) {
    return statBubbleInAnyOrder(List.of(expected));
  }

  @Nonnull
  public static WebElementsCondition statBubbleInAnyOrder(List<Bubble> expected) {
    return new WebElementsCondition() {

      private final Set<String> expectedElements = mapExpectedElements(toSet(), expected);

      @NotNull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (expected.size() != elements.size()) {
          final String message = String.format("List size mismatch (expected: %s, actual: %s)", expected.size(), elements.size());
          return rejected(message, elements);
        }

        final Set<String> actualElements = mapActualElements(toSet(), elements);

        if (!expectedElements.equals(actualElements)) {
          final String message = String.format(
                  "Bubbles list mismatch (expected: %s, actual: %s)", expectedElements, actualElements
          );
          return rejected(message, actualElements);
        }

        return accepted();
      }

      @Nonnull
      @Override
      public String toString() {
        return expectedElements.toString();
      }
    };
  }

  @Nonnull
  public static WebElementsCondition containsStatBubble(Bubble... expected) {
    return containsStatBubble(List.of(expected));
  }

  @Nonnull
  public static WebElementsCondition containsStatBubble(List<Bubble> expected) {
    return new WebElementsCondition() {

      private final Set<String> expectedElements = mapExpectedElements(toSet(), expected);

      @NotNull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        final Set<String> actualElements = mapActualElements(toSet(), elements);

        if (!actualElements.containsAll(expectedElements)) {
          final String message = String.format(
                  "Bubbles list is missing some expected elements (expected: %s, actual: %s)", expectedElements, actualElements
          );
          return rejected(message, actualElements);
        }

        return accepted();
      }

      @Nonnull
      @Override
      public String toString() {
        return expectedElements.toString();
      }
    };
  }

  @Nonnull
  private static <T extends Collection<String>> T mapExpectedElements(Collector<String, ?, T> collector, List<Bubble> expected) {
    if (expected.isEmpty()) {
      throw new IllegalArgumentException("No expected bubbles given");
    }

    return expected.stream()
            .map(c -> c.color().rgb + " - '" + c.text() + "'")
            .collect(collector);
  }

  @Nonnull
  private static <T extends Collection<String>> T mapActualElements(Collector<String, ?, T> collector, List<WebElement> elements) {
    return elements.stream()
            .map(e -> e.getCssValue("background-color") + " - '" + e.getText() + "'")
            .collect(collector);
  }
}