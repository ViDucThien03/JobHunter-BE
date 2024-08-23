package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.DTO.Meta;
import vn.hoidanit.jobhunter.DTO.ResultPaginationDTO;
import vn.hoidanit.jobhunter.model.Company;
import vn.hoidanit.jobhunter.model.User;
import vn.hoidanit.jobhunter.reponsitory.CompanyRepository;
import vn.hoidanit.jobhunter.reponsitory.UserRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company createCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO getAllCompany(Specification<Company> specification, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageSize() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());
        rs.setMeta(meta);
        List<Company> listCompany = pageCompany.getContent().stream().map(item -> this.getCompanyById(item.getId()))
                .collect(Collectors.toList());
        rs.setResult(listCompany);
        return rs;
    }

    public Company getCompanyById(long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        if (company.isPresent()) {
            return company.get();
        }
        return null;
    }

    public Company updateCompany(Company company) {
        Company currentCompany = this.getCompanyById(company.getId());
        if (currentCompany != null) {
            currentCompany.setAddress(company.getAddress());
            currentCompany.setDescription(company.getDescription());
            currentCompany.setLogo(company.getLogo());
            currentCompany.setName(company.getName());
            currentCompany = this.companyRepository.save(currentCompany);
        }
        return currentCompany;
    }

    public void deleteCompany(long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        if (company.isPresent()) {
            Company currentCompany = company.get();
            List<User> users = this.userRepository.findByCompany(currentCompany);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }
}
