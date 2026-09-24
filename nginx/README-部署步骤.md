# nginx 部署步骤（虚拟机 192.168.233.130）

> 拓扑说明：
> - Windows 宿主机（192.168.233.1）：IDEA 跑后端 Spring Boot（8081）、前端 vite 开发服务器
> - 虚拟机（192.168.233.130）：nginx（80）、MySQL（3306）、Redis（6379）
>
> nginx 的三个职责：
> 1. `/api/` 反向代理到宿主机后端（前端统一走 nginx 入口）
> 2. `/avatar/` 头像静态传输（文件存在虚拟机本地目录）
> 3. `/_avatar_upload/` 接收后端 WebDAV PUT 上传的头像文件（IP 白名单仅放行宿主机）

---

## 一、虚拟机上的操作（CentOS 为例）

### 1. 安装 nginx 并确认 dav 模块

```bash
sudo yum install -y epel-release
sudo yum install -y nginx
# 确认包含 dav 模块（输出里有 with-http_dav_module 即可）
nginx -V 2>&1 | tr ' ' '\n' | grep dav
```

### 2. 创建头像静态目录

```bash
sudo mkdir -p /data/nginx/avatar
# nginx 运行用户需要有读写权限
sudo chown -R nginx:nginx /data/nginx/avatar
sudo chmod 755 /data/nginx/avatar
```

### 3. 放置配置文件

把本目录的 `nginx.conf` 内容覆盖到 `/etc/nginx/nginx.conf`（先备份）：

```bash
sudo cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.bak
sudo vim /etc/nginx/nginx.conf   # 粘贴 nginx.conf 内容
```

### 4. SELinux（如果开启）

```bash
getenforce
# 若输出 Enforcing，执行：
sudo chcon -R -t httpd_sys_rw_content_t /data/nginx/avatar   # 允许 nginx 写头像目录
sudo setsebool -P httpd_can_network_connect 1                # 允许 nginx 反向代理外连
```

### 5. 防火墙放行 80

```bash
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --reload
```

### 6. 启动并验证

```bash
sudo nginx -t                  # 语法检查
sudo systemctl enable --now nginx
systemctl status nginx
```

---

## 二、Windows 宿主机上的操作

### 1. 防火墙放行虚拟机访问后端 8081（nginx 反代需要）

管理员身份打开 PowerShell：

```powershell
netsh advfirewall firewall add rule name="NJUPTPhySim-Backend-8081" dir=in action=allow protocol=TCP localport=8081 remoteip=192.168.233.130
```

### 2. 后端配置确认（已配好，无需改动）

`application.yml` 中：

```yaml
avatar:
  nginx-base-url: http://192.168.233.130
  upload-path: /_avatar_upload/
  static-path: /avatar/
```

---

## 三、整体验证

1. **反向代理**（Windows 上执行，应返回登录失败的业务JSON而不是网络错误）：

```bash
curl -X POST http://192.168.233.130/api/common/login -H "Content-Type: application/json" -d '{"account":"ROOT","password":"ROOT"}'
```

2. **头像上传链路**：启动后端 → 前端 `npm run dev` → 登录 → 个人中心 → 点击头像上传图片
   - 成功后虚拟机 `/data/nginx/avatar/` 下出现 `{学号}_{时间戳}.jpg`
   - 数据库 `users.avatar` 更新为 `/avatar/{文件名}.jpg`
   - 页面头像正常显示（开发模式经 vite 的 `/avatar` 代理转发到虚拟机 nginx）

3. **静态直访**：浏览器直接打开 `http://192.168.233.130/avatar/{文件名}.jpg` 应能看到图片

---

## 四、生产部署（可选）

前端打包并上传到虚拟机：

```bash
cd frontend/njuptpsim
npm run build
# 把 dist/ 上传到虚拟机 /data/nginx/web/dist
```

然后取消 `nginx.conf` 中 `location /` 的注释，重载 nginx：

```bash
sudo nginx -s reload
```

之后浏览器直接访问 `http://192.168.233.130` 即完整系统（页面 + /api 反代 + 头像静态全走 nginx）。

---

## 常见问题

| 现象 | 原因 | 处理 |
|------|------|------|
| 上传报"无法连接头像存储服务" | 虚拟机 nginx 未启动 / 80 端口不通 | `systemctl status nginx`，检查防火墙 |
| 上传报 HTTP 405 | nginx 编译时没带 dav 模块 | 换用官方 nginx.org 源的包，或手动把图片放进 `/data/nginx/avatar/` |
| 上传报 HTTP 403 | `/_avatar_upload/` IP 白名单未放行宿主机 | 确认宿主机 VMnet8 IP 是 192.168.233.1（`ipconfig` 查看），必要时调整 nginx.conf 中 allow 行 |
| 上传报 HTTP 500/无法写文件 | 目录权限或 SELinux | 见步骤 2、4 |
| /api 反代 502 | Windows 防火墙拦截 / 后端未启动 | 见"二、1"；确认后端监听 0.0.0.0:8081 |
