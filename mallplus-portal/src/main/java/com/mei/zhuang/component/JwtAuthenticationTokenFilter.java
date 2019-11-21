package com.mei.zhuang.component;


import com.google.common.collect.Lists;
import com.mei.zhuang.util.IpAddressUtil;
import com.mei.zhuang.util.JwtTokenUtil;
import com.zscat.mallplus.sys.entity.SysWebLog;
import com.zscat.mallplus.sys.mapper.SysWebLogMapper;
import com.zscat.mallplus.utils.ValidatorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT登录授权过滤器
 * https://github.com/shenzhuan/mallplus on 2018/4/26.
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Resource
    public SysWebLogMapper fopSystemOperationLogService;

    public static final List<String> IGNORE_TENANT_TABLES = Lists.newArrayList(
            "user.info",
            "user.editinfo",
            "user.changeavatar",
            "user.logout",
            "user.addgoodsbrowsing",
            "user.delgoodsbrowsing",
            "user.goodsbrowsing",
            "user.goodscollection",
            "user.goodscollectionlist",
            "user.vuesaveusership",
            "user.saveusership",
            "user.getshipdetail",
            "user.setdefship",
            "user.editship",
            "user.removeship",
            "user.getusership",
            "api/wxpay/user.pay",
            "user.orderevaluate",
            "user.getuserdefaultship",
            "user.issign",
            "user.sign",
            "user.mypoint",
            "user.userpointlog",
            "user.getbankcardlist",
            "user.getdefaultbankcard",
            "user.addbankcard",
            "user.removebankcard",
            "user.setdefaultbankcard",
            "user.getbankcardinfo",
            "user.editpwd",
            "user.forgotpwd",
            "user.recommend",
            "user.balancelist",
            "user.sharecode",
            "user.cash",
            "user.cashlist",
            "user.myinvite",
            "user.activationinvite",
            "coupon.getcoupon",
            "coupon.usercoupon",
            "cart.add",
            "cart.del",
            "cart.getlist",
            "cart.setnums",
            "cart.getnumber",
            "order.cancel",
            "order.del",
            "order.details",
            "order.confirm",
            "order.getlist",
            "order.create",
            "submitPreview",
            "order.getship",
            "order.getorderlist",
            "order.getorderstatusnum",
            "order.aftersaleslist",
            "order.aftersalesinfo",
            "order.aftersalesstatus",
            "order.addaftersales",
            "order.sendreship",
            "order.iscomment",
            "payments.getinfo",
            "user.getuserpoint",
            "coupon.getcouponkey",
            "store.isclerk",
            "store.storeladinglist",
            "store.ladinginfo",
            "store.lading",
            "store.ladingdel",
            "distribution_center-api-info",
            "distribution_center-api-applydistribution",
            "distribution_center-api-setstore",
            "distribution_center-api-myorder",
            "pintuan.pintuanteam",
            "lottery-api-getLotteryConfig",
            "lottery-api-lottery",
            "lottery-api-lotteryLog");
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        long startTime, endTime;

        String requestType = ((HttpServletRequest) request).getMethod();
        SysWebLog sysLog = new SysWebLog();
        StringBuffer sbParams = new StringBuffer();
        if (!"get".equals(requestType.toLowerCase())) {
            Map<String, String[]> params = new HashMap<String, String[]>(request.getParameterMap());
            sbParams.append("?");
            for (String key : params.keySet()) {
                if (null == key || null == params.get(key) || null == params.get(key)[0]) {
                    continue;
                }
                sbParams.append(key).append("=").append(params.get(key)[0]).append("&");
            }

            if (sbParams.length() > 1) {
                sbParams = sbParams.delete(sbParams.length() - 1, sbParams.length());
            }
            sysLog.setParams(sbParams.toString());
        } else {
            sysLog.setParams(getBodyString(request));
        }

        String fullUrl = ((HttpServletRequest) request).getRequestURL().toString();
        String username = null;
        int startIntercept = fullUrl.replace("//", "a").indexOf("/") + 2;
        String interfaceName = fullUrl.substring(startIntercept, fullUrl.length());
        String tokenPre = this.tokenHeader ;
        String authHeader = request.getParameter(tokenPre);
        if (ValidatorUtils.empty(authHeader)){
            authHeader = request.getHeader(tokenPre);
        }
      //  if (  IGNORE_TENANT_TABLES.stream().anyMatch((e) -> e.equalsIgnoreCase(interfaceName))){

            if (authHeader != null && authHeader.startsWith("Bearer")) {
                String authToken = authHeader.substring("Bearer".length());
                username = jwtTokenUtil.getUserNameFromToken(authToken);
                LOGGER.info("checking username:{}", username);
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    if (userDetails!=null && jwtTokenUtil.validateToken(authToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        LOGGER.info("authenticated user:{}", username);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }else{
                        throw new ClassCastException();
                    }

            }else {
                logger.info("no token"+request.getRequestURI());
            }
       // }

        startTime = System.currentTimeMillis();
        chain.doFilter(request, response);
        endTime = System.currentTimeMillis();
        logger.info(formMapKey(11, fullUrl, requestType,
                IpAddressUtil.getIpAddr((HttpServletRequest) request), sbParams.toString(), authHeader)
                + ",\"cost\":\"" + (endTime - startTime) + "ms\"");
        sysLog.setCreateTime(new Date());
        sysLog.setIp(IpAddressUtil.getIpAddr(request));
        sysLog.setMethod(interfaceName);
        sysLog.setServiceName(requestType);

        sysLog.setOperationDesc(authHeader);
        sysLog.setUserName(username);
        sysLog.setTimeMin((endTime - startTime));
        if (!"OPTIONS".equals(requestType) && !interfaceName.contains("webjars")
                && !interfaceName.contains("api-docs")) {
        //    fopSystemOperationLogService.insert(sysLog);
        }
    }

    private String formMapKey(Object userId, String mothedName, String requestType,
                              String ip, String params, String token) {
        return "\"time\"" + ":\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date())
                + "\",\"name\"" + ":\"" + mothedName + "\",\"uid\":\"" + userId
                + "\",\"type\":\"" + requestType + "\",\"ip\":\"" + ip
                + "\",\"token\":\"" + token + "\",\"params\":\"" + params + "\"";
    }

    /**
     * 获取请求Body
     *
     * @param request
     * @return
     */
    public String getBodyString(final ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = cloneInputStream(request.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    /**
     * Description: 复制输入流</br>
     *
     * @param inputStream
     * @return</br>
     */
    public InputStream cloneInputStream(ServletInputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return byteArrayInputStream;
    }
}
