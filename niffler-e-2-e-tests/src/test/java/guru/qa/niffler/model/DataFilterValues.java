package guru.qa.niffler.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor //обязательные поля - это final или @NonNull
public enum DataFilterValues {
  TODAY("Today"), WEEK("last week"), MONTH("Last month");
  public final String text;
}