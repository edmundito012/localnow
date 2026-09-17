package com.localnow.servicerequest.persistence

import com.localnow.servicerequest.domain.ServiceRequest
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ServiceRequestRepository : JpaRepository<ServiceRequest, UUID>
