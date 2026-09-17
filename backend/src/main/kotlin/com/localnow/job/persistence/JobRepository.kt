package com.localnow.job.persistence

import com.localnow.job.domain.Job
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JobRepository : JpaRepository<Job, UUID>
