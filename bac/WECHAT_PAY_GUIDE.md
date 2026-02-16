# 微信支付接入指南

## 📋 准备工作

### 1. 申请微信支付商户号

1. 访问 [微信支付商户平台](https://pay.weixin.qq.com/)
2. 注册并完成企业认证
3. 获取以下信息：
   - **AppID**: 公众号/小程序的AppID
   - **商户号(MchID)**: 微信支付商户号
   - **APIv3密钥**: 在商户平台设置

### 2. 下载商户证书

1. 登录微信支付商户平台
2. 进入【账户中心】->【API安全】
3. 下载商户API证书（apiclient_key.pem）
4. 获取证书序列号

### 3. 配置项目

#### 步骤1：放置证书文件

将下载的 `apiclient_key.pem` 文件放到项目中：

```
bac/src/main/resources/wechat/apiclient_key.pem
```

#### 步骤2：修改配置文件

编辑 `bac/src/main/resources/application.yml`：

```yaml
wechat:
  pay:
    app-id: wx1234567890abcdef              # 你的AppID
    mch-id: 1234567890                      # 你的商户号
    api-v3-key: your32characterapiv3key     # 你的APIv3密钥（32位）
    private-key-path: classpath:wechat/apiclient_key.pem
    merchant-serial-number: 1234567890ABCDEF1234567890ABCDEF12345678  # 证书序列号
    notify-url: https://your-domain.com/api/payment/wechat/notify    # 支付回调地址
```

⚠️ **注意**：
- `notify-url` 必须是外网可访问的HTTPS地址
- 本地开发可以使用内网穿透工具（如ngrok、natapp）

## 🚀 使用方式

### 后端API接口

#### 1. 创建JSAPI支付订单（公众号/小程序支付）

```http
POST /api/payment/wechat/jsapi/create
Content-Type: application/json

{
  "orderId": 1,
  "openid": "用户的openid"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "appId": "wx1234567890abcdef",
    "timeStamp": "1234567890",
    "nonceStr": "abc123",
    "package": "prepay_id=wx123456789",
    "signType": "RSA",
    "paySign": "签名字符串"
  }
}
```

#### 2. 创建Native支付订单（扫码支付）

```http
POST /api/payment/wechat/native/create?orderId=1
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "codeUrl": "weixin://wxpay/bizpayurl?pr=abc123",
    "orderNo": "PO1234567890"
  }
}
```

#### 3. 查询订单支付状态

```http
GET /api/payment/wechat/query/PO1234567890
```

#### 4. 关闭订单

```http
POST /api/payment/wechat/close/PO1234567890
```

### 前端集成

#### 方式1：公众号/小程序支付（JSAPI）

```javascript
import { createWeChatJsapiOrder } from '@/api/payment'

// 创建支付订单
const payWithWeChatJsapi = async (orderId, openid) => {
  try {
    const res = await createWeChatJsapiOrder({ orderId, openid })
    const payData = res.data
    
    // 调用微信支付
    if (typeof WeixinJSBridge !== 'undefined') {
      WeixinJSBridge.invoke('getBrandWCPayRequest', {
        appId: payData.appId,
        timeStamp: payData.timeStamp,
        nonceStr: payData.nonceStr,
        package: payData.package,
        signType: payData.signType,
        paySign: payData.paySign
      }, (res) => {
        if (res.err_msg === 'get_brand_wcpay_request:ok') {
          // 支付成功
          ElMessage.success('支付成功')
        } else {
          // 支付失败
          ElMessage.error('支付失败')
        }
      })
    }
  } catch (error) {
    ElMessage.error('创建支付订单失败')
  }
}
```

#### 方式2：扫码支付（Native）

```javascript
import { createWeChatNativeOrder } from '@/api/payment'
import QRCode from 'qrcode'

// 创建扫码支付订单
const payWithWeChatNative = async (orderId) => {
  try {
    const res = await createWeChatNativeOrder(orderId)
    const { codeUrl, orderNo } = res.data
    
    // 生成二维码
    const canvas = document.getElementById('qrcode')
    QRCode.toCanvas(canvas, codeUrl, (error) => {
      if (error) {
        ElMessage.error('生成二维码失败')
      } else {
        ElMessage.success('请使用微信扫码支付')
        // 轮询查询支付状态
        pollPaymentStatus(orderNo)
      }
    })
  } catch (error) {
    ElMessage.error('创建支付订单失败')
  }
}

// 轮询查询支付状态
const pollPaymentStatus = (orderNo) => {
  const timer = setInterval(async () => {
    try {
      const res = await queryPaymentStatus(orderNo)
      if (res.data.tradeState === 'SUCCESS') {
        clearInterval(timer)
        ElMessage.success('支付成功')
        // 跳转到订单详情页
        router.push(`/order/${orderNo}`)
      }
    } catch (error) {
      console.error('查询支付状态失败', error)
    }
  }, 2000) // 每2秒查询一次
  
  // 5分钟后停止轮询
  setTimeout(() => clearInterval(timer), 300000)
}
```

## 📝 前端API封装

创建 `fro/src/api/payment.js`：

```javascript
import request from '@/utils/request'

// 创建JSAPI支付订单
export const createWeChatJsapiOrder = (data) => {
  return request.post('/payment/wechat/jsapi/create', null, { params: data })
}

// 创建Native支付订单
export const createWeChatNativeOrder = (orderId) => {
  return request.post('/payment/wechat/native/create', null, { params: { orderId } })
}

// 查询支付状态
export const queryPaymentStatus = (orderNo) => {
  return request.get(`/payment/wechat/query/${orderNo}`)
}

// 关闭订单
export const closePaymentOrder = (orderNo) => {
  return request.post(`/payment/wechat/close/${orderNo}`)
}
```

## 🔔 支付回调处理

微信支付成功后会自动回调 `/api/payment/wechat/notify` 接口，系统会自动：

1. 验证签名
2. 解析支付结果
3. 更新订单状态
4. 返回处理结果给微信

## 🧪 测试流程

### 1. 本地测试（使用内网穿透）

```bash
# 使用natapp（示例）
natapp -authtoken=your_token

# 获得临时域名，如：http://abc123.natappfree.cc
# 将此域名配置到 notify-url
```

### 2. 沙箱测试

微信支付提供沙箱环境用于测试：
- 访问 [微信支付沙箱环境](https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=23_1)
- 获取沙箱密钥
- 使用沙箱商户号进行测试

### 3. 测试用例

```bash
# 1. 创建订单
curl -X POST http://localhost:8080/api/orders/create \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"treeId":1,"amount":588.00}'

# 2. 创建支付（扫码）
curl -X POST http://localhost:8080/api/payment/wechat/native/create?orderId=1

# 3. 查询支付状态
curl http://localhost:8080/api/payment/wechat/query/PO1234567890
```

## ⚠️ 注意事项

1. **证书安全**：
   - 不要将证书文件提交到Git仓库
   - 生产环境使用环境变量或密钥管理服务

2. **回调地址**：
   - 必须是HTTPS
   - 必须外网可访问
   - 需要在微信商户平台配置白名单

3. **金额单位**：
   - 微信支付金额单位是"分"
   - 系统会自动转换（元 × 100）

4. **订单号**：
   - 必须唯一
   - 建议使用时间戳+随机数

5. **超时处理**：
   - 订单超时未支付应自动关闭
   - 建议设置定时任务处理

## 🔗 相关文档

- [微信支付官方文档](https://pay.weixin.qq.com/wiki/doc/apiv3/index.shtml)
- [微信支付Java SDK](https://github.com/wechatpay-apiv3/wechatpay-java)
- [微信公众平台](https://mp.weixin.qq.com/)

## 💡 常见问题

**Q: 本地开发如何测试支付回调？**
A: 使用内网穿透工具（ngrok、natapp等）将本地服务暴露到公网。

**Q: 如何获取用户的openid？**
A: 需要通过微信网页授权或小程序登录获取。

**Q: 支付失败如何处理？**
A: 检查日志中的错误信息，常见问题包括证书配置错误、签名错误、金额格式错误等。

**Q: 如何测试退款功能？**
A: 可以参考微信支付退款API文档，添加退款接口。
