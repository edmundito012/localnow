package com.localnow.professional.application

class ProfessionalProfileAlreadyExistsException :
    RuntimeException("The authenticated user already has a professional profile")
