package com.github.benslabbert.fm.iam.dao.entity;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Version;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity<T> implements Identifiable<T>, Versioned {

  @Version
  @MappedProperty("version")
  private Integer version;

  @DateCreated
  @MappedProperty("created")
  private Instant created;

  @DateUpdated
  @MappedProperty("updated")
  private Instant updated;
}
