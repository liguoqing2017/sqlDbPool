package jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA
 *
 * @author : liguoqing
 * @date : 2019/3/14
 * Time: 14:20
 * Description:
 */
public class DBConnectionPool  extends   Pool {
    
    private  int  checkedOut ; //正在使用的连接数
    private Vector<Connection> freeConnections  =  new Vector<Connection>(); //存放产生的连接对象容器
    private  String passWord =null;//密码
    private String  url  =  null;//链接字符串
    private String  userName = null;//用户名
    private static  int num =0;//空闲链接数
    private static int  numActive  =0;//当前可用链接数
    
    private static  DBConnectionPool  pool  = null; //连接池实例变量
    
    
    public  static  synchronized   DBConnectionPool  getInstance(){
        if(pool ==null){
            pool  =  new DBConnectionPool();
        }
        return pool;
    }
    
    private   DBConnectionPool(){
        try{
          init();
            for (int i = 0; i <normalConnect ; i++) {
                Connection  c  =  newConnection();
                if(c!=null){
                    freeConnections.addElement(c);
                    num++;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    private  void init()  throws IOException{
        InputStream  is  = DBConnectionPool.class.getResourceAsStream(propertiesName);
        Properties  p  =  new Properties();
        p.load(is);
        this.userName  =p.getProperty("userName");
        this.passWord  =p.getProperty("passWord");
        this.driverName = p.getProperty("driverName");
        this.maxConnect  =Integer.parseInt( p.getProperty("maxConnect"));
        this.normalConnect = Integer.parseInt(p.getProperty("normalConnect"));
    }

    @Override
    public void createPool() {
       pool  =  new DBConnectionPool();
       if(pool !=null){
           System.out.println("创捷连接池成功");
       }else{
           System.out.println("创建连接池失败");
       }
    }

    @Override
    public synchronized Connection getConnection(long time) {
        long  startTime  =  new Date().getTime();
        Connection con;
        while((con =getConnection())==null){
            try{
                wait(time);
            }catch(InterruptedException  e){
                
            }
            if((new Date().getTime()-startTime)>=time){
             return   null;
            }
        }
        return con;
    }

    @Override
    public synchronized void freeConnection(Connection con) {
   freeConnections.add(con);
   num++;
   checkedOut--;
   numActive--;
   notifyAll();
    }
    
    
    

    @Override
    public int getnum() {
        return 0;
    }

    @Override
    public int getnumActive() {
        return 0;
    }
    
    private Connection  newConnection(){
        Connection  con  =null;
        try{
            if(userName  ==null){
                con = DriverManager.getConnection(url);
            }else{
                con = DriverManager.getConnection(url,userName,passWord);
            }
            System.out.println("连接池创建一个新的连接");
        }catch(SQLException e){
            System.out.println("无法创建这个url"+url);
            return null;
        }
        return con;
    }

    /**
     * (单例模式)获取一个可用连接
     * @return
     */
    public synchronized   Connection  getConnection(){
        Connection  con  = null;
        if(freeConnections.size()>0){
            num--;
            con  =(Connection)freeConnections.firstElement();
            freeConnections.removeElementAt(0);
            try{
               if(con.isClosed()){
                   System.out.println("从连接池中删除一个无效链接");
                   con = getConnection();
               }
            }catch(SQLException e){
                System.out.println("从连接池删除一个无效连接");
                con  = getConnection();
            }
        }else{
            con  = newConnection();
        }
        
        if(con!=null){
            checkedOut ++;
        }
        numActive++;
        return con;
    }
    
    public synchronized  void release(){
        
    }
    
}
