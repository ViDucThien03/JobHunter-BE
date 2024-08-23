package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.DTO.ResultPaginationDTO;
import vn.hoidanit.jobhunter.error.IdInvalidException;
import vn.hoidanit.jobhunter.model.Skill;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * SkillController
 */
@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/skills")
    @ApiMessage("Get all skill success")
    public ResponseEntity<ResultPaginationDTO> getMethodName(@Filter Specification<Skill> specification,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.skillService.getAllSkill(specification, pageable));
    }

    @PostMapping("/skills")
    @ApiMessage("Create skill success")
    public ResponseEntity<Skill> createSkill(@RequestBody Skill skill) throws IdInvalidException {
        boolean isExitsName = this.skillService.existsByName(skill.getName());
        if (isExitsName) {
            throw new IdInvalidException("Name " + skill.getName() + " already exits!");
        }
        Skill newSkill = this.skillService.createSkill(skill);
        return ResponseEntity.ok().body(newSkill);
    }

    @GetMapping("/skills/{id}")
    public ResponseEntity<Skill> getSkillById(@PathVariable("id") long id) throws IdInvalidException {
        Skill currentSkill = this.skillService.getSkillById(id);
        if (currentSkill == null) {
            throw new IdInvalidException("Not found");
        }
        Skill skill = this.skillService.getSkillById(id);
        return ResponseEntity.status(HttpStatus.OK).body(skill);
    }

    @PutMapping("skills/")
    public ResponseEntity<Skill> updateSkill(@RequestBody Skill skill) throws IdInvalidException {
        Skill currentSkill = this.skillService.updateSkill(skill);
        if (currentSkill == null) {
            throw new IdInvalidException("User does not exist");
        }
        if (skill.getName() != null && this.skillService.existsByName(skill.getName())) {
            throw new IdInvalidException("Skill name " + skill.getName() + " is exits");
        }
        return ResponseEntity.ok().body(this.skillService.updateSkill(currentSkill));
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Skill> deleteSkill(@PathVariable("id") long id) throws IdInvalidException {
        Skill currentSkill = this.skillService.getSkillById(id);
        if (currentSkill == null) {
            throw new IdInvalidException("Not found");
        }
        this.skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}