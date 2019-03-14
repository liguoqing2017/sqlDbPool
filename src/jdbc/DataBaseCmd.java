package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA
 *
 * @author : liguoqing
 * @date : 2019/3/14
 * Time: 13:58
 * Description:
 */
public class DataBaseCmd {
    private PreparedStatement  pstmt =  null;//连接语句
    private Connection  con  =null;
    private ResultSet  rs =  null;
    private String datasource = null;//指定使用的数据源

    /**
     * 默认构造器
     */
    public  DataBaseCmd(){
        
    }

    public  DataBaseCmd  (String  datasource){
        this.datasource  = datasource;
    }
    
    private synchronized   void  initCon(){
        try{
            if(null ==con){
             if(null ==datasource  ||"".equals(datasource)){
                 con  =ConMannager.getConnection();
             }else{
                 con = ConMannager.getConnection(datasource);
             }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public ResultSet  excuteQuery(String sql,boolean cmtype,List values)throws  Exception{
        try{
            initCon();
            if(cmtype){
                pstmt  = con.prepareCall(sql);
            }else{
                pstmt  = con.prepareStatement(sql);
            }
            if(values != null && values.size()>0){
                setValues(pstmt,values);
            }
            rs= pstmt.executeQuery();
            return rs;
        }catch (Exception ex){
            throw ex;
        }
    }
    
    public  int excuteUpdate(String sql ,boolean cmdtype,List values)throws  Exception{
        int  noOfRows  =0;
        try{
            initCon();
            if(cmdtype){
                pstmt  = con.prepareCall(sql);
            }else{
                pstmt  = con.prepareStatement(sql);
            }
            if(values!=null && values.size()>0){
                setValues(pstmt,values);
            }
            noOfRows  = pstmt.executeUpdate();
        }catch(Exception e){
            throw  e;
                    
        }
        return noOfRows;
    }
            
    
    
    private void  closeAll(){
        closePstmt();
        closeResult();
        closeConnection();
    }
    
    public void closeConnection(){
        try{
            if(null != con && !con.isClosed()){
                ConMannager.closeCon(con);
                con =null;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public   void closePstmt(){
      if(null!= pstmt){
          try{
              pstmt.close();
              pstmt =null;
          }catch(SQLException e){
              e.printStackTrace();
          }
      }
    }
    
    public  void closeResult(){
        if(null!=rs){
            try{
                rs.close();
                rs=null;
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
    }
    private void setValues(PreparedStatement pstmt,List  values)throws SQLException{
        for (int i = 0; i <values.size() ; i++) {
            Object  v  = values.get(i);
            pstmt.setObject(i+1,v);
            
        }
    }
    public  void setDatasource(String datasource){
        this.datasource = datasource;
        
    }
}
