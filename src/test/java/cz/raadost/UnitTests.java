package cz.raadost;

import static org.testng.Assert.assertNotNull;

import cz.raadost.dataSource.ContentEntity;
import cz.raadost.dataSource.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@SpringBootTest
public class UnitTests extends AbstractTestNGSpringContextTests {

  @Autowired private ContentService contentService;

  /*  @Test
  public void displayAll() {
    var entities =contentService.findAll();
      for(ContentEntity e : entities) {
        System.out.println(e);
      }
  }*/
  @Test
  public void findById() {
    var entity = contentService.findById(1);
    System.out.println(entity);
  }
}
