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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderItemService {

    OrderItemsRepository itemsRepository;
    OrderItemMapper itemMapper;

    public OrderItemResponse createOrderItem(OrderItemRequest request){
        OrderItems orderItems = itemMapper.itemToEntity(request);
        return itemMapper.itemToDto(itemsRepository.save(orderItems));
    }

    public List<OrderItemResponse> getAllOrderItems(){
        return itemsRepository.findAll().stream()
                .map(itemMapper::itemToDto).toList();
    }

    public OrderItemResponse getById(Long id){
        OrderItems orderItems = itemsRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        return itemMapper.itemToDto(orderItems);
    }

    public void deleteById(Long id){
        itemsRepository.deleteById(id);
    }

    public OrderItemResponse updateOrderItem(Long id, OrderItemRequest request){

        OrderItems orderItems = itemsRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        itemMapper.update(request, orderItems);
        itemsRepository.save(orderItems);
        return itemMapper.itemToDto(orderItems);
    }

}
