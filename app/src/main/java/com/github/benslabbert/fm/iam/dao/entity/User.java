package com.github.benslabbert.fm.iam.dao.entity;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Version;
import java.time.Instant;
import java.util.UUID;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@MappedEntity
@NoArgsConstructor
@AllArgsConstructor
public class User implements Identifiable<UUID>, Versioned, TimeStamped {

  @Id
  @MappedProperty(value = "id")
  @GeneratedValue(value = GeneratedValue.Type.AUTO)
  private UUID id;

  @Size(min = 3, max = 255)
  @MappedProperty(value = "name")
  private String name;

  @MappedProperty(value = "password_hash")
  private byte[] passwordHash;

  @MappedProperty(value = "password_salt")
  private byte[] passwordSalt;

  @MappedProperty(value = "locked")
  private Boolean locked;

  @MappedProperty(value = "deleted")
  private Boolean deleted;

  @Version
  @MappedProperty("version")
  private Integer version;

  @DateCreated
  @MappedProperty("created")
  private Instant created;

  @DateUpdated
  @MappedProperty("updated")
  private Instant updated;

  @Builder
  public User(
      String name, byte[] passwordHash, byte[] passwordSalt, boolean locked, boolean deleted) {
    this.name = name;
    this.passwordHash = passwordHash;
    this.passwordSalt = passwordSalt;
    this.locked = locked;
    this.deleted = deleted;
  }
}
