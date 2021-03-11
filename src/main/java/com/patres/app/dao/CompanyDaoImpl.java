package com.patres.app.dao;

import com.patres.app.model.Company;
import com.patres.framework.component.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CompanyDaoImpl implements CompanyDao {

    private static final Logger logger = LoggerFactory.getLogger(CompanyDaoImpl.class);

    @Override
    public void create(final Company company) {
        logger.info("Do logic");
    }

}