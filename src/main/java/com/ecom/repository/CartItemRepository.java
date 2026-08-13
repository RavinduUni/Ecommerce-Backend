package com.ecom.repository;

import com.ecom.entity.CartItemEntity;
import com.ecom.entity.ProductEntity;
import com.ecom.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    CartItemEntity findByUserAndProduct(UserEntity userEntity, ProductEntity productEntity);

}
