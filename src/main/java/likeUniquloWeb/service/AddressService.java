package likeUniquloWeb.service;

import jakarta.transaction.Transactional;
import likeUniquloWeb.dto.request.AddressRequest;
import likeUniquloWeb.dto.response.AddressResponse;
import likeUniquloWeb.entity.Address;
import likeUniquloWeb.entity.User;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.AddressMapper;
import likeUniquloWeb.repository.AddressRepository;
import likeUniquloWeb.repository.OrderRepository;
import likeUniquloWeb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressService {

    AddressRepository addressRepository;
    AddressMapper addressMapper;
    UserRepository userRepository;
    AuthenticationService authenticationService;
    OrderRepository orderRepository;
    private static final int MAX_ADDRESSES_PER_USER = 3;
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public AddressResponse createAddress(AddressRequest request, String token) {
        User user = authenticationService.getUserFromToken(token);

        long addressCount = addressRepository.countByUser_Id(user.getId());
        if (addressCount >= MAX_ADDRESSES_PER_USER) {
            throw new AppException(ErrorCode.ADDRESS_LIMIT_EXCEEDED);
        }

        Address address = addressMapper.toEntity(request);
        address.setUser(user);

        if (Boolean.TRUE.equals(request.isAddressDefault())) {
            addressRepository.findByUser_IdAndAddressDefaultTrue(user.getId())
                    .forEach(addr -> {
                        addr.setAddressDefault(false);
                        addressRepository.save(addr);
                    });
        }

        return addressMapper.toDto(addressRepository.save(address));
    }

    public List<AddressResponse> getAddresses(){
        return addressRepository.findAll()
                .stream().map(addressMapper::toDto).toList();
    }

    public List<AddressResponse> getMyAddresses(String token) {
        User user = authenticationService.getUserFromToken(token);
        List<Address> addresses = addressRepository.findByUser(user);
        addresses.sort((a1, a2) -> {
            if (Boolean.TRUE.equals(a1.isAddressDefault())) return -1;
            if (Boolean.TRUE.equals(a2.isAddressDefault())) return 1;
            return 0;
        });
        return addressRepository.findByUser(user)
                .stream().map(addressMapper::toDto).toList();
    }

    public AddressResponse getDefaultAddress(String token) {
        User user = authenticationService.getUserFromToken(token);
        Address address = addressRepository
                .findByUserIdAndAddressDefaultTrue(user.getId())
                .orElse(null);
        return address != null ? addressMapper.toDto(address) : null;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public AddressResponse updateAddress(Long id, AddressRequest request, String token) {
        User user = authenticationService.getUserFromToken(token);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        addressMapper.updateAddress(request, address);

        if (Boolean.TRUE.equals(request.isAddressDefault())) {
            addressRepository.findByUser_IdAndAddressDefaultTrue(user.getId())
                    .stream()
                    .filter(addr -> !addr.getId().equals(id))
                    .forEach(addr -> {
                        addr.setAddressDefault(false);
                        addressRepository.save(addr);
                    });
        }

        return addressMapper.toDto(addressRepository.save(address));
    }


    public void delete(Long addressId, String token){
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        User currentUser = authenticationService.getUserFromToken(token);
        if (!address.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        boolean hasOrders = orderRepository.existsByAddressId(addressId);
        if (hasOrders) {
            throw new AppException(ErrorCode.ADDRESS_HAS_ORDER);
        }
        addressRepository.delete(address);
    }

    public List<AddressResponse> getAddressesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return addressRepository.findByUser(user)
                .stream()
                .map(addressMapper::toDto)
                .toList();
    }

    public Page<AddressResponse> getAddressesByPageAndSearch(int page, int size, String keySearch, String sortDir){
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by("id").ascending()
                : Sort.by("id").descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Address> addresses;
        if (keySearch == null || keySearch.trim().isEmpty()) {
            addresses = addressRepository.findAll(pageable);
        } else {
            addresses = addressRepository.searchByUserKeyword(keySearch.trim(), pageable);
        }

        return addresses.map(addressMapper::toDto);
    }


}

