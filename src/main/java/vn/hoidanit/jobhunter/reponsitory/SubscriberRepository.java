package vn.hoidanit.jobhunter.reponsitory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.hoidanit.jobhunter.model.Subcriber;

@Repository
public interface SubscriberRepository extends JpaRepository<Subcriber, Long>, JpaSpecificationExecutor<Subcriber> {
    boolean existsByEmail(String email);

    Subcriber findByEmail(String email);
}
