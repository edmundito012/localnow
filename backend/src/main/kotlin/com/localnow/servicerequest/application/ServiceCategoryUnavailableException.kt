package com.localnow.servicerequest.application

class ServiceCategoryUnavailableException(code: String) :
    RuntimeException("Service category is unknown or inactive: " + code)
