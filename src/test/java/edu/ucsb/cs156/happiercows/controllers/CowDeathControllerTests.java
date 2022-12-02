package edu.ucsb.cs156.happiercows.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import edu.ucsb.cs156.happiercows.ControllerTestCase;
import edu.ucsb.cs156.happiercows.entities.Commons;

import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.entities.CowDeath;
import edu.ucsb.cs156.happiercows.models.CreateCowDeathParams;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.CowDeathRepository;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.UserRepository;

@WebMvcTest(controllers = CowDeathController.class)
@AutoConfigureDataJpa
public class CowDeathControllerTests extends ControllerTestCase {
    
    @MockBean
    CowDeathRepository cowDeathRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    CommonsRepository commonsRepository;

    @MockBean
    UserCommonsRepository userCommonsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void createCowDeathTest() throws Exception {
        LocalDateTime someTime = LocalDateTime.parse("2022-03-05T15:50:10");

        CowDeath cowDeath = CowDeath.builder()
            .commonsId(1)
            .userId(1)
            .zonedDateTime(someTime)
            .cowsKilled(10)
            .avgHealth(50)
            .build();

        CreateCowDeathParams parameters = CreateCowDeathParams.builder()
            .commonsId(1)
            .userId(1)
            .zonedDateTime(someTime)
            .cowsKilled(10)
            .avgHealth(50)
            .build();
        
        UserCommons userCommons1 = UserCommons.builder()
            .commonsId(1)
            .userId(1)
            .numOfCows(100)
            .totalWealth(10000)
            .username("user")
            .build();

        when(userCommonsRepository.findByCommonsIdAndUserId(1L, 1L)).thenReturn(Optional.of(userCommons1));

        String requestBody = objectMapper.writeValueAsString(parameters);
        String expectedResponse = objectMapper.writeValueAsString(cowDeath);

        when(cowDeathRepository.save(cowDeath))
            .thenReturn(cowDeath);

        MvcResult response = mockMvc
            .perform(post("/api/cowdeath?commonsId=1&userId=1&zonedDateTime=2022-03-05T15:50:10&cowsKilled=10&avgHealth=50").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(requestBody))
            .andExpect(status().isOk())
            .andReturn();

        verify(cowDeathRepository, times(1)).save(cowDeath);

        String actualResponse = response.getResponse().getContentAsString();
        assertEquals(expectedResponse, actualResponse);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void createCowDeathTest_invalid() throws Exception {
        LocalDateTime someTime = LocalDateTime.parse("2022-03-05T15:50:10");

        CowDeath cowDeath = CowDeath.builder()
            .commonsId(1)
            .userId(1)
            .zonedDateTime(someTime)
            .cowsKilled(10)
            .avgHealth(50)
            .build();

        CreateCowDeathParams parameters = CreateCowDeathParams.builder()
            .commonsId(1)
            .userId(1)
            .zonedDateTime(someTime)
            .cowsKilled(10)
            .avgHealth(50)
            .build();
        
        String requestBody = objectMapper.writeValueAsString(parameters);

        when(cowDeathRepository.save(cowDeath))
            .thenReturn(cowDeath);

        MvcResult response = mockMvc
            .perform(post("/api/cowdeath?commonsId=1&userId=1&zonedDateTime=2022-03-05T15:50:10&cowsKilled=10&avgHealth=50").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(requestBody))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(userCommonsRepository, times(1)).findByCommonsIdAndUserId(1L, 1L);

        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("UserCommons with commonsId 1 and userId 1 not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void getCommonsTest() throws Exception {
        LocalDateTime someTime = LocalDateTime.parse("2022-03-05T15:50:10");

        List<CowDeath> expectedCowDeaths = new ArrayList<CowDeath>();

        CowDeath cowDeath1 = CowDeath.builder()
            .id(1)
            .commonsId(1)
            .userId(1)
            .zonedDateTime(someTime)
            .cowsKilled(10)
            .avgHealth(50)
            .build();

        Commons commons1 = Commons.builder()
            .name("Commons1")
            .cowPrice(100)
            .milkPrice(10)
            .startingBalance(1000)
            .startingDate(someTime)
            .degradationRate(1)
            .showLeaderboard(true)
            .build();

        when(commonsRepository.findById(1L)).thenReturn(Optional.of(commons1));

        expectedCowDeaths.add(cowDeath1);
        when(cowDeathRepository.findAllByCommonsId(1L)).thenReturn(expectedCowDeaths);
        MvcResult response = mockMvc.perform(get("/api/cowdeath/bycommons?commonsId=1").contentType("application/json"))
            .andExpect(status().isOk()).andReturn();

        verify(cowDeathRepository, times(1)).findAllByCommonsId(1L);

        String responseString = response.getResponse().getContentAsString();
        List<CowDeath> actualCowDeath = objectMapper.readValue(responseString, new TypeReference<List<CowDeath>>() {});
        assertEquals(actualCowDeath, expectedCowDeaths);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void getCommonsTest_invalid() throws Exception {
        
        List<CowDeath> expectedCowDeaths = new ArrayList<CowDeath>();

        when(cowDeathRepository.findAllByCommonsId(1L)).thenReturn(expectedCowDeaths);
        MvcResult response = mockMvc.perform(get("/api/cowdeath/bycommons?commonsId=1").contentType("application/json"))
            .andExpect(status().isNotFound()).andReturn();

        verify(commonsRepository, times(1)).findById(1L);

        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("Commons with id 1 not found", json.get("message"));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void getUserCommonsTest() throws Exception {
        LocalDateTime someTime = LocalDateTime.parse("2022-03-05T15:50:10");
        
        List<CowDeath> expectedCowDeaths = new ArrayList<CowDeath>();

        CowDeath cowDeath1 = CowDeath.builder()
            .id(1)
            .commonsId(1)
            .userId(1)
            .zonedDateTime(someTime)
            .cowsKilled(10)
            .avgHealth(50)
            .build();

        UserCommons userCommons1 = UserCommons.builder()
            .commonsId(1)
            .userId(1)
            .numOfCows(100)
            .totalWealth(10000)
            .username("user")
            .build();

        when(userCommonsRepository.findByCommonsIdAndUserId(1L, 1L)).thenReturn(Optional.of(userCommons1));

        expectedCowDeaths.add(cowDeath1);
        when(cowDeathRepository.findAllByCommonsIdAndUserId(1L, 1L)).thenReturn(expectedCowDeaths);
        MvcResult response = mockMvc.perform(get("/api/cowdeath/byusercommons?commonsId=1&userId=1").contentType("application/json"))
            .andExpect(status().isOk()).andReturn();

        verify(cowDeathRepository, times(1)).findAllByCommonsIdAndUserId(1L, 1L);

        String responseString = response.getResponse().getContentAsString();
        List<CowDeath> actualCowDeath = objectMapper.readValue(responseString, new TypeReference<List<CowDeath>>() {});
        assertEquals(actualCowDeath, expectedCowDeaths);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void getUserCommonsTest_invalid() throws Exception {
        
        List<CowDeath> expectedCowDeaths = new ArrayList<CowDeath>();

        when(cowDeathRepository.findAllByCommonsIdAndUserId(1L, 1L)).thenReturn(expectedCowDeaths);
        MvcResult response = mockMvc.perform(get("/api/cowdeath/byusercommons?commonsId=1&userId=1").contentType("application/json"))
            .andExpect(status().isNotFound()).andReturn();

        verify(userCommonsRepository, times(1)).findByCommonsIdAndUserId(1L, 1L);

        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("UserCommons with commonsId 1 and userId 1 not found", json.get("message"));
    }

}
