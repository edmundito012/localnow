package com.localnow.identity.application

class EmailAlreadyRegisteredException(email: String) :
    RuntimeException("An account already exists for email: $email")
