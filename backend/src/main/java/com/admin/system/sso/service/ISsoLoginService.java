package com.admin.system.sso.service;

import com.admin.system.sso.vo.SsoBindingVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ISsoLoginService {
    /** 未登录态发起登录授权 URL（mode=login）。 */
    String buildAuthorizationUrl(String code);

    /** 已登录态发起绑定授权 URL（mode=bind，记当前 userId）。 */
    String buildBindAuthorizationUrl(String code, Long userId);

    /** 处理 IdP 回调。state 中带 mode 字段：login → 登录注册；bind → 仅追加 binding。 */
    String handleCallback(String code, String authCode, String state, HttpServletResponse response);

    /** 当前用户已绑定的 IdP 列表（带 provider 元信息）。 */
    List<SsoBindingVO> listMyBindings(Long userId);

    /** 解绑：仅本人能解绑自己的绑定。 */
    void unbind(Long bindingId, Long userId);
}
