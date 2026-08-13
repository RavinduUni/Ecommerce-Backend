package com.ecom.mapper;

import com.ecom.dto.CartItem;
import com.ecom.entity.CartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CartItemMapper {
    CartItem toProductDto(CartItemEntity entity);
    CartItemEntity toProductEntity(CartItem cartItemDto);
    void updateCartItemEntityFromDto(CartItem updatedCartItem, @MappingTarget CartItemEntity existingCartItem);
}
