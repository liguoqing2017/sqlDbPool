package jdbc;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Created with IntelliJ IDEA
 *
 * @author : liguoqing
 * @date : 2019/3/14
 * Time: 14:11
 * Description:
 */
public class ConMannager {
    private static  Pool  pool;
    private static ConMannager  instance  =null;//单例对象
    
    private ConMannager(){
        
    }
    
    protected   static void closeCon(Connection  con ){
        pool.freeConnection(con);
        
    }
    
    private  static   ConMannager  getInstance(){
        if(null  == instance ){
            instance  =  new ConMannager();
        }
        return  instance;
    }
    
    
    protected   static  Connection  getConnection(){
        Connection  conn  =  null;
        try{
            if(pool  ==null){
                pool  = DBConnectionPool.getInstance();
                
            }
            conn  =  pool.getConnection(10);
        }catch(Exception  e){
            e.printStackTrace();;
        }
        return  conn;
    }
    
    
    protected   static  Connection  getConnection(String lookupStr){
    Connection  conn  =null;
        try {
            ConMannager.getInstance();
            Context  ctx = new InitialContext();
            DataSource  ds =(DataSource)ctx.lookup(lookupStr);
            conn  = ds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
