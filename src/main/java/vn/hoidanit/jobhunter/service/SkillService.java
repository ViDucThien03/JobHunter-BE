package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.DTO.Meta;
import vn.hoidanit.jobhunter.DTO.ResultPaginationDTO;
import vn.hoidanit.jobhunter.model.Skill;
import vn.hoidanit.jobhunter.reponsitory.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean existsByName(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill createSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public ResultPaginationDTO getAllSkill(Specification<Skill> specification, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());
        rs.setMeta(meta);
        rs.setResult(pageSkill.getContent());
        return rs;
    }

    public Skill getSkillById(long id) {
        Optional<Skill> skill = this.skillRepository.findById(id);
        if (skill.isPresent()) {
            return skill.get();
        }
        return null;
    }

    public Skill updateSkill(Skill skill) {
        Skill currentSkill = this.getSkillById(skill.getId());
        if (currentSkill != null) {
            currentSkill.setName(skill.getName());
        }
        currentSkill = this.skillRepository.save(currentSkill);
        return currentSkill;
    }

    public void deleteSkill(long id) {
        Optional<Skill> skill = this.skillRepository.findById(id);
        Skill currentSkill = skill.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));
        currentSkill.getSubcribers().forEach(subs -> subs.getSkills().remove(currentSkill));
        this.skillRepository.delete(currentSkill);
    }
}
