/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import entities.Groups;
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
import service.GroupsFacade;

/**
 *
 * @author David_killa
 */
@RolesAllowed("manager")
@Stateless
@Path("groups")
public class GroupResource 
{
    @EJB
    private GroupsFacade group_facade;

    private Groups local_book;
       
        /**
     *
     * @param entity
     * @return
     */
        //@RolesAllowed("administrator")
        @POST
        @Consumes({"application/xml", "application/json"})
        public Response create(Groups entity) 
        {
            if(entity == null)throw new CustomEx(401,"\n<->Invalid request data<->");
            if("".equals(entity.getUsername()))throw new CustomEx(401,"\n<->No user name entered<->");
            if("".equals(entity.getGroupname()))throw new CustomEx(401,"\n<->No group entered<->");
            if(entity.getGroupname() != "administrator" || entity.getGroupname() != "developer" || entity.getGroupname() != "manager")throw new CustomEx(401,"\n<->Group doesn't extist<->");
            if(group_facade.find(entity.getUsername()) != null)throw new CustomEx(500,"\nUsername already extists"); 
            try 
            {
                group_facade.create(entity);
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
        public Response edit(Groups entity) 
        {
            if(entity == null)throw new CustomEx(401,"\n<->Invalid request data<->");
            if("".equals(entity.getUsername()))throw new CustomEx(401,"\n<->No user name entered<->");
            if("".equals(entity.getGroupname()))throw new CustomEx(401,"\n<->No group entered<->");
            if(entity.getGroupname() != "administrator" || entity.getGroupname() != "developer" 
                    || entity.getGroupname() != "manager")throw new CustomEx(401,"\n<->Group doesn't extist<->");
            
            Groups temp = group_facade.find(entity.getUsername());
            if(temp == null)return Response.status(Response.Status.NOT_FOUND).entity(entity + "\nUser not found").build();
            try 
            {
                group_facade.edit(entity);
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
            local_book = group_facade.find(username);
            if(local_book == null)return Response.status(Response.Status.NOT_FOUND).build();
            group_facade.remove(local_book);
            return Response.ok(local_book).build();
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
            if(group_facade.find(username) == null)return Response.status(Response.Status.NOT_FOUND).build();
            
            return Response.ok(group_facade.find(username)).build();
        }
    
        /**
     *
     * @return
     */
        @GET
        @Produces({"application/xml", "application/json"})
        public List<Groups> findAll()  
        {
            return group_facade.findAll();
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
        public List<Groups> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) 
        {
            return group_facade.findRange(new int[]{from, to});
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
            return String.valueOf(group_facade.count());
        }
        
}
