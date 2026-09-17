package com.localnow.professional.application

class ProfessionalProfileNotFoundException :
    RuntimeException("The authenticated user does not have a professional profile")
