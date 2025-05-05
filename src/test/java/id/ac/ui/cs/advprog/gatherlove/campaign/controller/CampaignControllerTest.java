package id.ac.ui.cs.advprog.gatherlove.campaign.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.User;
import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
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

import static org.mockito.Mockito.when;
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

    @Test
    @DisplayName("[RED] Should display edit form for specific campaign owned by user")
    @WithMockUser(username = "user1")
    void testEditForm_ReturnsEditPage() throws Exception {
        Campaign campaign = new Campaign();
        campaign.setId("abc123");
        campaign.setTitle("Old Title");

        when(campaignService.getCampaignById("abc123")).thenReturn(campaign);

        mockMvc.perform(MockMvcRequestBuilders.get("/campaign/edit/abc123"))
                .andExpect(status().isOk())
                .andExpect(view().name("campaign/edit"))
                .andExpect(model().attributeExists("campaignDto"));
    }

    @Test
    @DisplayName("[RED] Should delete campaign and redirect")
    @WithMockUser
    void testDeleteCampaign() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/campaign/delete/abc123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/campaign/my"));
    }

}
