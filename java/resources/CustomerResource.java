/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import entities.Customer;
import exceptionfunction.CustomEx;
import java.util.List;
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
import service.CustomerFacade;

/**
 *
 * @author David_killa
 */
@RolesAllowed({"administrator", "developer"})
@Stateless
@Path("customer")
public class CustomerResource 
{
    @Context
    SecurityContext secure_ctx;
    
    @EJB
    private CustomerFacade customer_facade;
    
    private Customer local_customer;
   
    @POST
    @Consumes({"application/xml", "application/json"})
    public Response create(Customer entity) 
    {
        if(entity == null)throw new CustomEx(401,"<->No data in request<->");
        this.validateData(entity);
        //Check for existing customers with same name, addresses and email
        if(customer_facade.checkCustomerDetailsExist(entity) != -1)throw new CustomEx(500,"\nCustomer already extists"); 
        try 
        {
            if(secure_ctx.isUserInRole("developer"))
                entity.setOwner(secure_ctx.getUserPrincipal().getName());
            else entity.setOwner("administrator");
            customer_facade.create(entity);
        } catch (Exception ex) 
        {
            throw new CustomEx(ex);
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    public Response edit(Customer entity) 
    {
        /*
         * Check if user has the authority for this particular customer
         * and if the customer exists
         */
        if(entity == null)throw new CustomEx(401,"<->No data in request<->");
        this.validateData(entity);
        if(!validateOwnerSearch(entity.getCustomerId()))
                return Response.status(Response.Status.NOT_FOUND).entity(entity + "\nCustomer to be edited not found").build();
        
        /*
             * Check if the edited details are the same as another customer which is in the database already
             * checkCustomerDetailsExist returns ID of duplicate customer, just in case it is the same entity be edited
             * if result is -1, there is no duplicate details.
             * if the result is ID of customer been edited, edit is allowed
             * if result has a different ID, edit can't be allowed
             */
        long result = customer_facade.checkCustomerDetailsExist(entity);
        if(result != -1 && result != entity.getCustomerId())throw new CustomEx(500,"\nCustomer already exists with edited details"); 
        try 
        {
            if(secure_ctx.isUserInRole("developer"))
                entity.setOwner(secure_ctx.getUserPrincipal().getName());
            else entity.setOwner("administrator");
            customer_facade.edit(entity);
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
        /*
         * Check if user has the authority for this particular customer
         * and if the customer exists
         */
        if(!validateOwnerSearch(id))Response.status(Response.Status.NOT_FOUND).entity("\nCustomer not found").build();
        //if(local_customer == null)return Response.status(Response.Status.NOT_FOUND).build();
        
        customer_facade.remove(local_customer);
        
        return Response.ok(local_customer).build();
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Response find(@PathParam("id") Integer id) 
    {
        if(validateOwnerSearch(id))return Response.ok(local_customer).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Produces({"application/xml", "application/json"})
    public List<Customer> findAll() 
    {
         if(!secure_ctx.isUserInRole("administrator"))
            return customer_facade.findAllByOwner(secure_ctx.getUserPrincipal().getName());
        
        return customer_facade.findAll();
    }

    /*@GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Customer> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return customer_facade.findRange(new int[]{from, to});
    }*/

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() 
    {
        return String.valueOf(customer_facade.count());
    }
    private boolean validateOwnerSearch(Integer id) 
    {
        
        if(id == null)throw new CustomEx(401,"Incomplete customer ID entered");
        local_customer = customer_facade.find(id);
        
        if(local_customer == null)return false;
        
        if(!local_customer.getOwner().equals(secure_ctx.getUserPrincipal().getName()) && !secure_ctx.isUserInRole("administrator"))return false;
        
        return true;
    }
    
    private void validateData(Customer entity)
    {
        if("".equals(entity.getName()))throw new CustomEx(401,"<->No name entered<->");
        if("".equals(entity.getAddressline1()) || "".equals(entity.getAddressline2()))throw new CustomEx(401,"<->Incomplete Address entered<->");
        if("".equals(entity.getCity()))throw new CustomEx(401,"<->No city entered<->");
        if("".equals(entity.getCountry()))throw new CustomEx(401,"<->No country entered<->");
        if("".equals(entity.getEmail()))throw new CustomEx(401,"<->No email Address entered<->");
        if("".equals(entity.getProvince()))throw new CustomEx(401,"<->Province not entered<->");
    }
    
}
