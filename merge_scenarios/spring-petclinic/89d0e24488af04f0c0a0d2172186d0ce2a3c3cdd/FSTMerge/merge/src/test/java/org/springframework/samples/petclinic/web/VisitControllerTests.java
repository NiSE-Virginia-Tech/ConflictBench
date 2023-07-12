package org.springframework.samples.petclinic.web; 

import org.junit.Before; 
import org.junit.Test; 
import org.junit.runner.RunWith; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.samples.petclinic.model.Pet; 
import org.springframework.samples.petclinic.service.ClinicService; 
import org.springframework.test.context.ContextConfiguration; 
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner; 
import org.springframework.test.context.web.WebAppConfiguration; 
import org.springframework.test.web.servlet.MockMvc; 
import org.springframework.test.web.servlet.setup.MockMvcBuilders; 

import static org.mockito.BDDMockito.given; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; 
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*; 
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest; 
import org.springframework.boot.test.mock.mockito.MockBean; 

/**
 * Test class for {@link VisitController}
 *
 * @author Colin But
 */
~~FSTMerge~~ @RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(VisitController.class)
public ##FSTMerge## @RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/business-config.xml", "classpath:spring/tools-config.xml", "classpath:spring/mvc-core-config.xml"})
@WebAppConfiguration
@ActiveProfiles("spring-data-jpa")
public ##FSTMerge## @RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/mvc-core-config.xml", "classpath:spring/mvc-test-config.xml"})
@WebAppConfiguration
public  class  VisitControllerTests {
	

    private static final int TEST_PET_ID = 1;

	

    @Autowired
    private VisitController visitController;

	

    ~~FSTMerge~~ @MockBean
    private ClinicService clinicService; ##FSTMerge## ##FSTMerge## @Autowired
    private ClinicService clinicService;

	

    @Autowired
    private MockMvc mockMvc;

	

    <<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/temp/fstmerge_tmp1678913832615/fstmerge_var1_8981554489206418927
=======
@Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();

        given(this.clinicService.findPetById(TEST_PET_ID)).willReturn(new Pet());
    }
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/temp/fstmerge_tmp1678913832615/fstmerge_var2_4965142559669116058


	


    @Test
    public void testInitNewVisitForm() throws Exception {
        mockMvc.perform(get("/owners/*/pets/{petId}/visits/new", TEST_PET_ID))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdateVisitForm"));
    }


	

    @Test
    public void testProcessNewVisitFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/*/pets/{petId}/visits/new", TEST_PET_ID)
            .param("name", "George")
            .param("description", "Visit Description")
        )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/{ownerId}"));
    }


	

    @Test
    public void testProcessNewVisitFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/*/pets/{petId}/visits/new", TEST_PET_ID)
            .param("name", "George")
        )
            .andExpect(model().attributeHasErrors("visit"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdateVisitForm"));
    }


	

    @Test
    public void testShowVisits() throws Exception {
        mockMvc.perform(get("/owners/*/pets/{petId}/visits", TEST_PET_ID))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("visits"))
            .andExpect(view().name("visitList"));
    }


	

    @Before
    public void init() {
        given(this.clinicService.findPetById(TEST_PET_ID)).willReturn(new Pet());
    }


}
