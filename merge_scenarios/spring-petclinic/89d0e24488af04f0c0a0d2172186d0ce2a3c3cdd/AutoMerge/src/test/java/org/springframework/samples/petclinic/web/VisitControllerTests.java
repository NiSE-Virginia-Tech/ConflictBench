package org.springframework.samples.petclinic.web;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link VisitController}
 *
 * @author Colin But
 */
@RunWith(value = SpringJUnit4ClassRunner.class) @WebMvcTest(value = 
<<<<<<< left/src/test/java/org/springframework/samples/petclinic/web/VisitControllerTests.java
VisitController.class
=======
{ "classpath:spring/mvc-core-config.xml", "classpath:spring/mvc-test-config.xml" }
>>>>>>> right/src/test/java/org/springframework/samples/petclinic/web/VisitControllerTests.java
) public class VisitControllerTests {
  private static final int TEST_PET_ID = 1;

  @Autowired private VisitController visitController;

  @Autowired private MockMvc mockMvc;

  @
<<<<<<< left/src/test/java/org/springframework/samples/petclinic/web/VisitControllerTests.java
  MockBean
=======
  Autowired
>>>>>>> right/src/test/java/org/springframework/samples/petclinic/web/VisitControllerTests.java
   private ClinicService clinicService;

  @Before public void init() {
    given(this.clinicService.findPetById(TEST_PET_ID)).willReturn(new Pet());
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Before public void setup() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
    given(this.clinicService.findPetById(TEST_PET_ID)).willReturn(new Pet());
  }
>>>>>>> right/src/test/java/org/springframework/samples/petclinic/web/VisitControllerTests.java


  @Test public void testInitNewVisitForm() throws Exception {
    mockMvc.perform(get("/owners/*/pets/{petId}/visits/new", TEST_PET_ID)).andExpect(status().isOk()).andExpect(view().name("pets/createOrUpdateVisitForm"));
  }

  @Test public void testProcessNewVisitFormSuccess() throws Exception {
    mockMvc.perform(post("/owners/*/pets/{petId}/visits/new", TEST_PET_ID).param("name", "George").param("description", "Visit Description")).andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/owners/{ownerId}"));
  }

  @Test public void testProcessNewVisitFormHasErrors() throws Exception {
    mockMvc.perform(post("/owners/*/pets/{petId}/visits/new", TEST_PET_ID).param("name", "George")).andExpect(model().attributeHasErrors("visit")).andExpect(status().isOk()).andExpect(view().name("pets/createOrUpdateVisitForm"));
  }

  @Test public void testShowVisits() throws Exception {
    mockMvc.perform(get("/owners/*/pets/{petId}/visits", TEST_PET_ID)).andExpect(status().isOk()).andExpect(model().attributeExists("visits")).andExpect(view().name("visitList"));
  }
}