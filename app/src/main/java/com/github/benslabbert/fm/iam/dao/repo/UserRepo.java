package com.github.benslabbert.fm.iam.dao.repo;

import com.github.benslabbert.fm.iam.dao.entity.User;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface UserRepo extends CrudRepository<User, UUID> {
  // use on queries    @io.micronaut.context.annotation.Executable
}
