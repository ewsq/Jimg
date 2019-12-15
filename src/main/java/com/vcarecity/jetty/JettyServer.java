package com.vcarecity.jetty;

import com.vcarecity.appender.TextLog;
import com.vcarecity.utils.Log4jUtil;
import com.vcarecity.utils.YamlUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;

public class JettyServer {
    private static Logger logger = Logger.getLogger(TextLog.class);
    public static void main(String[] args) {
        try {
            Log4jUtil.InitLog4jConfig();
            String listen=YamlUtils.getValue("alpha.listen");
            // 进行服务器配置
            Server server = new Server(Integer.parseInt(listen));
            server.setHandler(new JettyReceive());
            // 启动服务器
            server.start();
            System.out.println("http 服务已启动！,监听端口："+listen+" 。");
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}