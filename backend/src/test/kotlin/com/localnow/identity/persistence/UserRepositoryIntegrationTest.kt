package com.localnow.identity.persistence

import com.localnow.TestcontainersConfiguration
import com.localnow.identity.domain.User
import com.localnow.identity.domain.UserRole
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class UserRepositoryIntegrationTest(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val entityManager: EntityManager,
) {

    @Test
    fun `persists a normalized email and multiple roles`() {
        val user = User.create(
            email = "  Edmundo@Example.com ",
            passwordHash = "bcrypt-hash",
            roles = setOf(UserRole.CUSTOMER, UserRole.PROFESSIONAL),
        )

        userRepository.saveAndFlush(user)
        entityManager.clear()

        val persisted = userRepository.findByEmail("edmundo@example.com")

        assertThat(persisted).isNotNull
        assertThat(persisted?.email).isEqualTo("edmundo@example.com")
        assertThat(persisted?.roles)
            .containsExactlyInAnyOrder(UserRole.CUSTOMER, UserRole.PROFESSIONAL)
        assertThat(persisted?.enabled).isTrue
    }
}
