package cz.raadost;


import cz.raadost.service.content.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@SpringBootTest
public class UnitTests extends AbstractTestNGSpringContextTests {

  @Autowired private Content content;

  /*  @Test
  public void displayAll() {
    var entities =contentService.findAll();
      for(ContentEntity e : entities) {
        System.out.println(e);
      }
  }*/
  @Test
  public void findById() {
    var entity = content.findById(1);
    System.out.println(entity);
  }
}
