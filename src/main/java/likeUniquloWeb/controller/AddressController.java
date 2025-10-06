package likeUniquloWeb.controller;


import likeUniquloWeb.dto.request.AddressRequest;
import likeUniquloWeb.dto.response.AddressResponse;
import likeUniquloWeb.service.AddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@CrossOrigin(origins = "*")
public class AddressController {
    AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(
            @RequestBody AddressRequest request, @RequestHeader("Authorization") String token )
    { AddressResponse response = addressService.createAddress(request, token);
        return ResponseEntity.ok(response); }

    @GetMapping
    public List<AddressResponse> getAll(){
        return addressService.getAddresses();
    }
    @GetMapping("/me/address") public ResponseEntity<List<AddressResponse>> getMyAddresses( @RequestHeader("Authorization") String token )
    { List<AddressResponse> responses = addressService.getMyAddresses(token);
        return ResponseEntity.ok(responses); }
    @GetMapping("/default")
    public AddressResponse getDefaultAddress(
            @RequestHeader("Authorization") String token) {
        String accessToken = token.substring(7);
        AddressResponse address = addressService.getDefaultAddress(accessToken);
        return address;
    }
    @PutMapping("/{id}") public ResponseEntity<AddressResponse> updateAddress( @PathVariable("id") Long id, @RequestBody AddressRequest request,
                                                                               @RequestHeader("Authorization") String token )
    { AddressResponse response = addressService.update(id, request, token);
        return ResponseEntity.ok(response); }

    @DeleteMapping("/{id}") public ResponseEntity<Void> deleteAddress( @PathVariable("id") Long id, @RequestHeader("Authorization") String token )
    { addressService.delete(id, token);
        return ResponseEntity.noContent().build(); }

    @GetMapping("/user/{userId}")
    public List<AddressResponse> getAddressesByUserId(@PathVariable Long userId) {
        return addressService.getAddressesByUserId(userId);
    }
    @GetMapping("/page")
    public Page<AddressResponse> getAddressesByPageAndSearch(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "") String keySearch,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return addressService.getAddressesByPageAndSearch(page, size, keySearch, sortDir);
    }
}
