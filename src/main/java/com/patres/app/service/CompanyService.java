package com.patres.app.service;

import com.patres.app.model.Company;

public interface CompanyService {

    void create(Company company);

    String generateCompanyName(Long id);
}
