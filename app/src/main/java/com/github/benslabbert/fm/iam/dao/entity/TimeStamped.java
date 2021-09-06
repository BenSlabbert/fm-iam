package com.github.benslabbert.fm.iam.dao.entity;

import java.time.Instant;

public interface TimeStamped {

  Instant getCreated();

  Instant getUpdated();
}
