package edu.ucsb.cs156.happiercows.jobs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;

import edu.ucsb.cs156.happiercows.entities.User;
import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.entities.Commons;

import edu.ucsb.cs156.happiercows.services.jobs.JobContext;
import edu.ucsb.cs156.happiercows.services.jobs.JobContextConsumer;
import lombok.Builder;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Builder
public class MilkTheCowsJob implements JobContextConsumer {
    @Autowired
    private UserCommonsRepository userCommonsRepository;

    @Autowired
    private CommonsRepository commonsRepository;

    @Override
    public void accept(JobContext ctx) throws Exception {
        // ctx.log("Starting to milk the cows");
        // ctx.log("This is where the code to milk the cows will go.");
        // ctx.log("Cows have been milked!");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        if (LocalTime.now().equals("04:00:00")) {
            Iterable<Commons> allCommons = commonsRepository.findAll();
            for (Commons commons : allCommons) {
                Iterable<UserCommons> allUserCommons = userCommonsRepository.findByCommonsId(commons.getId());
                double commonsMilkPrice = commons.getMilkPrice();

                for (UserCommons userCommons: allUserCommons) {
                    long userId = userCommons.getUserId();
                    int userNumCows = userCommons.getNumOfCows();
                    double userCowHealth = userCommons.getCowHealth();

                    double currentWealth = userCommons.getTotalWealth();

                    double newWealth = currentWealth + commonsMilkPrice*userNumCows*userCowHealth;
                    userCommons.setTotalWealth(newWealth);
                }
            }
        }
    }
}
