/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.Books;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;

/**
 * 
 * @author David_killa
 * Uses facade pattern to manipulate a database's tables
 * BooksFacade allows the user to access the books entities
 * The unit restservicePU is the persistence instance that the manager is generated from.
 */

//@Interceptors(TXCatcher.class)
//@TransactionManagement(TransactionManagementType.BEAN)
@Stateless
public class BooksFacade extends AbstractFacade<Books> 
{
    @PersistenceContext(unitName = "restservicePU")
    private EntityManager em;
   

    /**
     * Returns the entity manager  
     * Used for AbstractFacade functions
     * @return
     */
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Sets abstract facade to the book entity
     */
    public BooksFacade() {
        super(Books.class);
    }

    /**
     * Persists a books entity to the database
     * @TransactionAttribute is used to catch exceptions in a CMT application
     * @param entity
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void create(Books entity) //throws javax.persistence.EntityExistsException
    {
        super.create(entity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void edit(Books entity) 
    {
        Books  temp = this.findByIsbn(entity.getIsbn());
        entity.setBookid(temp.getBookid());
        super.edit(entity); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     *
     * @param ISBN
     * @return null or the book entity with the chosen ISBN
     */
    public Books findByIsbn(long ISBN) 
    {
        Books temp_book = null;
        try 
        {
            temp_book = (Books)em.createNamedQuery("Books.findByIsbn").setParameter("isbn", ISBN).getSingleResult();
        } catch (Exception e) 
        {
            /*No need to deal with exception, not a rollback exception. 
             *The exception throws if the entity was not found
             */
        }
        return temp_book;
    }
    
    public Response findByParameters(String query_type, String searchvalue) 
    {
        long num = 0;
        if(searchvalue != null)
        {
            if(query_type.equals("isbn"))
            {
                try 
                {
                    num = Long.valueOf(searchvalue).longValue();
                } catch (NumberFormatException e) 
                {
                    throw new NumberFormatException("<->Incorrect query symbols or query parameters<->");
                    //return Response.status(Response.Status.NOT_FOUND).entity(entity).build();
                }
                //String s = String.valueOf(num);
                if(this.findByIsbn(num) != null)return Response.ok(this.findByIsbn(num)).build();
                return Response.status(Response.Status.NOT_FOUND).entity("\n<->Book not found<->").build();
            }
            else if(query_type.equals("author"))
            {
                List<Books> local_list = this.findByAuthor(searchvalue); 
                if(!local_list.isEmpty())
                {

                     //GenericEntity t = new GenericEntity<List<Books>>(Lists.newArrayList(l)){}; 
                     return Response.ok(local_list.toArray(new Books[local_list.size()])).build();
                 }
                 return Response.status(Response.Status.NOT_FOUND).entity("\n<->Book not found<->").build();
            }
            else if(query_type.equals("title"))
            {
                if(this.findByTitle(searchvalue) != null)return Response.ok(this.findByTitle(searchvalue)).build();
                return Response.status(Response.Status.NOT_FOUND).entity("\n<->Book not found<->").build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("\n<->Incorrect request<->").build();
    }

    public List<Books> findByAuthor(String author) 
    {
        List<Books> temp_book = null;
        try 
        {
            temp_book = (List<Books>)em.createNamedQuery("Books.findByAuthor").setParameter("author", author).getResultList();
        } catch (Exception e) 
        {
            /*No need to deal with exception, not a rollback exception. 
             *The exception throws if the entity was not found
             */
        }
        return temp_book;
    }

    public Books findByTitle(String title) 
    {
        Books temp_book = null;
        try 
        {
            temp_book = (Books)em.createNamedQuery("Books.findByTitle").setParameter("title", title).getSingleResult();
        } catch (Exception e) 
        {
            /*No need to deal with exception, not a rollback exception. 
             *The exception throws if the entity was not found
             */
        }
        return temp_book;
    }
    
}
