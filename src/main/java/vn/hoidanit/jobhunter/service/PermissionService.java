package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.DTO.Meta;
import vn.hoidanit.jobhunter.DTO.ResultPaginationDTO;
import vn.hoidanit.jobhunter.model.Permission;
import vn.hoidanit.jobhunter.reponsitory.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(), permission.getApiPath(),
                permission.getMethod());
    }

    public Permission createPermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission getPermissionById(long id) {
        Optional<Permission> currentPermission = this.permissionRepository.findById(id);
        if (currentPermission.isPresent()) {
            return currentPermission.get();
        }
        return null;
    }

    public Permission updatePermission(Permission permission) {
        Permission currentPermission = this.getPermissionById(permission.getId());
        if (currentPermission != null) {
            currentPermission.setName(permission.getName());
            currentPermission.setApiPath(permission.getApiPath());
            currentPermission.setModule(permission.getModule());
            currentPermission.setMethod(permission.getMethod());
            currentPermission = this.permissionRepository.save(currentPermission);
            return currentPermission;
        }
        return null;
    }

    public void deletePermission(long id) {
        // delete permission_role
        Optional<Permission> permission = this.permissionRepository.findById(id);
        Permission currentPermission = permission.get();
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));

        // delete permission
        this.permissionRepository.delete(currentPermission);
    }

    public ResultPaginationDTO getAllPermission(Specification<Permission> specification, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pagePermission.getTotalPages());
        meta.setTotal(pagePermission.getTotalElements());
        rs.setMeta(meta);
        rs.setResult(pagePermission.getContent());
        return rs;
    }
}
