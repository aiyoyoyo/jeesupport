package com.jees.test;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.tool.utils.CustomSystemUtil;
import com.jees.tool.utils.FileUtil;
import com.jees.webs.core.interf.ISupport;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;

import java.util.Arrays;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ComponentScan( "com.jees" )
@DependsOn({"commonContextHolder", "commonConfig" })
@Log4j2
public class WebApplication {
    public static void main(String...args){
        System.out.println( "Start with:" + Arrays.toString( args ) );
        FileUtil.classpath();
        FileUtil.project();
        FileUtil.webroot();
        SpringApplication.run( WebApplication.class, args);
        CommonContextHolder.getBean( ISupport.class ).initialize();
        log.info( "服务器启动: http"
                + ( CommonConfig.getBoolean( "server.useSSL", false ) ? "s" : "" )
                + "://" + CustomSystemUtil.INTRANET_IP  + ":"
                + CommonConfig.getString( "server.port", "8080" )
                + CommonConfig.getString( "server.servlet.context-path", "/" )
        );
    }

    static int m = 4;
    static int n = 4;
    static int sum = 0;
    static int[][] state = {{1, 3, 5, 9}, {2, 1, 3, 4}, {5, 2, 6, 7}, {6, 8, 4, 3}};
    static int[][] matrix = new int[m][n];

    public static void dynamic_1(){
        for(int i = 0; i < m; i++){
            sum = sum + state[i][0];
            matrix[i][0] = sum;
        }
        sum = 0;
        for(int j = 0; j < n; j++){
            sum = sum + state[0][j];
            matrix[0][j] = sum;
        }

        for(int i = 1; i < m; i++){
            for(int j = 1; j < n; j++){
                matrix[i][j] = state[i][j] + Math.min(matrix[i - 1][j], matrix[i][j - 1]);
            }
        }

        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

//    public static void main(String[] argv){
//        int[][] arrs = new int[][] { { 0, 0, 0 }, { 0, 1, 0 }, { 0, 0, 0 } };
//        System.out.println(distinctPaths(arrs, 2, 2));
//    }

    /**
     * 算法分析： 当 arr[0][0] = 0时 +1 当 m = 0 并且 arr[m][n] = 0时 f(m,n) = f(m,n-1) 当 n = 0
     * 并且 arr[m][n] = 0时 f(m,n) = f(m-1,n) 当 arr[m][n] = 1 return 0 否则 f(m,n) =
     * f(m,n-1) + f(m-1,n)
     *
     * @param arrs
     * @param m
     * @param n
     * @return
     */
    public static int distinctPaths(int[][] arrs, int m, int n) {
        if (m == 0 && n == 0 && arrs[m][n] == 0)
            return 1;
        if (m == 0 && arrs[m][n] == 0)
            return distinctPaths(arrs, m, n - 1);
        if (n == 0 && arrs[m][n] == 0)
            return distinctPaths(arrs, m - 1, n);
        if (arrs[m][n] == 1)
            return 0;
        return distinctPaths(arrs, m - 1, n) + distinctPaths(arrs, m, n - 1);
    }
}
