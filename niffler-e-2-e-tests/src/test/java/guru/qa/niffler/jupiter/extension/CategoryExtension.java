package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;
import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

//add from 3.3 1:01:01 createdCategory()
//hw 3.3 /video 6.3 1:27:01, 1:51:20
@ParametersAreNonnullByDefault
public class CategoryExtension implements
        BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext
          .Namespace.create(CategoryExtension.class);
  private final SpendClient spendClient = SpendClient.getInstance();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
            .ifPresent(userAnno -> {
              if (ArrayUtils.isNotEmpty(userAnno.categories())) {
                final @Nullable UserJson createdUser = UserExtension.getUserJson();

                final String username = createdUser != null
                        ? createdUser.username()
                        : userAnno.username();

                final List<CategoryJson> result = new ArrayList<>();
                for (Category categoryAnno : userAnno.categories()) {
                  CategoryJson category = new CategoryJson(
                          null,
                          "".equals(categoryAnno.name()) ? randomCategoryName() : categoryAnno.name(),
                          username,
                          categoryAnno.archived()
                  );

                  CategoryJson created = spendClient.createCategory(category);
                  if (categoryAnno.archived()) {
                    CategoryJson archivedCategory = new CategoryJson(
                            created.id(),
                            created.name(),
                            created.username(),
                            true
                    );
                    created = spendClient.updateCategory(archivedCategory);
                  }
                  result.add(created);
                }

                if (createdUser != null) {//если юзер лежит в контексте
                  //сохраняем созданные категории в юзера
                  createdUser.testData().categories().addAll(result);
                } else {//а иначе просто кладем в контекст, как и раньше
                  context.getStore(NAMESPACE).put(
                          context.getUniqueId(),
                          result.stream().toArray(CategoryJson[]::new)
                  );//итог работы цепочки из 3-х экстеншенов, либо юзер у которого testData и все нужное нам
                }//хранится в этой testData, любо по отдельности массивы категорий, спендингов
              }//зависит от того, нужны они для тестов или нет
            });//видео 6.3 1:51:20
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    CategoryJson[] categories = createdCategory();
    if (categories != null) {
      for (CategoryJson category : categories) {
        if (category != null && !category.archived()) {
          category = new CategoryJson(
                  category.id(),
                  category.name(),
                  category.username(),
                  true
          );
          spendClient.updateCategory(category);
        }
      }
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson[].class);
  }

  @Override
  public CategoryJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    //return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson[].class);
    return createdCategory();
  }

  public static CategoryJson[] createdCategory() {
    final ExtensionContext methodContext = context();
    return methodContext.getStore(NAMESPACE)
            .get(methodContext.getUniqueId(), CategoryJson[].class);
  }

}
