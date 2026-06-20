# ADR 0001：令牌作为「不透明 Redis 会话键」使用

- 状态：已接受（记录现状，本轮不改造）
- 日期：2026-06-19

## 背景

系统在登录时用 `jjwt` 签发一枚 JWT（`JwtUtil.generateToken`），随后把 `LoginUser`
以该 token 为键存入 Redis。但 `JwtAuthenticationFilter` 在**每次请求**时的处理是：

1. 从 Cookie（回退 `Authorization` 头）取出 token 串；
2. 直接用 token 串作为 Redis key 查出 `LoginUser`；
3. **不调用 `JwtUtil` 验签，也不解析 claims / exp**。

令牌有效期完全以 Redis 中的 `expireTime` 为准（剩余 ≤20 分钟自动滑动续期）。

## 结论：这是「有状态会话」，不是无状态 JWT

当前 JWT 的密码学签名在运行期**不提供任何安全价值**——伪造 token 无法命中 Redis，
安全性完全来自「Redis 中是否存在该键」。等价于用一个随机不透明会话 ID。

代价：仍背负 JWT 的全部复杂度（HS512 要求 ≥64 字节密钥、`WeakKeyException` 风险、
`jjwt-api/impl/jackson` 三件套依赖）。

## 决策

本轮**不改造**认证主链路（避免登录回归），仅以代码注释 + 本 ADR 明确现状，消除"这是无状态
JWT"的误解。`JwtUtil` 与 `JwtAuthenticationFilter` 已加说明性注释指向本文档。

## 未来可选演进（二选一，勿骑墙）

1. **诚实化为不透明令牌**：登录改发 `UUID` + Redis 会话，删除 `jjwt` 依赖与密钥长度约束。
   语义最清晰，改动集中在 `LoginServiceImpl` / `JwtUtil` 调用点。
2. **改造为真·无状态 JWT**：请求期验签、权限入 claims、Redis 仅做注销黑名单。
   收益是减轻 Redis 依赖，代价是登录/注销/续期语义需整体重写。

在没有明确的横向扩展 / 降 Redis 依赖诉求前，维持现状（有状态会话）是成本最低的选择。
