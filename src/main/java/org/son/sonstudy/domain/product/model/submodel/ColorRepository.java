package org.son.sonstudy.domain.product.model.submodel;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColorRepository extends JpaRepository<Color, Long> {
    boolean existsByHexCode(String hexCode);

    Optional<Color> findByHexCode(String colorHexCode);
}
