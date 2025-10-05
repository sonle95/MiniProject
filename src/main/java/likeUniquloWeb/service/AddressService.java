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
import likeUniquloWeb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    public AddressResponse createAddress(AddressRequest request, String token){
        Address address = addressMapper.toEntity(request);
        User user = authenticationService.getUserFromToken(token);
        address.setUser(user);
        if (request.isAddressDefault()) {
            addressRepository.clearDefaultAddress(user, null);
            address.setAddressDefault(true);
        }
        Address savedAddress = addressRepository.save(address);
       return addressMapper.toDto(savedAddress);

    }

    public List<AddressResponse> getAddresses(){
        return addressRepository.findAll()
                .stream().map(addressMapper::toDto).toList();
    }

    public List<AddressResponse> getMyAddresses(String token) {
        User user = authenticationService.getUserFromToken(token);
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
    public AddressResponse update(Long addressId, AddressRequest request, String token){
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        User currentUser = authenticationService.getUserFromToken(token);
        if (!address.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        address.setDistrict(request.getDistrict());
        address.setPhone(request.getPhone());
        address.setWard(request.getWard());
        address.setProvince(request.getProvince());
        address.setStreet(request.getStreet());
        if (request.isAddressDefault()) {
            addressRepository.findByUser(currentUser).forEach(a -> {
                if (!a.getId().equals(addressId) && a.isAddressDefault()) {
                    a.setAddressDefault(false);
                    addressRepository.save(a);
                }
            });
        }

        return addressMapper.toDto(addressRepository.save(address));    }

    public void delete(Long addressId, String token){
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        User currentUser = authenticationService.getUserFromToken(token);
        if (!address.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
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


}

