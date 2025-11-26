package com.admin.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 后台管理系统启动类
 *
 * @author Admin
 */
@SpringBootApplication
@MapperScan("com.admin.system.mapper")
@EnableScheduling
public class AdminSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminSystemApplication.class, args);
        System.out.println("\n" +
                "  ___      _           _         ____            _                 \n" +
                " / _ \\    | |         (_)       / ___|          | |                \n" +
                "/ /_\\ \\ __| | _ __ ___  _  _ __ \\ `--.  _   _  _| |_  ___  _ __ ___\n" +
                "|  _  |/ _` || '_ ` _ \\| || '_ \\ `--. \\| | | |/ _` | / _ \\| '_ ` _ \\\n" +
                "| | | | (_| || | | | | || || | | /\\__/ /| |_| | (_| || (_) || | | | | |\n" +
                "\\_| |_/\\__,_||_| |_| |_||_||_| |_\\____/  \\__, |\\__,_| \\___/|_| |_| |_|\n" +
                "                                          __/ |                      \n" +
                "                                         |___/                       \n" +
                "\n后台管理系统启动成功！访问地址：http://localhost:8080/api\n");
    }

}
