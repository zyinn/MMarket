package com.sumscope.optimus.moneymarket.model.dbmodel;

/**
 * Created by shaoxu.wang on 2016/6/12.
 */
public class MmAllianceInstitution {
    private String userId;
    private String useCase;
    private String primaryInstitutionId;
    private String allianceInstitutionId;

    public String getPrimaryInstitutionId() {
        return primaryInstitutionId;
    }

    public void setPrimaryInstitutionId(String primaryInstitutionId) {
        this.primaryInstitutionId = primaryInstitutionId;
    }

    public String getAllianceInstitutionId() {
        return allianceInstitutionId;
    }

    public void setAllianceInstitutionId(String allianceInstitutionId) {
        this.allianceInstitutionId = allianceInstitutionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUseCase() {
        return useCase;
    }

    public void setUseCase(String useCase) {
        this.useCase = useCase;
    }
}
