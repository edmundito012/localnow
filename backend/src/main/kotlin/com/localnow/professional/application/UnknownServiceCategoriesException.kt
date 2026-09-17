package com.localnow.professional.application

class UnknownServiceCategoriesException(codes: Set<String>) :
    RuntimeException("Unknown or inactive service categories: " + codes.sorted().joinToString())
