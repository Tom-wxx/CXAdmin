package com.admin.system.service;

import com.admin.system.dto.RegisterDTO;
import com.admin.system.dto.ResetPasswordDTO;

public interface IRegisterService {
    void register(RegisterDTO dto);
    void forgotPassword(String email);
    void resetPassword(ResetPasswordDTO dto);
}
