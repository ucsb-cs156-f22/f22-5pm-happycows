package edu.ucsb.cs156.happiercows.entities;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "cowdeath")
@EntityListeners(AuditingEntityListener.class)

public class CowDeath {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name="commons_id")
  private long commonsId;

  @Column(name="user_id")
  private long userId;
  
  @CreatedDate
  private ZonedDateTime createdAt;
  private Integer cowsKilled; 
  private double avgHealth; 

}
