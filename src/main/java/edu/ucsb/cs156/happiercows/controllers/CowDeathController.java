package edu.ucsb.cs156.happiercows.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ucsb.cs156.happiercows.errors.EntityNotFoundException;

import edu.ucsb.cs156.happiercows.entities.Commons;
import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.entities.CowDeath;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.CowDeathRepository;


@Slf4j
@Api(description = "Cow Death")
@RequestMapping("/api/cowdeath")
@RestController
public class CowDeathController extends ApiController {
    
    @Autowired
    private CowDeathRepository cowDeathRepository;

    @Autowired
    private CommonsRepository commonsRepository;

    @Autowired
    private UserCommonsRepository userCommonsRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "Create a new CowDeath entity")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity<String> createCowDeath(
        @ApiParam("commons_id") @RequestParam long commonsId,
        @ApiParam("user_id") @RequestParam long userId,
        @ApiParam("cowsKilled") @RequestParam Integer cowsKilled,
        @ApiParam("avgHealth") @RequestParam double avgHealth) throws JsonProcessingException {
        
        log.info("createCowDeath()...");

        userCommonsRepository.findByCommonsIdAndUserId(commonsId, userId)
            .orElseThrow(() -> new EntityNotFoundException(UserCommons.class, "commonsId", commonsId, "userId", userId));

        CowDeath createdCowDeath = new CowDeath();
        createdCowDeath.setCommonsId(commonsId);
        createdCowDeath.setUserId(userId);
        createdCowDeath.setCowsKilled(cowsKilled);
        createdCowDeath.setAvgHealth(avgHealth);
        
        CowDeath savedCowDeath = cowDeathRepository.save(createdCowDeath);
        String body = mapper.writeValueAsString(savedCowDeath);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "List cow deaths for a given commons")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/bycommons")
    public ResponseEntity<String> listCommonsCowDeaths(
        @ApiParam("commons_id") @RequestParam Long commonsId) throws JsonProcessingException {
        
        log.info("listCommonsCowDeaths()...");
        
        commonsRepository.findById(commonsId)
            .orElseThrow(
                () -> new EntityNotFoundException(Commons.class, commonsId));

        Iterable<CowDeath> cowDeathIter = cowDeathRepository.findAllByCommonsId(commonsId);
        
        ArrayList<CowDeath> cowDeathList = new ArrayList<CowDeath>();
        cowDeathIter.forEach(cowDeathList::add);
        
        String body = mapper.writeValueAsString(cowDeathList);
        return ResponseEntity.ok().body(body);

    }

    @ApiOperation(value = "List cow deaths for a given commons and user")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/byusercommons")
    public ResponseEntity<String> listUserCommonsCowDeaths(
        @ApiParam("commons_id") @RequestParam Long commonsId,
        @ApiParam("user_id") @RequestParam Long userId) throws JsonProcessingException {
        
        log.info("listUserCommonsCowDeaths()...");
        
        userCommonsRepository.findByCommonsIdAndUserId(commonsId, userId)
            .orElseThrow(
                () -> new EntityNotFoundException(UserCommons.class, "commonsId", commonsId, "userId", userId));

        Iterable<CowDeath> cowDeathIter = cowDeathRepository.findAllByCommonsIdAndUserId(commonsId, userId);
        
        ArrayList<CowDeath> cowDeathList = new ArrayList<CowDeath>();
        cowDeathIter.forEach(cowDeathList::add);
        
        String body = mapper.writeValueAsString(cowDeathList);
        return ResponseEntity.ok().body(body);

    }
}
