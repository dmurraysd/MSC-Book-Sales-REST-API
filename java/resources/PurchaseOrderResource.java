/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import entities.Books;
import entities.Customer;
import entities.PurchaseOrder;
import exceptionfunction.CustomEx;
import java.util.List;
import java.util.logging.Logger;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import service.BooksFacade;
import service.CustomerFacade;
import service.PurchaseOrderFacade;

/**
 *
 * @author David_killa
 */
//@RolesAllowed({"administrator", "developer"})
@Stateless
@Path("purchaseorder")
public class PurchaseOrderResource 
{
    @Context
    SecurityContext secure_ctx;
    
    @EJB
    private PurchaseOrderFacade order_facade;
    @EJB 
    private BooksFacade books_facade;
    @EJB
    private CustomerFacade customer_facade;
    
    private PurchaseOrder local_order;
    private Customer local_customer;
    private Books local_book;
    
    @POST
    @Consumes({"application/xml", "application/json"})
    public Response create(PurchaseOrder entity)
    {
        
        /*Check for authorisation - could have just checked for a non-manager role, but a new role may be added at a later stage*/
        if(!this.secure_ctx.isUserInRole("administrator") && !this.secure_ctx.isUserInRole("developer"))
            throw new CustomEx(401,"Not authorized to create orders");
        
        checkEntityData(entity);
        
        //check if book exists 
        local_book = books_facade.findByIsbn(entity.getIsbn());
        if(local_book == null)throw new CustomEx(401,"Book not found");
        
        //Check if customer exists
        local_customer = customer_facade.find(entity.getCustomer_id());
        if(local_customer == null)throw new CustomEx(401,"Customer not found");
        
        //check if it is a valid request
        if(entity.getQuantity() <=0)throw new CustomEx(401,"Quantity requested set at 0");
        
        //check if the book is available
        if(local_book.getQuantity() <= 0)throw new CustomEx(401,"Store Quantity at 0");
        
        //check if enough copies are available to satisfy oder
        if(entity.getQuantity() > local_book.getQuantity())throw new CustomEx(401,"Excess quantity requested");
        
        //set the owner of the order
        if(!secure_ctx.isUserInRole("administrator"))entity.setOwner(secure_ctx.getUserPrincipal().getName());
        else entity.setOwner("administrator");
        
        try 
        {
            //set the id for easy search if required, for order processors
            entity.setBookid(local_book.getBookid());
            
            order_facade.create(entity);
        } catch (Exception ex) 
        {
            throw new CustomEx(ex);
        }
        local_book.setQuantity(local_book.getQuantity() - entity.getQuantity());
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    public Response edit(PurchaseOrder entity) 
    {
        /*Check for authorisation - could have just checked for a non-manager role, but a new role may be added at a later stage*/
        if(!this.secure_ctx.isUserInRole("administrator") && !this.secure_ctx.isUserInRole("developer"))
            throw new CustomEx(401,"Not authorized to edit orders");
        
        checkEntityData(entity);
        if(entity.getOrderNum() == null)throw new CustomEx(401,"Incomplete order number entered");
        local_order = order_facade.find(entity.getOrderNum());
        if(local_order == null)return Response.status(Response.Status.NOT_FOUND).entity(entity + "\nOrder not found").build();
        
        if(local_order.isProcessed() == 1)Response.status(Response.Status.FORBIDDEN).entity(entity + "\nOrder already processed").build();
        //Logger LOG = Logger.getLogger(PurchaseOrderResource.class.getName());
        //String g = "j " ;
        //check if book exists 
        local_book = books_facade.findByIsbn(entity.getIsbn());
        //LOG.log(Level.INFO, g + entity.getIsbn());
        if(local_book == null)throw new CustomEx(401,"Book not found");
        
        
        //LOG.log(Level.INFO,entity.getCustomer_id().toString());
        //check if customer exists 
        
        local_customer = customer_facade.find(entity.getCustomer_id());
        if(local_customer == null)throw new CustomEx(401,"Customer not found");
        
        //check if it is a valid request
        if(entity.getQuantity() <=0)throw new CustomEx(401,"Quantity requested set at 0");
        
        //check if the book is available
        if(local_book.getQuantity() <= 0)throw new CustomEx(401,"Store Quantity at 0");
        
        //check if enough copies are available to satisfy oder
        if(entity.getQuantity() > local_book.getQuantity())throw new CustomEx(401,"Excess quantity requested");
        
        //set the owner of the order
        if(!secure_ctx.isUserInRole("administrator"))entity.setOwner(secure_ctx.getUserPrincipal().getName());
        else entity.setOwner("administrator");
        try 
        {
            order_facade.edit(entity);
        } catch (Exception ex) 
        {

            throw new CustomEx(ex);
            //throw new Exception(ex);
        }
        return Response.status(Response.Status.OK).entity(entity).build();
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") Integer id) 
    {
        /*Check for authorisation - could have just checked for a non-manager role, but a new role may be added at a later stage*/
        if(!this.secure_ctx.isUserInRole("administrator") && !this.secure_ctx.isUserInRole("developer"))
            throw new CustomEx(401,"Not authorized to create orders");
        
        if(id == null)throw new CustomEx(401,"Incomplete order number entered");
        local_order = order_facade.find(id);
        if(local_order == null)return Response.status(Response.Status.NOT_FOUND).build();
        if(!local_order.getOwner().equals(secure_ctx.getUserPrincipal().getName()) && !secure_ctx.isUserInRole("administrator"))
            return Response.status(Response.Status.NOT_FOUND).entity("\nOrder not found").build();
        if(local_order.isProcessed() == 1 && !secure_ctx.isUserInRole("administrator"))Response.status(Response.Status.FORBIDDEN).entity("\nOrder processed, unable to delete").build();
        order_facade.remove(local_order);
        return Response.ok(local_order).build();
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Response find(@PathParam("id") Integer id) 
    {
        if(id == null)throw new CustomEx(401,"Incomplete order number entered"); 
        local_order = order_facade.find(id);
        if(local_order == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        Logger LOG = Logger.getLogger(PurchaseOrderResource.class.getName());
        
        if(!local_order.getOwner().equals(secure_ctx.getUserPrincipal().getName()) && !secure_ctx.isUserInRole("administrator"))
            return Response.status(Response.Status.NOT_FOUND).entity("\nOrde2r not found").build();
    
         return Response.ok(order_facade.find(id)).build();
    }

    @GET
    @Produces({"application/xml", "application/json"})
    public List<PurchaseOrder> findAll() 
    {
        if(!secure_ctx.isUserInRole("administrator"))
            return order_facade.findAllByOwner(secure_ctx.getUserPrincipal().getName());
        
        return order_facade.findAll();
    }

    /*@GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<PurchaseOrder> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return order_facade.findRange(new int[]{from, to});
    }*/

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() 
    {
        return String.valueOf(order_facade.count());
    }
    
    ////////////////*************
    public void checkEntityData(PurchaseOrder entity)
    {
        if(entity == null)throw new CustomEx(401,"<->No data in request<->");
        if(entity.getIsbn() <= 0)throw new CustomEx(401,"<->Incomplete isbn entered<->");
        if(entity.getCustomer_id() == null)throw new CustomEx(401,"<->Incomplete customer id entered<->");
    }
}
