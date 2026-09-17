package com.localnow.servicerequest.application

class ServiceRequestNotFoundException :
    RuntimeException("Service request was not found")
