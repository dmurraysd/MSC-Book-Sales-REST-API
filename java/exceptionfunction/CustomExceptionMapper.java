/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptionfunction;

import javax.ejb.EJBException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author David_killa
 */
    @Provider
    public class CustomExceptionMapper implements ExceptionMapper<CustomEx>
    {
    /**
     *
     * @param exception
     * @return
     */
    @Override
    public Response toResponse(CustomEx exception) 
    {
        Response response = exception.getResponse();
        return Response.status(response.getStatus()).entity("except3").build();
    }

    }
          

