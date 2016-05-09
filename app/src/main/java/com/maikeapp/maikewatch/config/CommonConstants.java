package com.maikeapp.maikewatch.config;

public class CommonConstants {
	public static final String LOGCAT_APP_NAME = "maike+"  ;
	public static final String LOGCAT_TAG_NAME = "jlj"  ;
	public static final String APP_PACKAGE_NAME = "com.mkch.maikejia" ;
	
	public static final int FLAG_GET_BANNER_LIST_SUCCESS = 1;
	public static final int FLAG_GET_HOME_HOT_CREATIVE_LIST_SUCCESS = 2;
	public static final int FLAG_GET_HOME_HOT_DESIGN_LIST_SUCCESS = 3;
	public static final int FLAG_GET_FIND_CREATIVE_LIST_SUCCESS = 4;
	public static final int FLAG_GET_FIND_CREATIVE_LIST_AGAIN_SUCCESS = 5;
	public static final int FLAG_GET_CREATIVE_DETAIL_SUCCESS = 6;
	public static final int FLAG_GET_CREATIVE_DETAIL_COMMENT_LIST_SUCCESS = 7;
	public static final int FLAG_GET_REG_MOBILEMGS_REGISTER_SUCCESS = 8;
	public static final int FLAG_GET_REG_MOBILEMGS_VALIDATE_SUCCESS = 9;
	public static final int FLAG_GET_REG_MOBILEMGS_VALIDATE_CAN_GET_AGAIN_SUCCESS = 10;
	public static final int FLAG_GET_REG_USER_REGISTER_SUCCESS = 11;
	public static final int FLAG_GET_REG_USER_LOGIN_SUCCESS = 12;
	
	public static final int FLAG_GET_USER_FORGOT_PWD_ONE_SUCCESS = 13;
	public static final int FLAG_GET_USER_FORGOT_SMS_CAN_GET_AGAIN_SUCCESS = 14;
	public static final int FLAG_GET_USER_FORGOT_PWD_UPDATE_SUCCESS = 15;
	public static final int FLAG_GET_PSCENTER_LOGINED_USER_INFO_SUCCESS = 16;
	public static final int FLAG_GET_FIND_CREATIVE_LIST_NONE_SUCCESS = 17;
	public static final int FLAG_GET_MEMBER_ATTENTION_SAVE_SUCCESS = 18;
	public static final int FLAG_GET_MEMBER_ATTENTION_DELETE_SUCCESS = 19;
	public static final int FLAG_GET_MEMBER_CONTENT_COLLECT_SUCCESS = 20;
	public static final int FLAG_GET_MEMBER_CONTENT_UP_SUCCESS = 21;
	public static final int FLAG_GET_MEMBER_CONTENT_DOWN_SUCCESS = 22;
	public static final int FLAG_GET_MEMBER_COMMENT_ADD_SUCCESS = 23;
	public static final int FLAG_GET_MEMBER_COMMENT_REPLY_ADD_SUCCESS = 24;
	public static final int FLAG_GET_SEARCH_CREATIVE_LIST_SUCCESS = 25;
	public static final int FLAG_GET_SEARCH_CREATIVE_LIST_AGAIN_SUCCESS = 26;
	public static final int FLAG_GET_MKF_NEW_LIST_SUCCESS = 27;
	public static final int FLAG_GET_MKF_NEW_LIST_AGAIN_SUCCESS = 28;
	public static final int FLAG_GET_MKF_DETAIL_SUCCESS = 29;
	
	public static final int FLAG_GET_HOME_HOT_CREATIVE_NONE_SUCCESS = 30;
	public static final int FLAG_GET_HOME_HOT_DESIGN_NONE_SUCCESS = 31;
	public static final int FLAG_GET_USER_CREATIVE_LIST_SUCCESS = 32;
	public static final int FLAG_GET_USER_CREATIVE_LIST_AGAIN_SUCCESS = 33;
	public static final int FLAG_CANCEL_USER_ATTENTION_CREATIVE_SUCCESS = 34;
	public static final int FLAG_GET_USER_ATTENTION_USER_LIST_SUCCESS = 35;
	public static final int FLAG_GET_USER_ATTENTION_USER_LIST_AGAIN_SUCCESS = 36;
	public static final int FLAG_CANCEL_USER_ATTENTION_USER_SUCCESS = 37;
	public static final int FLAG_GET_USER_FANS_LIST_SUCCESS = 38;
	public static final int FLAG_GET_USER_FANS_LIST_AGAIN_SUCCESS = 39;
	public static final int FLAG_CANCEL_USER_FANS_SUCCESS = 40;
	public static final int FLAG_GET_USER_COMMENTS_LIST_SUCCESS = 41;
	public static final int FLAG_GET_USER_COMMENTS_LIST_AGAIN_SUCCESS = 42;
	public static final int FLAG_CANCEL_USER_COMMENTS_SUCCESS = 43;
	public static final int FLAG_GET_LIST_NO_DATA = 44;
	public static final int FLAG_GET_USER_SELF_COMMENTS_LIST_SUCCESS = 45;
	public static final int FLAG_GET_USER_SELF_COMMENTS_LIST_AGAIN_SUCCESS = 46;
	public static final int FLAG_CANCEL_USER_SELF_COMMENTS_SUCCESS = 47;
	public static final int FLAG_GET_USER_MESSAGES_LIST_SYS_PUB_SUCCESS = 48;
	public static final int FLAG_GET_USER_MESSAGES_LIST_SYS_PUB_AGAIN_SUCCESS = 49;
	public static final int FLAG_CANCEL_USER_MESSAGES_SYS_PUB_SUCCESS = 50;
	public static final int FLAG_GET_USER_MESSAGES_LIST_SOCIAL_SUCCESS = 51;
	public static final int FLAG_GET_USER_MESSAGES_LIST_SOCIAL_AGAIN_SUCCESS = 52;
	public static final int FLAG_CANCEL_USER_MESSAGES_SOCIAL_SUCCESS = 53;
	public static final int FLAG_GET_USER_NOTICE_DETAIL_SUCCESS = 54;
	public static final int FLAG_UPDATE_USER_PWD_SUCCESS = 55;
	public static final int FLAG_GET_USER_SETTING_SECURITY_CODE_SUCCESS = 56;
	public static final int FLAG_GET_USER_SETTING_SECURITY_CODE_CHECK_SUCCESS = 57;
	public static final int FLAG_GET_USER_SETTING_SECURITY_CODE_SEND_AGAIN_SUCCESS = 58;
	public static final int FLAG_GET_WAP_LOGINED = 59;
	public static final int FLAG_GET_WAP_LOGINED_INIT = 60;
	public static final int FLAG_GET_USER_SETTING_SECURITY_EMAIL_CODE_SUCCESS = 61;
	public static final int FLAG_UPDATE_USER_INFO_SUCCESS = 62;
	public static final int FLAG_CHECK_APP_UPDATE_SUCCESS = 63;
	
	public static final String MSG_GET_ERROR = "获取出错";
	public static final String MSG_GET_SUCCESS = "获取成功";
	public static final String MSG_SERVER_ERROR = "请求服务器错误";
	public static final String MSG_REQUEST_TIMEOUT = "请求服务器超时";
	public static final String MSG_SERVER_RESPONSE_TIMEOUT = "服务器响应超时";
	/**
	 * 测试地址
	 */
	public static final String TEST_ADDRESS = "http://192.168.0.251:8080/external/";
	public static final String TRUE_ADDRESS = "http://www.maikejia.com/external/";
	public static final String NOW_ADDRESS = TRUE_ADDRESS;
	
	public static final String TEST_ADDRESS_WAP = "http://192.168.0.251:8080/";
	public static final String TRUE_ADDRESS_WAP = "http://www.maikejia.com/";
	public static final String NOW_ADDRESS_WAP = TRUE_ADDRESS_WAP;
	
	public static final String TEST_CYDS_ADDRESS_WAP = "http://192.168.0.252:8080/";
	public static final String TRUE_CYDS_ADDRESS_WAP = "http://m.dasai.maikejia.com/";
	public static final String NOW_CYDS_ADDRESS_WAP = TRUE_CYDS_ADDRESS_WAP;
	/**
	 * 注册登录
	 */
	//发送注册短信
	public static final String MOBILEMSG_REGISTER = NOW_ADDRESS + "mobilemsg/register.json";
	//验证注册短信
	public static final String MOBILEMSG_VALIDATE = NOW_ADDRESS + "mobilemsg/validate.json";
	//注册
	public static final String USER_REGISTER = NOW_ADDRESS + "user/register.json";
	//登录
	public static final String USER_LOGIN = NOW_ADDRESS + "user/login.json";
	//忘记密码第一步
	public static final String USER_FORGOT_PASSWORD_ONE = NOW_ADDRESS + "user/forgot_password_one.json";
	//忘记密码第二步
	public static final String USER_FORGOT_PASSWORD_TWO = NOW_ADDRESS + "user/forgot_password_two.json";
	
	/**
	 * 前台
	 */
	//获取banner
	public static final String BANNER_LIST = NOW_ADDRESS + "banner/list.json";
	//-获取各个阶段的创意-
	//获取各个阶段的创意
	public static final String CONTENT_LIST = NOW_ADDRESS + "content/list.json";
	//获取各个阶段的创意（带分页）
	public static final String CONTENT_PAGE = NOW_ADDRESS + "content/page.json";
	//创意详情
	public static final String CONTENT_DETAIL = NOW_ADDRESS + "content/detail.json";
	//创意分类 channel/list.json
	public static final String CHANNEL_LIST = NOW_ADDRESS + "channel/list.json";
	//-交互-
	//关注 member/content/collect.json
	public static final String MEMBER_CONTENT_COLLECT = NOW_ADDRESS + "member/content/collect.json";
	//赞 member/content/up.json
	public static final String MEMBER_CONTENT_UP = NOW_ADDRESS + "member/content/up.json";
	//踩 member/content/down.json
	public static final String MEMBER_CONTENT_DOWN = NOW_ADDRESS + "member/content/down.json";
	//-评论-
	//评论列表 comment/list.json
	public static final String COMMENT_LIST = NOW_ADDRESS + "comment/list.json";
	//评论上传图片 member/comment/uploadImg.json
	public static final String MEMBER_COMMENT_UPLOADIMG = NOW_ADDRESS + "member/comment/uploadImg.json";
	//发表评论 member/comment/add.json
	public static final String MEMBER_COMMENT_ADD = NOW_ADDRESS + "member/comment/add.json";
	//删除评论 member/comment/delete.json
	public static final String MEMBER_COMMENT_DELETE = NOW_ADDRESS + "member/comment/delete.json";
	//评论举报 member/comment/report.json
	public static final String MEMBER_COMMENT_REPORT = NOW_ADDRESS + "member/comment/report.json";
	//评论点赞 member/comment/up.json
	public static final String MEMBER_COMMENT_UP = NOW_ADDRESS + "member/comment/up.json";
	//-评论回复-
	//回复列表 comment/reply/list.json
	public static final String COMMENT_REPLY_LIST = NOW_ADDRESS + "comment/reply/list.json";
	//评论回复 member/comment/reply/add.json
	public static final String MEMBER_COMMENT_REPLY_ADD = NOW_ADDRESS + "member/comment/reply/add.json";
	//删除回复 member/comment/reply/delete.json
	public static final String MEMBER_COMMENT_REPLY_DELETE = NOW_ADDRESS + "member/comment/reply/delete.json";
	//搜索 search.json
	public static final String SEARCH = NOW_ADDRESS + "search.json";
	//搜索列表（不分页） search/page.json
	public static final String SEARCH_PAGE = NOW_ADDRESS + "search/page.json";
	//-活动-
	//活动列表 activity/list.json
	public static final String ACTIVITY_LIST = NOW_ADDRESS + "activity/list.json";
	//活动详情 activity/detail.json
	public static final String ACTIVITY_DETAIL = NOW_ADDRESS + "activity/detail.json";
	//-调研活动-
	//调研结果列表 research/list.json
	public static final String RESEARCH_LIST = NOW_ADDRESS + "research/list.json";
	//调研投票 member/research/vote.json
	public static final String MEMBER_RESEARCH_VOTE = NOW_ADDRESS + "member/research/vote.json";
	//-设计活动-
	//设计活动结果列表 adesign/list.json
	public static final String ADESIGN_LIST = NOW_ADDRESS + "adesign/list.json";
	//设计活动回答 member/adesign/answer.json
	public static final String MEMBER_ADESIGN_ANSWER = NOW_ADDRESS + "member/adesign/answer.json";
	//设计活动回答结果修改 member/adesign/update.json
	public static final String MEMBER_ADESIGN_UPDATE = NOW_ADDRESS + "member/adesign/update.json";
	//设计活动投票 member/adesign/vote.json
	public static final String MEMBER_ADESIGN_VOTE = NOW_ADDRESS + "member/adesign/vote.json";
	//设计活动结果选定 member/adesign/select.json
	public static final String MEMBER_ADESIGN_SELECT = NOW_ADDRESS + "member/adesign/select.json";
	//设计活动结果删除 member/adesign/delete.json
	public static final String MEMBER_ADESIGN_DELETE = NOW_ADDRESS + "member/adesign/delete.json";
	//-优化活动-
	//优化活动结果列表 optimize/list.json
	public static final String OPTIMIZE_LIST = NOW_ADDRESS + "optimize/list.json";
	//优化活动回答 member/optimize/answer.json
	public static final String MEMBER_OPTIMIZE_ANSWER = NOW_ADDRESS + "member/optimize/answer.json";
	//优化活动回答结果修改 member/optimize/update.json
	public static final String MEMBER_OPTIMIZE_UPDATE = NOW_ADDRESS + "member/optimize/update.json";
	//优化活动投票 member/optimize/vote.json
	public static final String MEMBER_OPTIMIZE_VOTE = NOW_ADDRESS + "member/optimize/vote.json";
	//优化活动结果选定 member/optimize/select.json
	public static final String MEMBER_OPTIMIZE_SELECT = NOW_ADDRESS + "member/optimize/select.json";
	//优化活动结果删除 member/optimize/delete.json
	public static final String MEMBER_OPTIMIZE_DELETE = NOW_ADDRESS + "member/optimize/delete.json";
	//-样式活动-
	//样式活动结果列表 style/list.json
	public static final String STYLE_LIST = NOW_ADDRESS + "style/list.json";
	//样式活动回答 member/style/answer.json
	public static final String MEMBER_STYLE_ANSWER = NOW_ADDRESS + "member/style/answer.json";
	//样式活动回答结果修改 member/style/update.json
	public static final String MEMBER_STYLE_UPDATE = NOW_ADDRESS + "member/style/update.json";
	//样式活动投票 member/style/vote.json
	public static final String MEMBER_STYLE_VOTE = NOW_ADDRESS + "member/style/vote.json";
	//样式活动结果选定 member/style/select.json
	public static final String MEMBER_STYLE_SELECT = NOW_ADDRESS + "member/style/select.json";
	//样式活动结果删除 member/style/delete.json
	public static final String MEMBER_STYLE_DELETE = NOW_ADDRESS + "member/style/delete.json";
	//-命名活动-
	//命名活动结果列表 name/list.json
	public static final String NAME_LIST = NOW_ADDRESS + "name/list.json";
	//命名活动回答 member/name/answer.json
	public static final String MEMBER_NAME_ANSWER = NOW_ADDRESS + "member/name/answer.json";
	//命名活动回答结果修改 member/name/update.json
	public static final String MEMBER_NAME_UPDATE = NOW_ADDRESS + "member/name/update";
	//命名活动投票 member/name/vote.json
	public static final String MEMBER_NAME_VOTE = NOW_ADDRESS + "member/name/vote.json";
	//命名活动结果选定 member/name/select.json
	public static final String MEMBER_NAME_SELECT = NOW_ADDRESS + "member/name/select.json";
	//命名活动结果删除 member/name/delete.json
	public static final String MEMBER_NAME_DELETE = NOW_ADDRESS + "member/name/delete.json";
	//-标语活动-
	//标语活动结果列表 slogan/list.json
	public static final String SLOGAN_LIST = NOW_ADDRESS + "slogan/list.json";
	//标语活动回答 member/slogan/answer.json
	public static final String MEMBER_SLOGAN_ANSWER = NOW_ADDRESS + "member/slogan/answer.json";
	//标语活动回答结果修改 member/slogan/update.json
	public static final String MEMBER_SLOGAN_UPDATE = NOW_ADDRESS + "member/slogan/update.json";
	//标语活动投票 member/slogan/vote.json
	public static final String MEMBER_SLOGAN_VOTE = NOW_ADDRESS + "member/slogan/vote.json";
	//标语活动结果选定 member/slogan/select.json
	public static final String MEMBER_SLOGAN_SELECT = NOW_ADDRESS + "member/slogan/select.json";
	//标语活动结果删除 member/slogan/delete.json
	public static final String MEMBER_SLOGAN_DELETE = NOW_ADDRESS + "member/slogan/delete.json";
	//-价格活动-
	//价格活动结果列表 price/list.json
	public static final String PRICE_LIST = NOW_ADDRESS + "price/list.json";
	//价格活动回答 member/price/answer.json
	public static final String MEMBER_PRICE_ANSWER = NOW_ADDRESS + "member/price/answer.json";
	//价格活动回答结果修改 member/price/update.json
	public static final String MEMBER_PRICE_UPDATE = NOW_ADDRESS + "member/price/update.json";
	//价格活动投票 member/price/vote.json
	public static final String MEMBER_PRICE_VOTE = NOW_ADDRESS + "member/price/vote.json";
	//标语活动结果选定 member/price/select.json
	public static final String MEMBER_PRICE_SELECT = NOW_ADDRESS + "member/price/select.json";
	//价格活动结果删除 member/price/delete.json
	public static final String MEMBER_PRICE_DELETE = NOW_ADDRESS + "member/price/delete.json";
	/**
	 * 用户中心
	 */
	//我的创意 创意列表 member/contribute/page.json
	public static final String MEMBER_CONTRIBUTE_PAGE = NOW_ADDRESS + "member/contribute/page.json";
	//-我的征集活动-
	//我发起的活动 member/activity/page.json
	public static final String MEMBER_ACTIVITY_PAGE = NOW_ADDRESS + "member/activity/page.json";
	//我参与的活动 member/activity/join.json
	public static final String MEMBER_ACTIVITY_JOIN = NOW_ADDRESS + "member/activity/join.json";
	//-我关注的创意-
	//我关注的创意 member/collection/page.json
	public static final String MEMBER_COLLECTION_PAGE = NOW_ADDRESS + "member/collection/page.json";
	//删除关注 member/collection/delete.json
	public static final String MEMBER_COLLECTION_DELETE = NOW_ADDRESS + "member/collection/delete.json";
	//-我的粉丝-
	//获取粉丝 member/fans/page.json
	public static final String MEMBER_FANS_PAGE = NOW_ADDRESS + "member/fans/page.json";
	//关注粉丝 member/fans/attention.json
	public static final String MEMBER_FANS_ATTENTION = NOW_ADDRESS + "member/fans/attention.json";
	//移除粉丝 member/fans/delete.json
	public static final String MEMBER_FANS_DELETE = NOW_ADDRESS + "member/fans/delete.json";
	//-我的评论功能-
	//获取评论 member/comments/page.json
	public static final String MEMBER_COMMENTS_PAGE = NOW_ADDRESS + "member/comments/page.json";
	//获取评论 member/comments/page.json
	public static final String MEMBER_COMMENTS_REPAGE = NOW_ADDRESS + "member/comments/repage.json";
	//-我的代理-
	//获取申请的代理 member/agent/page.json
	public static final String MEMBER_AGENT_PAGE = NOW_ADDRESS + "member/agent/page.json";
	//-我的融资-
	//融资显示 member/rong/page.json
	public static final String MEMBER_RONG_PAGE = NOW_ADDRESS + "member/rong/page.json";
	//详情显示 member/rong/detail.json
	public static final String MEMBER_RONG_DETAIL = NOW_ADDRESS + "member/rong/detail.json";
	//-我的通知-
	//信息显示 消息列表 member/message/page.json
	public static final String MEMBER_MESSAGE_PAGE = NOW_ADDRESS + "member/message/page.json";
	//删除消息 member/message/delete.json
	public static final String MEMBER_MESSAGE_DELETE = NOW_ADDRESS + "member/message/delete.json";
	//标记已读 member/message/readed.json
	public static final String MEMBER_MESSAGE_READED = NOW_ADDRESS + "member/message/readed.json";
	//通知详情 member/message/read.json
	public static final String MEMBER_MESSAGE_READ = NOW_ADDRESS + "member/message/read.json";
	//-我的账户设置-
	//个人资料显示 member/profile.json
	public static final String MEMBER_PROFILE = NOW_ADDRESS + "member/profile.json";
	//个人资料修改确认 member/profile/update.json
	public static final String MEMBER_PROFILE_UPDATE = NOW_ADDRESS + "member/profile/update.json";
	//修改头像显示 member/portiart.json
	public static final String MEMBER_PORTIART = NOW_ADDRESS + "member/portiart.json";
	//个人资料修改头像 member/portiart/update.json
	public static final String MEMBER_PORTIART_UPDATE = NOW_ADDRESS + "member/portiart/update.json";
	//其他设置 member/otherConfig.json
	public static final String MEMBER_OTHERCONFIG = NOW_ADDRESS + "member/otherConfig.json";
	//其他设置提交 member/otherConfig/update.json
	public static final String MEMBER_OTHERCONFIG_UPDATE = NOW_ADDRESS + "member/otherConfig/update.json";
	//安全中心显示 member/security.json
	public static final String MEMBER_SECURITY = NOW_ADDRESS + "member/security.json";
	//安全中心提交 member/security/mobile.json
	public static final String MEMBER_SECURITY_MOBILE = NOW_ADDRESS + "member/security/mobile.json";
	//安全中心修改密码member/pwd.json
	public static final String MEMBER_PWD = NOW_ADDRESS + "member/pwd.json";
	//安全中心获取验证码//输入需要绑定的手机号码获取验证码member/security/mobile/captcha.json
	public static final String MEMBER_SECURITY_MOBILE_CAPTCHA = NOW_ADDRESS + "member/security/mobile/captcha.json";
	//安全中心修改绑定手机//获取的验证码与需要绑定的手机匹配成功则绑定手机成功member/security/mobile/bind.json
	public static final String MEMBER_SECURITY_MOBILE_BIND = NOW_ADDRESS + "member/security/mobile/bind.json";
	//安全中心获取验证码//输入需要绑定的邮箱号码获取验证码member/security/email/captcha.json
	public static final String MEMBER_SECURITY_EMAIL_CAPTCHA = NOW_ADDRESS + "member/security/email/captcha.json";
	//安全中心修改绑定邮箱//获取的验证码与需要绑定的邮箱匹配成功则绑定手机成功。member/security/email/bind.json
	public static final String MEMBER_SECURITY_EMAIL_BIND = NOW_ADDRESS + "member/security/email/bind.json";
	//-我关注的人-
	//获取关注的人 member/attention/page.json
	public static final String MEMBER_ATTENTION_PAGE = NOW_ADDRESS + "member/attention/page.json";
	//删除我关注的人 member/attention/delete.json
	public static final String MEMBER_ATTENTION_DELETE = NOW_ADDRESS + "member/attention/delete.json";
	//关注别人 member/attention/save.json
	public static final String MEMBER_ATTENTION_SAVE = NOW_ADDRESS + "member/attention/save.json";
	//-发布创意-
	//上传创意图片 member/contribute/img.json
	public static final String MEMBER_CONTRIBUTE_IMG = NOW_ADDRESS + "member/contribute/img.json";
	//发布创意 member/contribute/save.json
	public static final String MEMBER_CONTRIBUTE_SAVE = NOW_ADDRESS + "member/contribute/save.json";
	//创意修改 member/contribute/edit.json
	public static final String MEMBER_CONTRIBUTE_EDIT = NOW_ADDRESS + "member/contribute/edit.json";
	//创意更新 member/contribute/update.json
	public static final String MEMBER_CONTRIBUTE_UPDATE = NOW_ADDRESS + "member/contribute/update.json";
	//创意删除 member/contribute/delete.json
	public static final String MEMBER_CONTRIBUTE_DELETE = NOW_ADDRESS + "member/contribute/delete.json";
	//创意完成 member/contribute/finish.json
	public static final String MEMBER_CONTRIBUTE_FINISH = NOW_ADDRESS + "member/contribute/finish.json";
	//-发起活动-
	//发起活动 member/activity/save.json
	public static final String MEMBER_ACTIVITY_SAVE = NOW_ADDRESS + "member/activity/save.json";
	//-申请代理-
	//代理修改 member/agent/edit.json
	public static final String MEMBER_AGENT_EDIT = NOW_ADDRESS + " member/agent/edit.json";
	//申请代理 member/agent/apply.json
	public static final String MEMBER_AGENT_APPLY = NOW_ADDRESS + "member/agent/apply.json";
	//-申请融资-
	//上传计划书文档 member/rong/img.json
	public static final String MEMBER_RONG_IMG = NOW_ADDRESS + "member/rong/img.json";
	//申请融资 member/rong/apply.json
	public static final String MEMBER_RONG_APPLY = NOW_ADDRESS + "member/rong/apply.json";
	//融资修改 member/rong/edit.json
	public static final String MEMBER_RONG_EDIT = NOW_ADDRESS + "member/rong/edit.json";
	//融资更新 member/rong/update.json
	public static final String MEMBER_RONG_UPDATE = NOW_ADDRESS + "member/rong/update.json";
	//融资删除 member/rong/delete.json
	public static final String MEMBER_RONG_DELETE = NOW_ADDRESS + "member/rong/delete.json";
	/**
	 * 个人主页
	 */
	//创意列表 space/contribute/page.json
	public static final String SPACE_CONTRIBUTE_PAGE = NOW_ADDRESS + "space/contribute/page.json";
	//发起的活动 space/activity/page.json
	public static final String SPACE_ACTIVITY_PAGE = NOW_ADDRESS + "space/activity/page.json";
	//关注的创意 space/collection/page.json
	public static final String SPACE_COLLECTION_PAGE = NOW_ADDRESS + "space/collection/page.json";
	//关注的人 space/attention/page.json
	public static final String SPACE_ATTENTION_PAGE = NOW_ADDRESS + "space/attention/page.json";
	//粉丝列表 space/fans/page.json
	public static final String SPACE_FANS_PAGE = NOW_ADDRESS + "space/fans/page.json";
	//评论列表 space/comments/page.json
	public static final String SPACE_COMMENTS_PAGE = NOW_ADDRESS + "space/comments/page.json";
	/**
	 * 孵化和麦客风
	 */
	//文章列表(不带分页) article/list.json
	public static final String ARTICLE_LIST = NOW_ADDRESS + "article/list.json";
	//文章列表(带分页) article/page.json
	public static final String ARTICLE_PAGE = NOW_ADDRESS + "article/page.json";
	//文章详情 article/detail.json  
	public static final String ARTICLE_DETAIL = NOW_ADDRESS + "article/detail.json";
	//文章栏目详情(文章栏目中有内容的用该功能) article_channel/detail.json  
	public static final String ARTICLE_CHANNEL_DETAIL = NOW_ADDRESS + "article_channel/detail.json";
	
	//用户详情
	public static final String USER_DETAIL = NOW_ADDRESS + "user/detail.json";
	//检查更新
	public static final String CHECK_APP_VERSION = NOW_ADDRESS + "version.json";
	
	/**
	 * wap页
	 */
	//创意详情页
	public static final String CREATIVE_DETAIL_WAP = NOW_ADDRESS_WAP+"articleDetailWap.html?id=";
	//麦客风文章详情页
	public static final String MKF_DETAIL_WAP = NOW_ADDRESS_WAP+"articleDetailWap.html?id=";
	//创业大赛首页
	public static final String START_BUSINESS_WAP = NOW_CYDS_ADDRESS_WAP;
	//创业大赛个人中心
	public static final String START_BUSINESS_PSCENTER_WAP = NOW_CYDS_ADDRESS_WAP+"member/index.html";
	//创业大赛验证验证用户登录
	public static final String START_BUSINESS_CHECK_LOGIN = NOW_CYDS_ADDRESS_WAP+"appWapLogin.html?verifyCode=";
	
	//注销登录
	public static final String START_BUSINESS_LOGIN_OUT = NOW_CYDS_ADDRESS_WAP+"appWapLogout.html?verifyCode=";
	
}
