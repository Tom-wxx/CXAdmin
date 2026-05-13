package com.admin.system.sso.service.impl;

import com.admin.system.sso.dto.SsoProviderDTO;
import com.admin.system.sso.entity.SysSsoProvider;
import com.admin.system.sso.mapper.SysSsoProviderMapper;
import com.admin.system.sso.service.ISysSsoProviderService;
import com.admin.system.sso.util.SsoCryptoUtil;
import com.admin.system.sso.vo.SsoProviderPublicVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysSsoProviderServiceImpl
        extends ServiceImpl<SysSsoProviderMapper, SysSsoProvider>
        implements ISysSsoProviderService {

    private final SsoCryptoUtil crypto;

    @Override
    public List<SsoProviderPublicVO> listEnabled() {
        return lambdaQuery()
                .eq(SysSsoProvider::getEnabled, 1)
                .orderByAsc(SysSsoProvider::getOrderNum)
                .list()
                .stream().map(p -> {
                    SsoProviderPublicVO v = new SsoProviderPublicVO();
                    v.setCode(p.getCode());
                    v.setName(p.getName());
                    v.setIcon(p.getIcon());
                    v.setOrderNum(p.getOrderNum());
                    return v;
                })
                .collect(Collectors.toList());
    }

    @Override
    public SysSsoProvider getEnabledByCode(String code) {
        return getOne(new LambdaQueryWrapper<SysSsoProvider>()
                .eq(SysSsoProvider::getCode, code)
                .eq(SysSsoProvider::getEnabled, 1));
    }

    @Override
    public void saveProvider(SsoProviderDTO dto) {
        SysSsoProvider entity = new SysSsoProvider();
        BeanUtils.copyProperties(dto, entity);
        entity.setClientSecret(crypto.encrypt(dto.getClientSecret()));
        save(entity);
    }

    @Override
    public void updateProvider(SsoProviderDTO dto) {
        SysSsoProvider entity = new SysSsoProvider();
        BeanUtils.copyProperties(dto, entity);
        if (dto.getClientSecret() == null || dto.getClientSecret().isEmpty()) {
            entity.setClientSecret(null);  // 空 = 不变更
        } else {
            entity.setClientSecret(crypto.encrypt(dto.getClientSecret()));
        }
        updateById(entity);
    }
}
