package com.localnow.professional.application

import com.localnow.identity.domain.UserRole
import com.localnow.identity.persistence.UserRepository
import com.localnow.professional.domain.ProfessionalProfile
import com.localnow.professional.persistence.ProfessionalProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateProfessionalProfileService(
    private val userRepository: UserRepository,
    private val professionalProfileRepository: ProfessionalProfileRepository,
) {

    @Transactional
    fun create(command: CreateProfessionalProfileCommand): ProfessionalProfile {
        if (professionalProfileRepository.existsById(command.userId)) {
            throw ProfessionalProfileAlreadyExistsException()
        }

        val normalizedPhone = command.phone.trim()
        if (professionalProfileRepository.existsByPhone(normalizedPhone)) {
            throw ProfessionalPhoneAlreadyRegisteredException()
        }

        val user = userRepository.findById(command.userId)
            .orElseThrow { IllegalArgumentException("Authenticated user does not exist") }

        val profile = ProfessionalProfile.create(
            userId = user.id,
            displayName = command.displayName,
            phone = normalizedPhone,
            bio = command.bio,
        )
        user.addRole(UserRole.PROFESSIONAL)

        return professionalProfileRepository.save(profile)
    }
}
