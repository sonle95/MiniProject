package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.PermissionRequest;
import likeUniquloWeb.dto.response.PermissionResponse;
import likeUniquloWeb.entity.Permission;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.PermissionMapper;
import likeUniquloWeb.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService
{
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse createPermission(PermissionRequest request){
        Permission permission = permissionMapper.toEntity(request);
        return permissionMapper.toDto(permissionRepository.save(permission));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionResponse> getAll(){
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toDto).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String permissionId){
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(()->new AppException(ErrorCode.PERMISSION_NOT_FOUND));
        permissionRepository.delete(permission);
    }



}
