package com.admin.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginPetTypeDTO {

    @NotBlank(message = "登录页宠物类型不能为空")
    @Pattern(regexp = "cat|dog|owl", message = "登录页宠物类型仅支持 cat、dog、owl")
    private String type;
}
