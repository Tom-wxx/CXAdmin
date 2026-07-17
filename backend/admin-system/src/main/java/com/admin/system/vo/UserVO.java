package com.admin.system.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户VO
 *
 * @author Admin
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long deptId;

    private String deptName;

    private String username;

    private String nickname;

    private String email;

    private String phone;

    /**
     * 性别（0男 1女 2未知）
     */
    private String gender;

    private String avatar;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    private String loginIp;

    private Date loginDate;

    private Date createTime;

    private String remark;

    private List<String> roleNames;

    private List<String> postNames;
}
