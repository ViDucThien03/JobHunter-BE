package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.DTO.ResultPaginationDTO;
import vn.hoidanit.jobhunter.error.IdInvalidException;
import vn.hoidanit.jobhunter.model.Role;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create permision success")
    public ResponseEntity<Role> createRole(@RequestBody Role role) throws IdInvalidException {
        if (this.roleService.isExistsByName(role.getName())) {
            throw new IdInvalidException("Role name is already exist");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.createRole(role));
    }

    @PutMapping("roles")
    @ApiMessage("Update permision success")
    public ResponseEntity<Role> updateRole(@RequestBody Role role) throws IdInvalidException {

        if (this.roleService.getRoleById(role.getId()) == null) {
            throw new IdInvalidException("Role is not exist");
        }
        // if (this.roleService.isExistsByName(role.getName())) {
        // throw new IdInvalidException("Role name is already exist");
        // }

        return ResponseEntity.ok().body(this.roleService.updateRole(role));
    }

    @DeleteMapping("roles/{id}")
    @ApiMessage("Delete permision success")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws IdInvalidException {
        if (this.roleService.getRoleById(id) == null) {
            throw new IdInvalidException("Role is not exist");
        }
        this.roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    @ApiMessage("Get all role success")
    public ResponseEntity<ResultPaginationDTO> getAllPermission(@Filter Specification<Role> specification,
            Pageable pageable) {

        return ResponseEntity.ok().body(this.roleService.getAllRole(specification, pageable));
    }

    @GetMapping("roles/{id}")
    @ApiMessage("Get a role permision success")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id) throws IdInvalidException {
        Role role = this.roleService.getRoleById(id);
        if (this.roleService.getRoleById(id) == null) {
            throw new IdInvalidException("Role is not exist");
        }
        return ResponseEntity.ok().body(role);
    }
}
