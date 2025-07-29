package com.mango.products.infrastructure.adapter.persistence.jpa;

import com.mango.products.infrastructure.adapter.persistence.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPriceRepository extends JpaRepository<PriceEntity, Long> {

    @Query("SELECT p FROM PriceEntity p WHERE p.productEntity.id = :productId AND p.initDate <= :targetDate AND (p.endDate IS NULL OR p.endDate >= :targetDate)")
    Optional<PriceEntity> findCurrentPriceByProductIdAndDate(@Param("productId") UUID productId, @Param("targetDate") LocalDate targetDate);

    @Query(value = "SELECT COUNT(*) FROM prices p WHERE p.product_id = :productId AND (p.init_date <= COALESCE(CAST(:newEndDate AS DATE), :maxDate) AND CAST(:newInitDate AS DATE) <= COALESCE(p.end_date, :maxDate))", nativeQuery = true)
    long countOverlappingPrices(@Param("productId") UUID productId, @Param("newInitDate") String newInitDate, @Param("newEndDate") String newEndDate, @Param("maxDate") LocalDate maxDate);

    List<PriceEntity> findByProductEntity_IdOrderByInitDateAsc(UUID productId);
}