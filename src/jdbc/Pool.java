package jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA
 *  自定义连接池 getInstance ()  返回POOL唯一实例，第一次调用时将执行构造函数
 *  构造函数pool（）调用驱动装在loadDrivers()函数；连接池穿件createPool()函数 loadDrivers()装载驱动
 *  createPool()创建连接池，getConnection()返回一个连接实例 getConnection(long time) 添加时间限制
 *  freeConnection(Connection con)将con连接实例返回到连接池，getnum()返回空闲连接数
 *  getnumActive()返回当前连接数
 *  
 * @author : liguoqing
 * @date : 2019/3/12
 * Time: 9:53
 * Description:
 */
public  abstract class Pool {
    public String   propertiesName ="connecttion-INF.properties";
    private static Pool instance =  null;//定义唯一实例
    
    protected  int maxConnect  =100; //最大连接数
    
    protected  int normalConnect =10; //保持连接数
    
    protected  String  driverName  =null;//驱动字符串
    
    //驱动类
    protected Driver driver  =null;  //驱动变量
    
    protected  Pool(){
        try {
            init();
            loadDrivers(driverName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private  void  init()  throws IOException{
        InputStream  is  = Pool.class.getResourceAsStream(propertiesName);
        Properties  p= new Properties();
        p.load(is);
        this.driverName  = p.getProperty("driverName");
        this.maxConnect  =  Integer.parseInt(p.getProperty("maxConnect"));
        this.normalConnect  = Integer.parseInt(p.getProperty("normalConnect"));
        
    }
    
    protected  void loadDrivers(String  dri){
        String driverClassName  = dri;
        try{
            driver  = (Driver) Class.forName(driverClassName).newInstance();
            DriverManager.registerDriver(driver);
            System.out.println("成功注册JDBC驱动程序"+driverClassName);
        }catch(Exception e){
            System.out.println("无法注册JDBC驱动程序" + driverClassName+",错误："+e);     
            }
        
    }

    /**
     * 创建连接池
     */
    public abstract   void  createPool();
    
    
    public  static  synchronized   Pool  getInstance()  throws  IOException,InstantiationException,IllegalAccessException,ClassNotFoundException{
        if(instance  ==null){
            instance.init();
            instance  =(Pool)Class.forName("org.e_book").newInstance();
        }
        return instance;
    }

    /**
     * 获取一个连接，有时间限制，单位毫秒
     * @param time
     * @return
     */
    public  abstract Connection  getConnection(long time);

    /**
     * 将连接对象返回给连接池
     * @param con
     */
    public  abstract   void  freeConnection(Connection con);

    /**
     * 返回空闲连接数
     * @return
     */
    public abstract   int  getnum();

    /**
     * 获取当前工作连接数
     * @return
     */
    public abstract  int getnumActive();

    /**
     * 关闭所有链接，撤销驱动注册
     */
    protected  synchronized  void release(){
        try {
            DriverManager .deregisterDriver(driver);
            System.out.println("撤销JDBC驱动程序"+driver.getClass().getName());
        } catch (SQLException e) {
            System.out.println("无法撤销JDBC驱动程序注册:"+driver.getClass().getName());
        }


    }
}
