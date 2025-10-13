package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.PermissionRequest;
import likeUniquloWeb.dto.response.PermissionResponse;
import likeUniquloWeb.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "*")
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse create(@RequestBody PermissionRequest request){
        return permissionService.createPermission(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionResponse> getAll(){
        return permissionService.getAll();
    }

    @DeleteMapping("/{permissionName}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable String permissionName){
        permissionService.delete(permissionName);
    }
}
