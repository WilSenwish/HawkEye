package com.littleyes.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.Enumeration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p> <b> IP 工具类 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Slf4j
public class IpUtils {

    /**
     * 未知IP地址字符串：{@value}
     */
    private static final String UNKNOWN_IP = "unknown";
    private static final String UNKNOWN_HOST = "unknown-host";

    private static final String DOT_IP_REGEX = "((25[0-5]|2[0-4]\\d|1\\d{2}|0?[1-9]\\d|0?0?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|0?[1-9]\\d|0?0?\\d)";
    private static final Pattern DOT_IP_PATTERN = Pattern.compile(DOT_IP_REGEX);

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;

    private static final int ONE_BYTE_LENGTH = 8;
    private static final int TWO_BYTE_LENGTH = 16;
    private static final int THREE_BYTE_LENGTH = 24;

    private static final Long MAX_ADDRESS_NUM = 4294967295L;

    private static final int OX00FFFFFF = 0x00ffffff;
    private static final int OX0000FFFF = 0x0000ffff;
    private static final int OX000000FF = 0x000000ff;

    private static final Long A_LOCAL_START = 167772160L;
    private static final Long A_LOCAL_END = 184549375L;

    private static final Long B_LOCAL_START = 2886729728L;
    private static final Long B_LOCAL_END = 2887778303L;

    private static final Long C_LOCAL_START = 3232235520L;
    private static final Long C_LOCAL_END = 3232301055L;

    /**
     * 获取本机 IP 地址
     */
    private final String localIp;

    /**
     * 获取本机 IP 地址
     */
    private final String localHostName;

    private static final IpUtils INSTANCE = new IpUtils();

    private IpUtils() {
        this.localIp = loadLocalHostAddress();
        this.localHostName = loadLocalHostName();
    }

    /**
     * 获取本机 IP 地址
     *
     * @return
     */
    public static String getLocalIp() {
        return getLocalHostAddress();
    }

    /**
     * 获取本机 IP 地址
     *
     * @return
     */
    public static String getLocalHostAddress() {
        return INSTANCE.localIp;
    }

    /**
     * 获取本机 HOST 名称
     *
     * @return
     */
    public static String getLocalHostName() {
        return INSTANCE.localHostName;
    }

    /**
     * 获取请求的真实IP地址
     *
     * @param request
     * @return
     */
    public static String getRequestHostAddress(HttpServletRequest request) {
        if (Objects.isNull(request)) {
            return INSTANCE.localIp;
        }

        return INSTANCE.getRequestRealHostAddress(request);
    }

    private String loadLocalHostAddress() {
        try {
            InetAddress candidateAddress = null;

            // 遍历所有的网络接口
            for (Enumeration interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                if (isValidInterface(networkInterface)) {
                    // 在所有的接口下再遍历 IP
                    for (Enumeration addresses = networkInterface.getInetAddresses(); addresses.hasMoreElements(); ) {
                        InetAddress inetAddress = (InetAddress) addresses.nextElement();

                        // 排除 loop back 类型地址且是 IPV4
                        if (isValidAddress(inetAddress)) {
                            // 如果是site-local地址，就是它了
                            if (inetAddress.isSiteLocalAddress()) {
                                return inetAddress.getHostAddress();
                            }

                            // site-local 类型的地址未被发现，先记录候选地址
                            else if (candidateAddress == null) {
                                candidateAddress = inetAddress;
                            }
                        }
                    }
                }
            }

            // 如有候选地址，则使用候选地址
            if (candidateAddress != null) {
                return candidateAddress.getHostAddress();
            }

            // 如果没有发现 non-loop back 地址，只能用最次选的方案
            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            log.warn(e.getMessage());
        }

        return StringUtils.EMPTY;
    }

    /**
     * 过滤非活动网卡、回环网卡、点对点网卡、虚拟网卡并要求网卡名字是 eth 或 ens 开头
     *
     * @param ni 网卡
     * @return 如果满足要求则true，否则false
     */
    private static boolean isValidInterface(NetworkInterface ni) throws SocketException {
        return ni.isUp() && !ni.isLoopback() && !ni.isPointToPoint() && !ni.isVirtual()
                && (ni.getName().startsWith("eth") || ni.getName().startsWith("ens"));
    }

    /**
     * 判断是否是IPv4，并且内网地址并过滤回环地址
     */
    private static boolean isValidAddress(InetAddress address) {
        return address instanceof Inet4Address && address.isSiteLocalAddress() && !address.isLoopbackAddress();
    }

    private String loadLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.warn(e.getMessage());
        }

        return UNKNOWN_HOST;
    }

    private String getRequestRealHostAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        if (isUnKnownIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (isUnKnownIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (isUnKnownIp(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }

        if (isUnKnownIp(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (isUnKnownIp(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    private static boolean isUnKnownIp(String ip) {
        return StringUtils.isBlank(ip) || UNKNOWN_IP.equalsIgnoreCase(ip);
    }

    public static boolean isValidIpAddress(String ipAddress) {
        Matcher m = DOT_IP_PATTERN.matcher(ipAddress);
        return m.matches();
    }

    public static boolean isNotValidIpAddress(String ipAddress) {
        return !isValidIpAddress(ipAddress);
    }

    public static boolean isLocalIp(String ipAddress) {
        if (isNotValidIpAddress(ipAddress)) {
            return false;
        } else {
            Long ip = ipToLongAddressNum(ipAddress);

            boolean isLocalA = (ip >= A_LOCAL_START && ip <= A_LOCAL_END);
            boolean isLocalB = (ip >= B_LOCAL_START && ip <= B_LOCAL_END);
            boolean isLocalC = (ip >= C_LOCAL_START && ip <= C_LOCAL_END);

            return (isLocalA || isLocalB || isLocalC);
        }
    }

    public static long ipToLongAddressNum(String ip) {
        if (isValidIpAddress(ip)) {
            String[] ipStrArray = ip.split("\\.");
            Long[] ipLongArray = new Long[FOUR];

            for (int i = 0; i < ipStrArray.length; i++) {
                ipLongArray[i] = Long.parseLong(ipStrArray[i]);
            }

            return (ipLongArray[ZERO] << THREE_BYTE_LENGTH)
                    + (ipLongArray[ONE] << TWO_BYTE_LENGTH)
                    + (ipLongArray[TWO] << ONE_BYTE_LENGTH)
                    + ipLongArray[THREE];
        } else {
            throw new IllegalArgumentException("点分式IP地址格式错误！");
        }
    }

    public static String longAddressNumToIp(Long addressNum) {
        if (addressNum >= 0L && addressNum <= MAX_ADDRESS_NUM) {
            Long[] ipLongArray = new Long[FOUR];

            ipLongArray[ZERO] = addressNum >> THREE_BYTE_LENGTH;
            ipLongArray[ONE] = (addressNum & OX00FFFFFF) >> TWO_BYTE_LENGTH;
            ipLongArray[TWO] = (addressNum & OX0000FFFF) >> ONE_BYTE_LENGTH;
            ipLongArray[THREE] = addressNum & OX000000FF;

            return StringUtils.join(ipLongArray, '.');
        } else {
            throw new IllegalArgumentException("十进制IP数值超出IP范围！");
        }
    }

}
