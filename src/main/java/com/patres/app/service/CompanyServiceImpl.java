package com.patres.app.service;

import com.patres.app.dao.CompanyDao;
import com.patres.app.model.Company;
import com.patres.framework.component.Cacheable;
import com.patres.framework.component.Component;
import com.patres.framework.component.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Component
public class CompanyServiceImpl implements CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);

    private final CompanyDao companyDao;

    public CompanyServiceImpl(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    @Override
    @Transactional
    public void create(final Company company) {
        logger.info("START - Create company");
        companyDao.create(company);
        logger.info("END - Create company");
    }

    @Override
    @Cacheable
    public String generateCompanyName(final Long id) {
        return "Company " + id + "_" + UUID.randomUUID();
    }

}
