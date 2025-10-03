package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.RoleRequest;
import likeUniquloWeb.dto.response.RoleResponse;
import likeUniquloWeb.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    public RoleResponse create(@RequestBody RoleRequest request){
        return roleService.createRole(request);
    }

    @GetMapping
    public List<RoleResponse> getAll(){
        return roleService.getAll();
    }

    @PutMapping("/{roleName}")
    public RoleResponse update(@PathVariable String roleName, @RequestBody RoleRequest request){
        return roleService.updateRole(roleName,request);
    }
}
