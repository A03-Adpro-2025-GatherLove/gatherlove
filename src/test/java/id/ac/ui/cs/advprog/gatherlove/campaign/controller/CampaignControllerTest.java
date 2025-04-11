package id.ac.ui.cs.advprog.gatherlove.campaign.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.User;
import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.service.CampaignService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CampaignController.class)
public class CampaignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CampaignService campaignService;

    @Test
    @DisplayName("[RED] Should display create campaign form")
    @WithMockUser(username = "user1")
    void testShowCreateForm_ReturnsHtmlPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/campaign/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("campaign/create"))
                .andExpect(model().attributeExists("campaignDto"));
    }

    @Test
    @DisplayName("[RED] Should display list of campaigns owned by current user")
    @WithMockUser(username = "user1")
    void testShowMyCampaigns_ShouldReturnMyCampaignsView() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/campaign/my"))
                .andExpect(status().isOk())
                .andExpect(view().name("campaign/my"))
                .andExpect(model().attributeExists("campaignList"));
    }
}
