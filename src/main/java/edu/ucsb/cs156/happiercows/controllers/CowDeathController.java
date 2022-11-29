package edu.ucsb.cs156.happiercows.controllers;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.*;
import java.util.stream.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import lombok.extern.slf4j.Slf4j;
import springfox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ucsb.cs156.happiercows.entities.Commons;
import edu.ucsb.cs156.happiercows.entities.CommonsPlus;
import edu.ucsb.cs156.happiercows.entities.CowDeath;
import edu.ucsb.cs156.happiercows.entities.User;
import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.errors.EntityNotFoundException;
import edu.ucsb.cs156.happiercows.models.CreateCommonsParams;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.CowDeathRepository;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;
import edu.ucsb.cs156.happiercows.controllers.ApiController;

@Slf4j
@Api(description = "Cow Death")
@RequestMapping("/api/cowdeath")
@RestController
public class CowDeathController extends ApiController {
	
	@Autowired
	private CowDeathRepository cowDeathRepository;

	@Autowired
	ObjectMapper mapper;

	@ApiOperation(value = "Create a new CowDeath entity")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("")
	public ResponseEntity<String> createCowDeath(
		@ApiParam("id") @RequestParam long id,
		@ApiParam("commons_id") @RequestParam long commonsId,
		@ApiParam("user_id") @RequestParam long userId,
		@ApiParam("zonedDateTime") @RequestParam("zonedDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime zonedDateTime,
		@ApiParam("cowsKilled") @RequestParam Integer cowsKilled,
		@ApiParam("avgHealth") @RequestParam double avgHealth) throws JsonProcessingException {
		
		CowDeath createdCowDeath = new CowDeath(id, commonsId, userId, zonedDateTime, cowsKilled, avgHealth);
		CowDeath savedCowDeath = cowDeathRepository.save(createdCowDeath);
		String body = mapper.writeValueAsString(savedCowDeath);
		return ResponseEntity.ok().body(body);
	}

	@ApiOperation(value = "List cow deaths for a given commons")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/bycommons")
	public ResponseEntity<String> listCowDeaths(
		@ApiParam("commons_id") @RequestParam Long commonsId) throws JsonProcessingException {
		
		Iterable<CowDeath> cowDeathIter = cowDeathRepository.findAllByCommonsId(commonsId);
		
		ArrayList<CowDeath> cowDeathList = new ArrayList<CowDeath>();
		cowDeathIter.forEach(cowDeathList::add);
		
		String body = mapper.writeValueAsString(cowDeathList);
		return ResponseEntity.ok().body(body);

	}

	@ApiOperation(value = "List cow deaths for a given commons and user")
	@GetMapping("/byusercommons")
	public ResponseEntity<String> listUserCowDeaths(
		@ApiParam("commons_id") @RequestParam Long commonsId,
		@ApiParam("user_id") @RequestParam Long userId) throws JsonProcessingException {
		
		Iterable<CowDeath> cowDeathIter = cowDeathRepository.findAllByCommonsIdAndUserId(commonsId, userId);
		
		ArrayList<CowDeath> cowDeathList = new ArrayList<CowDeath>();
		cowDeathIter.forEach(cowDeathList::add);
		
		String body = mapper.writeValueAsString(cowDeathList);
		return ResponseEntity.ok().body(body);

	}
}
