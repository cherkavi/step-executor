package com.cherkavi.productconfig.cd.monitor.repository;


import com.cherkavi.productconfig.cd.monitor.domain.DeployStatus;
import org.springframework.data.repository.CrudRepository;


public interface DeployStatusRepository extends CrudRepository<DeployStatus, Long> {
}
