package com.patres.app;

import com.patres.app.model.Company;
import com.patres.app.service.CompanyService;
import com.patres.framework.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        final CompanyService companyService = ApplicationContext.getBeanDynamicProxy(CompanyService.class);
        final Company company = new Company();
        companyService.create(company);
//        logger.info(companyService.generateCompanyName(1L));
//        logger.info(companyService.generateCompanyName(1L));
//        logger.info(companyService.generateCompanyName(2L));
    }

}
