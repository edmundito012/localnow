package com.localnow.professional.persistence

import com.localnow.professional.domain.ProfessionalProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ProfessionalProfileRepository : JpaRepository<ProfessionalProfile, UUID> {
    fun existsByPhone(phone: String): Boolean

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        value = """
            UPDATE professional_profile
            SET location = CAST(
                    ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
                    AS geography
                ),
                service_radius_m = :serviceRadiusMeters,
                updated_at = CURRENT_TIMESTAMP
            WHERE user_id = :userId
        """,
        nativeQuery = true,
    )
    fun updateServiceArea(
        @Param("userId") userId: UUID,
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("serviceRadiusMeters") serviceRadiusMeters: Int,
    ): Int

    @Query(
        value = """
            SELECT
                p.user_id AS "userId",
                p.display_name AS "displayName",
                ST_Distance(
                    p.location,
                    CAST(
                        ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
                        AS geography
                    )
                ) AS "distanceMeters"
            FROM professional_profile p
            JOIN professional_service_category pc
                ON pc.professional_user_id = p.user_id
            JOIN service_category c
                ON c.code = pc.category_code
            WHERE p.location IS NOT NULL
              AND c.active = TRUE
              AND pc.category_code = :categoryCode
              AND ST_DWithin(
                    p.location,
                    CAST(
                        ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
                        AS geography
                    ),
                    LEAST(p.service_radius_m, :radiusMeters)
              )
            ORDER BY "distanceMeters"
            LIMIT 20
        """,
        nativeQuery = true,
    )
    fun searchNearby(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("radiusMeters") radiusMeters: Int,
        @Param("categoryCode") categoryCode: String,
    ): List<ProfessionalSearchProjection>
}
