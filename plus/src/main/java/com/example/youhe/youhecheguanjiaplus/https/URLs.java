package com.example.youhe.youhecheguanjiaplus.https;

/**
 * Created by Administrator on 2016/9/7 0007.
 */
public class URLs {
    public final static String OIL_APIURL = "http://apicloud.mob.com/oil/price/province/query?key=16e671c2065f0";

    public final static String APK_URL = "";//Apk下载地址

    public final static String GET_SERVERVERSION_URL = "";//服务器端版本号获取地址

//    public final static String HOST="http://qr.yeohe.com/testfeigoucar/index.php/API2";//测试接口

    public final static String HOST="http://qr.yeohe.com/carservice/index.php/API2";//测试接口
//    public final static String HOST="https://plus.yeohe.com/index.php/API2";//正式接口

//    public final static String HOST="http://qr.yeohe.com/carservice/index.php/API2";//测试接口
//    public final static String HOST="https://plus.yeohe.com/index.php/API2";//正式接口

//    public final static String HOST="http://qr.yeohe.com/carservice/index.php/API2";//测试接口
//    public final static String HOST="http://qr.yeohe.com/testplusoem/index.php/API2";//云管家  测试接口
//    public final static String HOST="https://plus.yeohe.com/index.php/API2";//正式接口

//    public final static String HOST="http://che.yeohe.com/youhe/index.php/API2";//正式接口
//    public final static String HOST="http://112.74.213.244/cwt/index.php/API2";//车违通接口
//    public final static String HOST="http://112.74.203.223/cjkc/index.php/API2";//超级快车&车行网接口
//    public final static String HOST="http://112.74.128.226/crbf/index.php/API2";//车融蝙蝠接口

    public final static String OEDER_SUPPLY_INFO=HOST+"/Order/orderSupplyInfo.html";

    public final static String QUERY = HOST+"/Peccancy/clientQueryPost.html";//违章查询
//    public final static String QUERY = "http://che.yeohe.com/youhe/index.php/API/Peccancy/testClientQueryPost.html";//违章查询

    public final static String VIOLATION_QUERY = HOST+"/Peccancy/carQueryCheck.html";//首页的违章查询

//    public final static String REGISTERLG = HOST+"API/Client/login.html";//用户登录

    public final static String REGISTERLG = HOST+"/Client/login.html";//用户登录

    public final static String ADD_CAR =HOST+ "/Car/add.html";//添加车辆

    public final static String EDIT_CAR = HOST+"/Car/editSave.html";//编辑保存车辆

    public final static String GET_CAR_LIST = HOST+"/Car/getCarList.html";//获取用户全部车辆列表

    public final static String DELETE_CAR = HOST+"/Car/del.html";//删除车辆

    public final static String COMMIT_ORDER = HOST+"/Order/add.html";//提交普通订单

    public final static String COMMIT_QUOTED_PRICE_ORDER=HOST+"/Order/addSpecialOrder.html";//提交待报价订单

    public final static String COMMIT_OWNER_ORDER=HOST+"/Order/addMyCarOrder.html";//提交本人本车订单

    public final static String UPLOAD_FILE=HOST+"/Car/editOtherCarInfo.html";//上传行驶证，驾驶证照片

    public final static String CAR_ORDER_CHECK=HOST+"/Car/carOrderCheck.html";//订单提交车辆检测

    public final static String GET_ONE_CAR_INFO=HOST+"/Car/getCarInfo.html";//获取某辆车的信息

    public final static String EDIT_ORDER_CAR_INFO=HOST+"/Car/editOrderCarInfo.html";//上传行驶证

    public final static String GET_CAR_OPEN_CITY=HOST+"/Peccancy/getMyCarOpenCitys.html";//获取本人本车开放城市

    public final static String ORDER_SUPPLY_INFO=HOST+"/Order/orderSupplyInfo.html";//订单补充资料

    public final static String COMMIT_OTHER_ORDER=HOST+"/Order/addNotQueryOrder.html";//提交其他订单

    public final static String GET_OTHER_ORDER_PROVINCE=HOST+"/Peccancy/getOtherOrderProvinces.html";//其他订单省份

    public final static String MOBILEREGISTERXINGE=HOST+"/Client/mobileRegisterXinGe";//提交信鸽Token

    public final static String GETMSG=HOST+"/Com/getHomeMsg.html";//获取首页公告通知

    public final static String GET_HOME_NOTICE=HOST+"/Com/getHomeNotice.html";//获取首页公告栏

    public final static String REGISTER_122_URL="122.gov.cn/views/register.html";//注册122官网
    public final static String DELETE_ACCOUNT_122=HOST+"/ClientCGS/delAccount.html";//删除122账号

    public final static String GET_BANNER_URL=HOST+"/Com/getHomeImgs.html";//获取首页banner图片

    public final static String ADD_IMG_URL=HOST+"/Com/addImg.html";//上传图片接口

    public final static String  ADD_BANK_CARD=HOST+"/ClientWallet/addBank.html";//添加银行卡

    public final static String ADD_WITHDRAWALS=HOST+"/ClientWallet/addWithdrawals.html";//提现申请

    public final static String GET_FEE=HOST+"/ClientWallet/get_fee.html";//获取提现手续费计算方式

    public final static String GET_WITHDRAWALSLIST=HOST+"/ClientWallet/getWithdrawalsList.html";//获取提现列表

    public final static String GET_BANK_LIST=HOST+"/ClientWallet/getBankList.html";//获取银行卡列表

    public final static String CLIENT_AUTH=HOST+"/ClientWallet/clientAuth.html";//实名认证

    public final static String SEND_BANK_VERIFY_CODE=HOST+"/ClientWallet/sendBankVerifyCode.html";//手机验证码

    public final static String CHECK_CLIENT_AUTH=HOST+"/ClientWallet/checkClientAuth.html";//用户认证信息

    public final static String GET_CLIENT_REMAINING_SUM=HOST+"/ClientWallet/getClientRemainingSum.html";//获取钱包余额

    public final static String GET_CAR_TYPE_LIST=HOST+"/Car/getCarTypeList.html";//获取车辆类型选择
//    public final static String GET_CAR_TYPE_LIST="http://api.jisuapi.com/car/brand?appkey=f4aaf6f11494bae0";//获取车辆类型选择

    public final static String GET_CAR_BRAND_LIST=HOST+"/Car/getCarBrandList.html";//获取车牌列表

    public final static String GET_CAR_LIST_BY_BRAND=HOST+"/Car/getCarListByBrand.html";//根据品牌id获取车辆列表

    public final static String GET_PROVINCE_LIST=HOST+"/ClientCGS/getProvinceList.html";//获取122支持的省份城市信息

    public final static String GET_REGISTER_CITY=HOST+"/ClientCGS/getRegisterCity.html";//获取注册省份的城市信息（包含省份注册提醒文案）

    public final static String GET_CAPTCHA_IMG=HOST+"/ClientCGS/getCaptchaImg.html";//获取122验证码

    public final static String ADD_ACCOUNTS=HOST+"/ClientCGS/addAccounts.html";//客户添加122帐号

    public final static String GET_ACCOUNT_LIST=HOST+"/ClientCGS/getAccountList.html";//获取客户122帐号列表

    public final static String GET_PAY_TYPE=HOST+"/Pay/getPayType.html";//获取用户的支付通道

    public final static String UNIONPAY_CONSUME=HOST+"/Unionpay/consume.html";//银嘉订单支付
//    public final static String UNIONPAY_CONSUME=HOST+"/NewUnionpay/consume.html";//银嘉订单支付
//    public final static String UNIONPAY_CONSUME=HOST+"/OtherUnionpay/consume.html";//银嘉订单支付

    public final static String ORDER_CHECK=HOST+"/Order/orderCheck.html";//订单检测

    public final static String GET_ACCOUNT_CAR_LIST=HOST+"/ClientCGS/getAccountCarList.html";//获取客户122帐号车辆列表

    public final static String AUTH_ACCOUNT_LOGIN=HOST+"/ClientCGS/authAccountLogin.html";//自动登录客户122帐号

    public final static String QUERY_VIOLATION_122=HOST+"/ClientCGS/queryViolation.html";//精准查询违章

    public final static String SUBMIT_REGISTER_122=HOST+"/ClientCGS/submitRegister.html";//提交122注册信息

    public final static String SEND_REGISTER_SMS_CODE=HOST+"/ClientCGS/sendRegisterSmsCode.html";//发送短信验证码

    public final static String VALID_REGISTER_SMS_CODE=HOST+"/ClientCGS/validRegisterSmsCode.html";//校验注册短信验证码

    public final static String SEND_FIND_PSW_SMSCODE=HOST+"/ClientCGS/sendFindPwdSmsCode.html";//发送找回密码短信验证码

    public final static String SUBMIT_ACCOUNT=HOST+"/ClientCGS/submitAccount.html";//提交找回密码用户信息

    public final static String UPDATA_FIND_PWD=HOST+"/ClientCGS/updateFindPwd.html";//提交找回密码的新密码

    public final static String BIN_VEH=HOST+"/ClientCGS/bindVeh.html";//绑定本人车辆

    public final static String SEND_REGISTER_SMSCODE=HOST+"/ClientCGS/sendRegisterSmsCode.html";//发送注册短信验证码

    public final static String VALID_REGISTER_SMSCODE=HOST+"/ClientCGS/validRegisterSmsCode.html";//校验注册短信验证码

    public final static String GET_QR_CODE_URL=HOST+"/MJPay/getQRcodeUrl.html";//订单支付(茂捷线上二维码通道)

    public final static String BALANCE_PAYMENT_URL=HOST+"/Pay/balance_payment.html";//余额支付

    public final static String GET_PAY_STATUS=HOST+"/MJPay/getPayStatus.html";//获取订单支付结果(茂捷线上二维码通道)

    public final static String GET_OPEN_PROVINCE=HOST+"/Peccancy/getOpenProvince.html";//获取快速查询开放省份

    public final static String  DISCLAIMER_122=HOST+"/Text/details/keyname/authorization.html";//免责声明

    public final static String QURERY_NOTICE=HOST+"/Text/details/keyname/qurerynotice.html";// 违章查询须知

    public final static String REGISTER_122_SERVICE_DISCLAIMER=HOST+"/Text/details/keyname/authorization.html";//注册服务协议

    public final static String ANNUALINSPECTIONT_DISCLAIMER=HOST+"/Text/details/keyname/AnnualInspectiont.html";//年检用户须知


    public final static String GET_SUPPORTED_PROVINCES=HOST+"/AnnualInspectiont/get_supported_provinces.html";//获取年检支持的省份城市及价格

    public final static String ADD_ANNUAL_ORDER=HOST+"/AnnualInspectiont/add_order.html";//添加年检订单

    public final static String ANNUAL_ORDER_LIST=HOST+"/AnnualInspectiont/order_list.html";//年检订单列表

    public final static String ANNUAL_ORDER_DETAIL=HOST+"/AnnualInspectiont/order_details.html";//年检订单详情

    public final static String GET_EXPRESS_INFO=HOST+"/AnnualInspectiont/get_express_info.html";//查询物流信息

    public final static String ANNUAL_ORDER_CHECK=HOST+"/AnnualInspectiont/orderCheck.html";//检测年检订单

    public final static String ADD_EXPRESSE_NUMBER=HOST+"/AnnualInspectiont/add_send_tracking_number.html";//添加年检快递单号

    public final static String ANNUAL_DEL=HOST+"/AnnualInspectiont/del.html";//删除年检订单

    public final static String ZHONGXIN_CREDIT="http://creditcard.ecitic.com/h5/shenqing/index.html?sid=SJUSOZYH";//中信信用卡申请

    public final static String GET_DAIKUANG_INFO=HOST+"/Client/getDaikuanInfo";//获取贷款初始化信息

    public final static String REGISTER_DAIKUAN=HOST+"/Client/registerDaikuan";//保存贷款注册信息

    //注册帐号地址
    public final static String REGISTER_URL = HOST+"/Client/register.html";
    //Token值刷新地址
    public final static String TOKENDEMAND = HOST+"/Client/refreshToken.html";
    //注册验证码接口
    public final static String PHONEVERIFICATIONCODE = HOST+"/Client/sendVerifyCode.html";

    //修改密码接口
    public final static String CHANGE_PASSWORD = HOST+"/Client/editPwd.html";

    //重置密码验证码接口
    public final static String RESET = HOST+"/Client/sendResetPwdVerifyCode.html";
    //重置密码接口
    public final static String VERIFICATION_REGISTER = HOST+"/Client/resetPwd.html";
    //发送手机登录验证码
    public final static String MOBILE_PHONE_ON_THE = HOST+"/Client/sendLoginVerifyCode.html";

    //手机验证登录地址
    public final static String VERIFY_THE_LOGIN = HOST+"/Client/verifyLogin.html";

    //发送手机跟换验证码
    public final static String TO_CHANGE_CELL_PHONE = HOST+"/Client/sendResetMobileVerifyCode.html";

    //手机跟换地址
    public final static String TO_CHANGE = HOST+"/Client/resetMobile.html";
    //订单查询1地址
    public final static String ORDER_QUERY = HOST+"/Order/getOrderList ";

    //订单详情删除地址
    public final static String DELETEORDE = HOST+"/Order/del";
    //订单详情
    public final static String GET_ORDER_DETAILS = HOST+"/Order/getOrderDetails.html";

    //12分特殊订单提交接口
    public final static String ADD_SPECIA_ORDER =HOST+"/Order/addSpecialOrder";

    //违章代码数据获取接口
    public final static String GET_CODE_LIST =HOST+"/PeccancyCode/getCodeList.html";

    //免责声明
    public final static String THE_STATEMENT = HOST+"/Text/details/keyname/disclaimer.html";

    //支付声明
    public final static String PAYMENT_AGREEMENT = HOST+"/Text/details/keyname/payremark.html";

    //常见问题
    public final static String COMMONPROBLEMS =HOST+ "/Text/details/keyname/usemanual";
    //服务协议
    public final static String THE_MANUAL =HOST+ "/Text/details/keyname/usemanual.html";
    //检测更新
    public final static String DETECTION_OF_UPDATE = HOST+"/Version/getNewVersion.html";

    //上传头像
    public final static String UPLOAD_THE_PICTURE =HOST+ "/Client/editHeadImg.html";

    //上传头像
//    public final static String HEAD_IMG=HOST+"/Client/headimg.html";

    //上传昵称
    public final static String UPLOAD_THE_NICKNAME = HOST+"/Client/editNickName.html";

    //反馈问题
    public final static String FEEDBACK =HOST+ "/Client/addSuggestion.html";

    //加如我们
    public final static String JOIN_US = HOST+"/Client/addApply.html";

    //获取车行易查询并下单的最低限条件
    public final static String QUERY_ORDER_RULES = "http://test.cx580.com:9000/QueryAndOrderRules.aspx";

    //汇聚支付结果通知
    public final static String HUIJUZHIFU =HOST+"/Pay/huijuPayResult.html";

    //智能posP92上网请求签到
    public final static String SIGN = HOST+"/Unionpay/sign.html";

    //拿到终端号
    public final static String GETTERMINALNUMBER =HOST+ "/Client/getPoscode.html";

    public final static String APPLY_CREDIT_CARD_URL="http://credit.uinpay.cn/yy1_20/";//申请信用卡
    public final static String PUFA_CREDIT_CARD_URL="https://ecentre.spdbccc.com.cn/creditcard/indexActivity.htm?data=P1359274";//浦发银行白金信用卡申请
    public final static String YOUTH_LOAD_URL="https://ecentre.spdbccc.com.cn/creditcard/indexActivity.htm?data=P1359274";//青春贷

    public final static String HIGHTWAY_STATUS="http://mainten.televehicle.com/highwaystatus/getRoadListForRegion.action?regionId=0&from=singlemessage&isappinstalled=0";//高速路况

    //支付交易
//    public final static String  CHECKPAYMENT = HOST+"/Order/orderCheck.html";

    //批量删除订单号
    public final static String  BATCHREMOVE = HOST+"/Order/dels.html";

    //智能posP92主密钥
    public final static String MASTERKEY ="http://qr.yeohe.com/testyouhe/index.php/"+"API/Unionpay/getPayKey.html";

    //鲲荣提交支付数据
    public final static String HTTPSPAY = HOST+"/Unionpay/consume.html";

    //plus 发送短信登陆验证码
    public final static String PLUS_LOGIN_VERIFYMESSAGE = HOST+"/Client/verifyMessage.html";
    //PluS 注册帐号地址
    public final static String PLUS_REGISTER_URL = HOST+"/Regist/regist.html";
    //PLUS 获取注册账号类型
    public final static String PLUS_REGISTER_TYPE = HOST+"/Regist/get_user_type.html";
    //PLUS 获取注册验证码
    public final static String PLUS_REGISTER_SMS = HOST+"/Regist/sms_verification.html";
    //PLUS 获取添加车主卡规则
    public final static String PLUS_ADD_RULE = HOST+"/CardNumber/get_card_number_rule.html";
    //PLUS 添加车主卡
    public final static String PLUS_ADD = HOST+"/CardNumber/add_card_number.html";
    //PLUS 车主卡列表
    public final static String PLUS_LIST = HOST+"/CardNumber/card_number_list.html";
    //PLUS 车主卡订单金额检测
    public final static String PLUS_ORDER_CHECK = HOST+"/CardNumber/card_number_order_check.html";
    //PLUS 商户注册h5
    public final static String PLUS_CLIENTMERCHANT = HOST+"/ClientMerchant/register/token/";  //后面加token值
    //交易记录分类列表
    public final static String PLUS_WAY_LIST = HOST+"/Pay/pay_way_list.html";
    //交易记录列表
    public final static String PLUS_PLY_LOG = HOST+"/Pay/pay_log.html";
    //plus 激活
    public final static String PLUS_USER_ACTIVATION = HOST+"/Regist/user_activation.html";
    //plus 刷新用户状态
    public final static String PLUS_REFRESH_USER_STATUS = HOST+"/Regist/refresh_user_status.html";
    //plus 获取邀请码
    public final static String PAY_INVICODE = HOST+"/Client/inviCode.html";
    //plus 分销记录
    public final static String PAY_RECORD= HOST+"/CardNumber/card_number_rebate_record.html?token=";


    //获取App支付结果(目前只支持支付宝)
    public final static String APPPAY_STATUS= HOST+"/AppPay/get_app_pay_status.html";
    //App支付(目前只支持支付宝)
    public final static String APPPAY_INFO= HOST+"/AppPay/get_app_pay_info.html";


    public final static String HTTP = "http://";
    public final static String HTTPS = "https://";
    private final static String URL_SPLITTER = "/";
    private final static String URL_UNDERLINE = "_";
}
