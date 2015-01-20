/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import entities.Users;
import exceptionfunction.CustomEx;
import java.util.List;
import javax.annotation.PreDestroy;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import service.UsersFacade;

/**
 *
 * @author David_killa
 */
@RolesAllowed("manager")
@Stateless
@Path("users")
public class UserResource 
{
    @EJB
    private UsersFacade user_facade;

    private Users local_user;
       
        /**
     *
     * @param entity
     * @return
     */
        //@RolesAllowed("administrator")
        @POST
        @Consumes({"application/xml", "application/json"})
        public Response create(Users entity) 
        {
            if(entity == null)throw new CustomEx(401,"\n<->Invalid request data<->");
            if("".equals(entity.getUsername()))throw new CustomEx(401,"\n<->No user name entered<->");
            if("".equals(entity.getPassword()))throw new CustomEx(401,"\n<->No password entered<->");
            if(user_facade.find(entity.getUsername()) != null)throw new CustomEx(500,"\nUsername already extists"); 
            try 
            {
                user_facade.create(entity);
            } catch (Exception ex) 
            {
                throw new CustomEx(ex);
            }
           return Response.status(Response.Status.CREATED).build();
        }
        @PreDestroy
        void PreDestroyFunction()
        {
            System.out.println("Shutdown");
        }
        /**
     *
     * @param entity
     * @return
     */
        @PUT
        @Consumes({"application/xml", "application/json"})
        public Response edit(Users entity) 
        {
            if(entity == null)throw new CustomEx(401,"\n<->Invalid request data<->");
            if("".equals(entity.getUsername()))throw new CustomEx(401,"\n<->No user name entered<->");
            if("".equals(entity.getPassword()))throw new CustomEx(401,"\n<->No password entered<->");
            Users  temp = user_facade.find(entity.getUsername());
            if(temp == null)return Response.status(Response.Status.NOT_FOUND).entity(entity + "\nUsername not found").build();
            try 
            {
                user_facade.edit(entity);
            } catch (Exception ex) 
            {
                
                throw new CustomEx(ex);
                //throw new Exception(ex);
            }
            return Response.status(Response.Status.OK).entity(entity).build();
        }
        /**
     *
     * @param id
     * @return
     */
        @DELETE
        @Path("{username}")
        public Response remove(@PathParam("username") String username) 
        {
            if("".equals(username))throw new CustomEx(401,"\n<->No user name entered<->");
            local_user = user_facade.find(username);
            if(local_user == null)return Response.status(Response.Status.NOT_FOUND).build();
            user_facade.remove(local_user);
            return Response.ok(local_user).build();
        }

        /**
     *
     * @param id
     * @return
     */
        @GET
        @Path("{username}")
        @Produces({"application/xml", "application/json"})
        public Response find(@PathParam("username") String username) //throws Throwable 
        {
           if("".equals(username))throw new CustomEx(401,"\n<->No user name entered<->");
           if(user_facade.find(username) == null)Response.status(Response.Status.NOT_FOUND).build();
           return Response.status(Response.Status.OK).entity(user_facade.find(username)).build();
        }
    
        /**
     *
     * @return
     */
        @GET
        @Produces({"application/xml", "application/json"})
        public List<Users> findAll()  
        {
            
            return user_facade.findAll();
        }

        /**
     *
     * @param from
     * @param to
     * @return
     */
        /*@GET
        @Path("{from}/{to}")
        @Produces({"application/xml", "application/json"})
        public List<Users> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) 
        {
            return user_facade.findRange(new int[]{from, to});
        }*/

        /**
     *
     * @return
     */
        @GET
        @Path("count")
        @Produces("text/plain")
        public String countREST() 
        {
            return String.valueOf(user_facade.count());
        }
}
