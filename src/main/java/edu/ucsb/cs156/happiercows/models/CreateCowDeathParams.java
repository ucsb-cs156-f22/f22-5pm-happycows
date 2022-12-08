package edu.ucsb.cs156.happiercows.models;

import java.time.LocalDateTime;
import java.util.Collection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.security.core.GrantedAuthority;

import edu.ucsb.cs156.happiercows.entities.CowDeath;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CreateCowDeathParams {  
  @NumberFormat
  private long commonsId;
  @NumberFormat
  private long userId;
  @DateTimeFormat
  private LocalDateTime zonedDateTime;
  @NumberFormat
  private Integer cowsKilled;
  @NumberFormat
  private double avgHealth;
}