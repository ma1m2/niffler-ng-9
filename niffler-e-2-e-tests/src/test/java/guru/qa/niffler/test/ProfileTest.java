package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@WebTest
public class ProfileTest {
  private static final Config CFG = Config.getInstance();

  @Test
  @DisplayName("Create new catecory")
  public void createNewCategory(){
    String category = new Faker().animal().name();
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .successLogin("anna", "12345")
            .checkThatMainPageLoaded();

    System.out.println("New category " + category);
    Selenide.open(CFG.frontUrl()+"profile", ProfilePage.class)
            .checkThatItIsProfilePage()
            .addNewCategory(category)
            .checkCategoryExists(category);
  }

  @User(
          username = "anna",
          categories = @Category(
                  archived = true
          )
  )
  @Test
  @DisplayName("Archived Category is Present In Categories List")
  void archivedCategoryShouldPresentInCategoriesList(CategoryJson category){
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .successLogin(category.username(), "12345")
            .checkThatMainPageLoaded();

    System.out.println("New category " + category.name());
    Selenide.open(CFG.frontUrl()+"profile", ProfilePage.class)
            .checkThatItIsProfilePage()
            .checkArchivedCategoryExists(category.name());
  }

  @User(
          username = "anna",
          categories = @Category(
                  archived = false
          )
  )
  @Test
  @DisplayName("Active Category is Present In Categories List")
  void activeCategoryShouldPresentInCategoriesList(CategoryJson category){
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .successLogin(category.username(), "12345")
            .checkThatMainPageLoaded();

    System.out.println("New category " + category.name());
    Selenide.open(CFG.frontUrl()+"profile", ProfilePage.class)
            .checkThatItIsProfilePage()
            .checkCategoryExists(category.name());
  }

}
