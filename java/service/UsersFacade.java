/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.Users;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author David_killa
 */
@Stateless
public class UsersFacade extends AbstractFacade<Users> 
{
    @PersistenceContext(unitName = "restservicePU")
    private EntityManager em;

    /**
     *
     * @return
     */
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     *
     */
    public UsersFacade() {
        super(Users.class);
    }
    
}
