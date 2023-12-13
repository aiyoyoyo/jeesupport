package com.jees.tool.license;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Scanner;

/**
 * 获取计算机的相关信息
 *
 * @author aiyoyoyo
 */
@Log4j2
public class MachineSerial {
    /**
     * 获取CPU序列号
     *
     * @return 序列号
     */
    public static String s_serial_CPU() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"wmic", "cpu", "get", "ProcessorId"});
            process.getOutputStream().close();
            @SuppressWarnings("resource")
            Scanner sc = new Scanner(process.getInputStream());
            sc.next();
            return sc.next();
        } catch (IOException e) {
            log.error("生成CPUSerial失败", e);
        }
        return null;
    }

    /**
     * 获取磁盘卷标
     *
     * @param _drive 硬盘驱动器分区 如C,D
     * @return 硬盘标识符
     */
    public static String s_serial_HD(String _drive) {
        StringBuilder result = new StringBuilder();
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            try (FileWriter fw = new FileWriter(file)) {
                String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n" + "Set colDrives = objFSO.Drives\n"
                        + "Set objDrive = colDrives.item(\"" + _drive + "\")\n" + "Wscript.Echo objDrive.SerialNumber";
                fw.write(vbs);
            }
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    result.append(line);
                }
            }
            file.delete();
        } catch (Throwable e) {
            log.error("生成HDSerial失败", e);
        }
        if (result.length() < 1) {
            log.error("无磁盘ID被读取");
        }

        return result.toString();
    }

    /**
     * 获取MAC地址
     *
     * @param _ia 网络地址
     * @throws SocketException 解析异常
     */
    public static void _s_serial_MAC(InetAddress _ia) throws SocketException {
        byte[] mac = NetworkInterface.getByInetAddress(_ia).getHardwareAddress();

        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            // 字节转换为整数
            int temp = mac[i] & 0xff;
            String str = Integer.toHexString(temp);

            if (str.length() == 1) {
                sb.append("0" + str);
            } else {
                sb.append(str);
            }
        }

        log.error("本机MAC地址:" + sb.toString().toUpperCase());
    }

    /**
     * Window系统机器码规则
     *
     * @return
     */
    private static String _s_os_windows_code() {
        String cpu = s_serial_CPU();
        String hd = s_serial_HD("C");

        if (cpu == null || hd == null) {
            return null;
        }
        return cpu + hd;
    }

    private static String _s_os_linux_code(){
        String cpu = s_serial_linux_CPU();
        String hd = s_os_bois_version();

        if (cpu == null || hd == null) {
            return null;
        }
        return cpu + hd;
    }

    /**
     * 获取当前计算机的机器码
     *
     * @return 机器码
     */
    public static String s_code() {
        String property = System.getProperty("os.name").toLowerCase();
        if (property.contains("windows")) {
            return _s_os_windows_code();
        } else if (property.contains("linux")) {
            return _s_os_linux_code();
        }
        return _s_os_windows_code();
    }

    /* 注：liunx上 如果想获取的话 需要root用户来执行；
     如果使用普通用户 执行的话 需要输入当前用户的密码（普通用户不支持dmidecode命令 因为没权限）
    */
    /**
     * bois版本号(linux) * * @return
     */
    public static String s_os_bois_version() {
        String result = "";
        Process p;
        try {
            p = Runtime.getRuntime().exec("sudo dmidecode -s bios-version");
            // 管道
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                result += line;
                break;
            }
            br.close();
        } catch (IOException e) {
            log.error("获取主板信息错误:", e);
        }
        return result;
    }

    /**
     * 获取系统序列号(linux)
     * @return
     */
    public static String s_serial_linux_CPU() {
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec("sudo dmidecode -s system-uuid");
            InputStream in;
            BufferedReader br;
            in = process.getInputStream();
            br = new BufferedReader(new InputStreamReader(in));
            while (in.read() != -1) {
                result = br.readLine();
            }
            br.close();
            in.close();
            process.destroy();
        } catch (Throwable e) {
            log.error("获取本机序列失败:", e);
        }
        return result;
    }
}
