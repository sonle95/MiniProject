package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.UserRequest;
import likeUniquloWeb.dto.request.UserUpdateRequest;
import likeUniquloWeb.dto.response.AddressResponse;
import likeUniquloWeb.dto.response.UserResponse;
import likeUniquloWeb.service.AddressService;
import likeUniquloWeb.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "*")
public class UserController {
    UserService userService;
    AddressService addressService;

    @PostMapping
    public UserResponse create(@RequestBody UserRequest request){
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAll(){
        return userService.getAll();
    }

    @PutMapping("/{userId}")
    public UserResponse update(@PathVariable Long userId, @RequestBody UserUpdateRequest request){
        return userService.updateUser(userId,request);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId){
        userService.delete(userId);
    }


    @GetMapping("/{userId}")
    public UserResponse findUserById(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }

    @GetMapping("/page")
    public Page<UserResponse> getUsersByPageAndSearch(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "") String keySearch,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return userService.getUsersByPageAndSearch(page, size, keySearch, sortDir);
    }

    @GetMapping("/myInfo")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public UserResponse getMyInfo(
            @RequestHeader("Authorization") String token
    ) {
        String actualToken = token.replace("Bearer ", "");
        UserResponse response = userService.getMyInfo(actualToken);
        return response;
    }

    @PatchMapping("/{userId}/toggle-active")
    public UserResponse toggleActive(@PathVariable Long userId) {
        return userService.toggleUserActive(userId);
    }

    @PatchMapping("/{userId}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse setActive(@PathVariable Long userId, @RequestParam boolean status) {
        return userService.setUserActive(userId, status);
    }

}
