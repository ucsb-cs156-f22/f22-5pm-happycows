package edu.ucsb.cs156.happiercows.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;
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

import edu.ucsb.cs156.happiercows.entities.CommonsPlus;
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

		expectedCowDeaths.add(cowDeath1);
		when(cowDeathRepository.findAllByCommonsId((long)1)).thenReturn(expectedCowDeaths);
		MvcResult response = mockMvc.perform(get("/api/cowdeath/bycommons?commonsId=1").contentType("application/json"))
			.andExpect(status().isOk()).andReturn();

		verify(cowDeathRepository, times(1)).findAllByCommonsId((long)1);

		String responseString = response.getResponse().getContentAsString();
		List<CowDeath> actualCowDeath = objectMapper.readValue(responseString, new TypeReference<List<CowDeath>>() {
});
		assertEquals(actualCowDeath, expectedCowDeaths);
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

		expectedCowDeaths.add(cowDeath1);
		when(cowDeathRepository.findAllByCommonsIdAndUserId((long)1, (long)1)).thenReturn(expectedCowDeaths);
		MvcResult response = mockMvc.perform(get("/api/cowdeath/byusercommons?commonsId=1&userId=1").contentType("application/json"))
			.andExpect(status().isOk()).andReturn();

		verify(cowDeathRepository, times(1)).findAllByCommonsIdAndUserId((long)1, (long)1);

		String responseString = response.getResponse().getContentAsString();
		List<CowDeath> actualCowDeath = objectMapper.readValue(responseString, new TypeReference<List<CowDeath>>() {});
		assertEquals(actualCowDeath, expectedCowDeaths);
	}
	

}
