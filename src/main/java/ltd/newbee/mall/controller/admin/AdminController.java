/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.AdminUser;
import ltd.newbee.mall.service.AdminUserService;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 这个是后台管理相关接口
 * @link https://github.com/newbee-ltd
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

	/// @Resource和@Autowired区别：
	/// 首先@Resource是javax.annotation.Resource的注解，不是spring的注解(import有前缀org.springframework)
	/// @Autowired是byteType自动装配对象，只要有这个类型的对象就会装配对象，在Android中可以为null，但在spring中不能为null，除非设置require=FALSE。
	/// 假如同类型的对象有多个的时候，就会冲突，编译的时候报错“类名冲突”，除非结合@Qualifier成为byteName。
	/// 而@Resource是byteName装配，有相同类型而且名字相同的对象就装配，没有则初始化对象。
	@Resource
	private AdminUserService adminUserService;

	/// 这是spring4.3引入的简化HTTP方法的映射
	/// @GetMapping是一个组合注解，是@RequestMapping(method = RequestMethod.GET)的缩写。该注解将HTTP Get 映射到 特定的处理方法上。
	/// 如果是前后端分离，不会用到这样的方法，直接返回
	@GetMapping({ "/login" })
	public String login() {
		return "admin/login";
	}

	@GetMapping({ "/test" })
	public String test() {
		return "admin/test";
	}

	@GetMapping({ "", "/", "/index", "/index.html" })
	public String index(HttpServletRequest request) {
		request.setAttribute("path", "index");
		return "admin/index";
	}

	/// 登录成功后将用户信息包括userId都放在session随时用，这里的userId是事先在数据库人为写入的，真实情况下应该是注册后写入新生成的随机UUID才对
	@PostMapping(value = "/login")
	public String login(
			@RequestParam("userName") String userName, 
			@RequestParam("password") String password,
			@RequestParam("verifyCode") String verifyCode, 
			HttpSession session) {
		if (StringUtils.isEmpty(verifyCode)) {
			session.setAttribute("errorMsg", "验证码不能为空");
			return "admin/login";
		}
		if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
			session.setAttribute("errorMsg", "用户名或密码不能为空");
			return "admin/login";
		}
		String kaptchaCode = session.getAttribute("verifyCode") + "";
		if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.toLowerCase().equals(kaptchaCode)) {
			session.setAttribute("errorMsg", "验证码错误");
			return "admin/login";
		}
		AdminUser adminUser = adminUserService.login(userName, password);
		if (adminUser != null) {
			session.setAttribute("loginUser", adminUser.getNickName());
			session.setAttribute("loginUserId", adminUser.getAdminUserId());
			// session过期时间设置为7200秒 即两小时
			// session.setMaxInactiveInterval(60 * 60 * 2);
			return "redirect:/admin/index";
		} else {
			session.setAttribute("errorMsg", "登录失败");
			return "admin/login";
		}
	}

	@GetMapping("/profile")
	public String profile(HttpServletRequest request) {
		Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
		AdminUser adminUser = adminUserService.getUserDetailById(loginUserId);
		if (adminUser == null) {
			return "admin/login";
		}
		request.setAttribute("path", "profile");
		request.setAttribute("loginUserName", adminUser.getLoginUserName());
		request.setAttribute("nickName", adminUser.getNickName());
		return "admin/profile";
	}

	@PostMapping("/profile/password")
	@ResponseBody
	public String passwordUpdate(HttpServletRequest request, @RequestParam("originalPassword") String originalPassword,
			@RequestParam("newPassword") String newPassword) {
		if (StringUtils.isEmpty(originalPassword) || StringUtils.isEmpty(newPassword)) {
			return "参数不能为空";
		}
		Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
		if (adminUserService.updatePassword(loginUserId, originalPassword, newPassword)) {
			// 修改成功后清空session中的数据，前端控制跳转至登录页
			request.getSession().removeAttribute("loginUserId");
			request.getSession().removeAttribute("loginUser");
			request.getSession().removeAttribute("errorMsg");
			return ServiceResultEnum.SUCCESS.getResult();
		} else {
			return "修改失败";
		}
	}

	@PostMapping("/profile/name")
	@ResponseBody
	public String nameUpdate(HttpServletRequest request, @RequestParam("loginUserName") String loginUserName, @RequestParam("nickName") String nickName) {
		if (StringUtils.isEmpty(loginUserName) || StringUtils.isEmpty(nickName)) {
			return "参数不能为空";
		}
		Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
		if (adminUserService.updateName(loginUserId, loginUserName, nickName)) {
			return ServiceResultEnum.SUCCESS.getResult();
		} else {
			return "修改失败";
		}
	}

	/// 如果像这个后台管理系统，用session存储userId作为身份验证，可行，但是如果是app用户，就不太行了，
	/// 毕竟用户太多，session能存这么多，访问的时候拿userId不一定能这么快
	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		request.getSession().removeAttribute("loginUserId");
		request.getSession().removeAttribute("loginUser");
		request.getSession().removeAttribute("errorMsg");
		return "admin/login";
	}
}
