package com.jees.server.message;

import com.alibaba.fastjson2.JSONObject;
import com.jees.common.CommonConfig;
import com.jees.tool.utils.FileUtil;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

@Log4j2
public class MessageFile<ID> {

    private static boolean check() {
        return CommonConfig.getBoolean("jees.jsts.message.jsonFile", false);
    }

    private static void _write_(Integer _cmd, Object _usr, String _str, boolean _toc) {
        if (_cmd == null) return;

        String file = CommonConfig.getString("jees.jsts.message.jsonPath");
        if (_usr == null) _usr = 0L;
        DateTimeFormatter fmt = DateTimeFormat.forPattern(
                CommonConfig.getString("jees.jsts.message.jsonFormat", "yyyy-MM-dd_HH-mm-ss-SSS"));
        file += _usr + "/" + _cmd + "/" + (_toc ? "S_" : "C_") + fmt.print(DateTime.now()) + ".json";

        try {
            if (CommonConfig.getBoolean("jees.jsts.message.jsonLogs", false)) {
                log.info("--生成消息文件：" + file);
            }
            FileUtil.write(_str, file, false);
        } catch (IOException e) {
            log.warn("写入文件失败：" + e.toString());
        }
    }

    public static void write(Integer _cmd, Object _usr, String _json, boolean _toc) {
        if (check()) {
            _write_(_cmd, _usr, _json, _toc);
        }
    }

    public static void write(Integer _cmd, Object _usr, JSONObject _job, boolean _toc) {
        if (check()) {
            _write_(_cmd, _usr, _job.toString(), _toc);
        }
    }


    public static void write(Integer _cmd, Object _usr, Message _msg, boolean _toc) {
        if (check()) {
            _write_(_cmd, _usr, _msg.toString(), _toc);
        }
    }
}
