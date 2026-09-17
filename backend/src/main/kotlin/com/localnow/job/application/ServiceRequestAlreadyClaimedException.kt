package com.localnow.job.application

class ServiceRequestAlreadyClaimedException :
    RuntimeException("Service request is no longer open")
