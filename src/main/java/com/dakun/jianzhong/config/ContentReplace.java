package com.dakun.jianzhong.config;

import java.io.*;

/**
 * 在文件指定位置添加注解
 * @author Administrator
 *
 */
public class ContentReplace {
    private static  int i=0;
    public static void main(String[] args) {
        //File file = new File("D:\\Program Files\\project\\IdeaProjects\\social-service\\src\\main\\java\\com\\dakun\\jianzhong\\controller");
        //File file = new File("D:\\Program Files\\project\\IdeaProjects\\resource-service\\src\\main\\java\\com\\dakun\\jianzhong\\controller");
        //File file = new File("D:\\Program Files\\project\\IdeaProjects\\product-service\\src\\main\\java\\com\\dakun\\jianzhong\\controller");
        File file = new File("D:\\Program Files\\project\\IdeaProjects\\account-service\\src\\main\\java\\com\\dakun\\jianzhong\\controller");
        list(file);
        System.out.println("修改地方："+i+"处。");
    }

    private static void list(File file){
        File[] listFiles = file.listFiles();
        for (File file2 : listFiles) {
            if (file2.isFile()) {
                //BufferedInputStream bs = new BufferedInputStream(new FileInputStream(file2));
                try {
                    StringBuffer sb = new StringBuffer();
                    BufferedReader br = new BufferedReader(new FileReader(file2));
                    String s="";
                    boolean change=false;
                    while ((s=br.readLine())!=null) {
                        if (s.indexOf("@PostMapping")>-1||s.indexOf("@GetMapping")>-1||s.indexOf("@RequestMapping")>-1) {
                            sb.append(s);
                            sb.append("\r\n");
                            change=true;
                            sb.append("    @IsPublic");
                            i++;
                        }else {
                            sb.append(s);
                        }
                        sb.append("\r\n");
                    }
                    br.close();
                    if (change) {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(file2));
                        bw.write(sb.toString());
                        bw.close();
                        System.out.println(++i);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }else {
                list(file2);
            }
        }
    }
}
