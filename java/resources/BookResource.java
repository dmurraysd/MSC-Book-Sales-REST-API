/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import entities.Books;
import exceptionfunction.CustomEx;
import java.util.List;
import javax.annotation.PreDestroy;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import service.BooksFacade;

/**
 * REST Web Service
 *
 * @author David_killa
 */
//@Interceptors(TXCatcher.class)
//@RolesAllowed({"administrator", "developer"})
@Stateless
@Path("books")
public class BookResource 
{
        @Context
        SecurityContext secure_ctx;
    
        @EJB
        private BooksFacade book_facade;
        
        private Books local_book;
       
        /**
     *
     * @param entity
     * @return
     */
        
        @POST
        @Consumes({"application/xml", "application/json"})
        public Response create(Books entity) 
        {
            if(!secure_ctx.isUserInRole("administrator"))throw new CustomEx(401,"\n<->Not authorized to add books<->"); 
            this.validateData(entity);
            if(book_facade.findByIsbn(entity.getIsbn()) != null)throw new CustomEx(409,"\n<->Book with ISBN already extists<->"); 
            try 
            {
                book_facade.create(entity);
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
        public Response edit(Books entity) 
        {
            if(!secure_ctx.isUserInRole("administrator"))throw new CustomEx(401,"\n<->Not authorized to edit books<->"); 
            this.validateData(entity);
            Books  temp = book_facade.findByIsbn(entity.getIsbn());
            if(temp == null)return Response.status(Response.Status.NOT_FOUND).entity(entity + "\nBook with isbn not found").build();
            try 
            {
                book_facade.edit(entity);
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
        @Path("{isbn}")
        public Response remove(@PathParam("isbn") String isbn) 
        {
            if(!secure_ctx.isUserInRole("administrator"))throw new CustomEx(401,"\n<->Not authorized to delete books<->"); 
            long l_isbn = 0;
            try 
                {
                    l_isbn = Long.valueOf(isbn);
                } catch (NumberFormatException e) 
                {
                    throw new NumberFormatException("<->Incorrect query symbols or query parameters<->");
                    //return Response.status(Response.Status.NOT_FOUND).entity(entity).build();
                }
            
            if(l_isbn <= 0)throw new CustomEx(401,"<->Incorrect isbn number<->");
            local_book = book_facade.findByIsbn(l_isbn);
            if(local_book == null)return Response.status(Response.Status.NOT_FOUND).build();
            book_facade.remove(local_book);
            return Response.ok(local_book).build();
        }

        /**
     *
     * @param id
     * @return
     */
        @GET
        @Path("{queryvalue}")
        @Produces({"application/xml", "application/json"})
        public Response find(@PathParam("queryvalue") String queryvalue, @NotNull @QueryParam("value") String searchvalue)
        {
           return book_facade.findByParameters(queryvalue, searchvalue);
        }
    
        /**
     *
     * @return
     */
        @GET
        @Produces({"application/xml", "application/json"})
        public List<Books> findAll()  
        {
            
            return book_facade.findAll();
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
        public List<Books> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) 
        {
            return book_facade.findRange(new int[]{from, to});
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
            return String.valueOf(book_facade.count());
        }
        
        public void validateData(Books entity)
        {
            if(entity == null)throw new CustomEx(401,"<->No data in request<->");
            if(entity.getIsbn() <= 0)throw new CustomEx(401,"<->Incorrect isbn number<->");
            if("".equals(entity.getTitle()))throw new CustomEx(401,"<->No title entered<->");
            if("".equals(entity.getAuthor()))throw new CustomEx(401,"<->No Author entered<->");
            if("".equals(entity.getCopyright()))throw new CustomEx(401,"<->No Copyright entered<->");
            if("".equals(entity.getPublisher()))throw new CustomEx(401,"<->No publisher entered<->");
            if(entity.getPrice() == 0)throw new CustomEx(401,"<->No price entered<->");
            if(entity.getQuantity() <= 0)throw new CustomEx(401,"<->Quantity must be at least 1<->");
        }

}
