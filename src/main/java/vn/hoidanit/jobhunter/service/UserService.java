package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.DTO.Meta;
import vn.hoidanit.jobhunter.DTO.ResCreateUserDTO;
import vn.hoidanit.jobhunter.DTO.ResUserDTO;
import vn.hoidanit.jobhunter.DTO.ResultPaginationDTO;
import vn.hoidanit.jobhunter.model.Company;
import vn.hoidanit.jobhunter.model.Role;
import vn.hoidanit.jobhunter.model.User;
import vn.hoidanit.jobhunter.reponsitory.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User handleCreateUser(User user) {
        if (user.getCompany() != null) {
            Company company = this.companyService.getCompanyById(user.getCompany().getId());
            if (company != null) {
                user.setCompany(company);
            } else {
                user.setCompany(null);
            }

        }
        if (user.getRole() != null) {
            Role role = this.roleService.getRoleById(user.getRole().getId());
            if (role != null) {
                user.setRole(role);
            } else {
                user.setRole(null);
            }
        }
        return this.userRepository.save(user);

    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User getUserById(long id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public ResultPaginationDTO getAllUser(Specification<User> specification, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());
        rs.setMeta(meta);
        List<ResUserDTO> listUser = pageUser.getContent().stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());
        rs.setResult(listUser);

        return rs;
    }

    public User updateUser(User user) {
        User currentUser = this.getUserById(user.getId());
        if (currentUser != null) {
            currentUser.setAge(user.getAge());
            currentUser.setName(user.getName());
            currentUser.setAddress(user.getAddress());
            currentUser.setGender(user.getGender());

            if (user.getCompany() != null) {
                Company company = this.companyService.getCompanyById(user.getCompany().getId());
                if (company != null) {
                    currentUser.setCompany(company);
                } else {
                    currentUser.setCompany(null);
                }
            }
            if (user.getRole() != null) {
                Role role = this.roleService.getRoleById(user.getRole().getId());
                if (role != null) {
                    currentUser.setRole(role);
                } else {
                    currentUser.setRole(null);
                }
            }
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User getUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser company = new ResCreateUserDTO.CompanyUser();
        res.setId(user.getId());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            res.setCompany(company);
        }
        return res;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
        ResUserDTO.CompanyUser companyUser = new ResUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompany(companyUser);
        }
        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.getUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
