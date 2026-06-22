package com.admin.system.sso.service;

import com.admin.system.sso.dto.SsoProviderDTO;
import com.admin.system.sso.entity.SysSsoProvider;
import com.admin.system.sso.vo.SsoProviderPublicVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISysSsoProviderService extends IService<SysSsoProvider> {
    List<SsoProviderPublicVO> listEnabled();
    SysSsoProvider getEnabledByCode(String code);
    void saveProvider(SsoProviderDTO dto);
    void updateProvider(SsoProviderDTO dto);
}
