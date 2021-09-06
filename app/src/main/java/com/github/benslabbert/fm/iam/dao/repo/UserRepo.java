package com.github.benslabbert.fm.iam.dao.repo;

import com.github.benslabbert.fm.iam.dao.entity.User;
import io.micronaut.context.annotation.Executable;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface UserRepo extends CrudRepository<User, UUID> {

  @Executable
  Optional<User> findByNameEqualsAndLockedFalseAndDeletedFalse(String name);

  @Executable
  Optional<User> findByNameEquals(String name);
}
