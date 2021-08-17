package com.github.benslabbert.fm.iam.dao.entity;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import java.util.UUID;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@MappedEntity
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity<UUID> {

  @Id
  @GeneratedValue(value = GeneratedValue.Type.AUTO)
  private UUID id;

  @Size(min = 3, max = 255)
  @MappedProperty(value = "name")
  private String name;

  @MappedProperty(value = "password_hash")
  private String passwordHash;
}
