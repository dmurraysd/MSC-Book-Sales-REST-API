/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptionfunction;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author David_killa
 */
public class CustomEx extends WebApplicationException
{
    private static final long serialVersionUID = 1L;
    private String reason;
    private int status;

    /**
     *
     * @param message
     * @param reason
     * @param status
     */
    public CustomEx(String message, String reason, int status) {
        //super(status);
        super(Response.status(status).entity(reason).build());
        this.reason = reason;
        this.status = status;
    }

    /**
     *
     * @param status
     * @param message
     */
    public CustomEx(int status,String message) {
        
        super(Response.status(status).entity(message).build());
        this.status = status;
    }

    /**
     * Filter exceptions to write custom response
     * @param ex
     */
    public CustomEx(Exception ex) 
    {
        //get newline character
        String eol = System.getProperty("line.separator") + "<->"; 
        String constraintviolation_str = String.format(">Book data incorrect" + eol + "   Probable Causes: " + eol + "\tinserted null values"
                    + eol + "\tincorrect data value sizes" + eol + "\tPlease look at data rules in api documentation"); 
        //casting Throwable object to SQL Exception
        Throwable t = unrollException(ex);
        if(t instanceof ConstraintViolationException)throw new CustomEx(400,constraintviolation_str);
        //if(ex.getCause() instanceof PersistenceException)throw new CustomEx(400,"innit");
        /**
         * Integrity constraint violation
         * Use to catch specific primary key violation
         * if(t instanceof org.apache.derby.client.am.SqlException)
         * if(t.getSQLState().equals("23505")) // Integrity constraint violation
         */
         throw new CustomEx(500,"Uncaught Exception");
         
    }
     private Throwable unrollException(Exception exception)
    {
        Throwable temp_throw = exception;
        while(temp_throw.getCause() != null)
        {
          temp_throw = temp_throw.getCause();
        }
        return temp_throw;
    }
    private Throwable unrollException(Throwable exception, Class<? extends Throwable> expected)
        {

        while(exception != null && exception != exception.getCause())
        {
          if(expected.isInstance(exception))
          {
            return exception;
          }
          exception = exception.getCause();
        }
        return null;
        }

   
}
