/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptionfunction;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.DuplicateKeyException;
import javax.ejb.EJBException;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.RollbackException;
import javax.transaction.UserTransaction;
import javax.ws.rs.core.Response;

/**
 *
 * @author David_killa
 */
    public class TXCatcher
    {

        @Resource
        UserTransaction tx;
        private final static Logger LOG = Logger.getLogger(TXCatcher.class.getName());

        /**
     *
     * @param ic
     * @return
     * @throws Exception
     */
    @AroundInvoke
        public Object beginAndCommit(InvocationContext ic) throws Exception 
        {
            //ic.proceed();
            System.out.println("Invoking method: " + ic.getMethod());
            System.out.println("\nheya");
            try 
            {
               // tx.begin();
                Object retVal = ic.proceed();
                //tx.commit();
                return retVal;
            }catch (RollbackException e) 
            {
                LOG.log(Level.SEVERE, "-----------------Caught roolback(in interceptor): {0}", e.getCause());
                System.out.println("Invoking method: " + ic.getMethod());
                //throw new CustomEx("Database error");
            }catch (RuntimeException e) 
            {
                LOG.log(Level.SEVERE, "-----------------Caught runtime (in interceptor): {0}", e.getCause());
                //System.out.println(e.getCause().getCause().toString());
                //tx.rollback();
                ///Response.status(500).
                //if(e.getCause().)
                //if(e instanceof DuplicateKeyException)
                //throw new CustomEx("Database error","",401);
                //throw new CustomEx("Database error");
            }
            return null;
            
            
            //return ic.proceed();
       

        }
}

    
