package com.cherkavi.productconfig.cd.statusholder.service;


import com.cherkavi.productconfig.cd.statusholder.domain.DeployStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "status", path = "status")
public interface DeployStatusRepository extends PagingAndSortingRepository<DeployStatus, Long>{

    DeployStatus[] findByBranch(@Param("branch") String branchName);

    @Override
    default Page<DeployStatus> findAll(Pageable pageable) {
        return findBy(pageable);
    }

    @RestResource(exported = true)
    Page<DeployStatus> findBy(Pageable pageable);
}
