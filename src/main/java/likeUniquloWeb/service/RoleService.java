package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.RoleRequest;
import likeUniquloWeb.dto.response.RoleResponse;
import likeUniquloWeb.entity.Role;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.RoleMapper;
import likeUniquloWeb.repository.PermissionRepository;
import likeUniquloWeb.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse createRole(RoleRequest request){
        if(!roleRepository.existsByName(request.getName()));
            Role role = roleMapper.toEntity(request);

        if(request.getPermissionName() != null && !request.getPermissionName().isEmpty()){
            var permissions = permissionRepository.findAllById(request.getPermissionName());
            role.setPermissions(new HashSet<>(permissions));
        }else {
            role.setPermissions(new HashSet<>());
        }
        return roleMapper.toDto(roleRepository.save(role));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> getAll(){
        return roleRepository.findAll()
                .stream().map(roleMapper::toDto).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse updateRole(String roleId, RoleRequest request){
        Role role = roleRepository.findById(roleId)
                .orElseThrow(()-> new AppException(ErrorCode.ROLE_NOT_FOUND));
        if(request.getPermissionName() != null && !request.getPermissionName().isEmpty()){
            var permissions = permissionRepository.findAllById(request.getPermissionName());
            role.setPermissions(new HashSet<>(permissions));
        }else {
            role.setPermissions(new HashSet<>());
        }
        roleMapper.update(request, role);

        return roleMapper.toDto(roleRepository.save(role));
    }
}
