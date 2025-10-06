package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.PermissionRequest;
import likeUniquloWeb.dto.response.PermissionResponse;
import likeUniquloWeb.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;
    @PostMapping
    public PermissionResponse create(@RequestBody PermissionRequest request){
        return permissionService.createPermission(request);
    }

    @GetMapping
    public List<PermissionResponse> getAll(){
        return permissionService.getAll();
    }

    @DeleteMapping("/{permissionName}")
    public void delete(@PathVariable String permissionName){
        permissionService.delete(permissionName);
    }
}
