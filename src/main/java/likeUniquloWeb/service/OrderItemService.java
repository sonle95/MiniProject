package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.OrderItemRequest;
import likeUniquloWeb.dto.response.OrderItemResponse;
import likeUniquloWeb.entity.OrderItems;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.OrderItemMapper;
import likeUniquloWeb.repository.OrderItemsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderItemService {

    OrderItemsRepository itemsRepository;
    OrderItemMapper itemMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public OrderItemResponse createOrderItem(OrderItemRequest request){
        OrderItems orderItem = itemMapper.itemToEntity(request);
        return itemMapper.itemToDto(itemsRepository.save(orderItem));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<OrderItemResponse> getAllOrderItems(){
        return itemsRepository.findAll().stream()
                .map(itemMapper::itemToDto).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public OrderItemResponse getById(Long id){
        OrderItems orderItems = itemsRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.ITEM_NOT_FOUND));
        return itemMapper.itemToDto(orderItems);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void deleteById(Long id){
        itemsRepository.deleteById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public OrderItemResponse updateOrderItem(Long id, OrderItemRequest request){

        OrderItems orderItems = itemsRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.ITEM_NOT_FOUND));
        itemMapper.update(request, orderItems);
        itemsRepository.save(orderItems);
        return itemMapper.itemToDto(orderItems);
    }

}
