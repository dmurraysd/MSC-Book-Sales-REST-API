/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author David_killa
 */
@Entity
@Table(name = "BOOKS", catalog = "", schema = "DAVID")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Books.findAll", query = "SELECT b FROM Books b"),
    @NamedQuery(name = "Books.findByBookid", query = "SELECT b FROM Books b WHERE b.bookid = :bookid"),
    @NamedQuery(name = "Books.findByIsbn", query = "SELECT b FROM Books b WHERE b.isbn = :isbn"),
    @NamedQuery(name = "Books.findByTitle", query = "SELECT b FROM Books b WHERE b.title = :title"),
    @NamedQuery(name = "Books.findByAuthor", query = "SELECT b FROM Books b WHERE b.author = :author"),
    @NamedQuery(name = "Books.findByCopyright", query = "SELECT b FROM Books b WHERE b.copyright = :copyright"),
    @NamedQuery(name = "Books.findByPublisher", query = "SELECT b FROM Books b WHERE b.publisher = :publisher"),
    @NamedQuery(name = "Books.findByQuantity", query = "SELECT b FROM Books b WHERE b.quantity = :quantity"),
    @NamedQuery(name = "Books.findByPrice", query = "SELECT b FROM Books b WHERE b.price = :price")})
public class Books implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    //@NotNull
    @Column(name = "BOOKID")
    private Integer bookid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ISBN")
    private long isbn;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "TITLE")
    private String title;
     @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "AUTHOR")
    private String author;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 4)
    @Column(name = "COPYRIGHT")
    private String copyright;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "PUBLISHER")
    private String publisher;
    @Column(name = "QUANTITY")
    private int quantity;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PRICE")
    private float price;
    //@OneToMany(cascade = CascadeType.ALL, mappedBy = "bookid")
    //private Collection<PurchaseOrder> purchaseOrderCollection;

    /**
     *
     */
    public Books() {
    }

    /**
     *
     * @param bookid
     */
    public Books(Integer bookid) {
        this.bookid = bookid;
    }

    /**
     *
     * @param isbn
     * @param title
     * @param copyright
     * @param publisher
     * @param quantity
     * @param price
     */
    public Books(long isbn, String title, String author,String copyright, String publisher, int quantity, float price) 
    {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.copyright = copyright;
        this.publisher = publisher;
        this.quantity = quantity;
        this.price = price;
    }
    
    /**
     *
     * @param isbn
     * @param title
     * @param copyright
     * @param publisher
     * @param price
     */
    public Books(long isbn, String title, String author, String copyright, String publisher, float price) {
        this.bookid = bookid;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.copyright = copyright;
        this.publisher = publisher;
        this.price = price;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public Integer getBookid() {
        return bookid;
    }

    /**
     *
     * @param bookid
     */
    public void setBookid(Integer bookid) {
        this.bookid = bookid;
    }

    /**
     *
     * @return
     */
    public long getIsbn() {
        return isbn;
    }

    /**
     *
     * @param isbn
     */
    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

   
    /**
     *
     * @return
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     *
     * @param copyright
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    /**
     *
     * @return
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     *
     * @param publisher
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     *
     * @return
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     *
     * @param quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     *
     * @return
     */
    public float getPrice() {
        return price;
    }

    /**
     *
     * @param price
     */
    public void setPrice(float price) {
        this.price = price;
    }

    /**
     *
     * @return
     */
   /* @XmlTransient
    public Collection<PurchaseOrder> getPurchaseOrderCollection() {
        return purchaseOrderCollection;
    }

    /**
     *
     * @param purchaseOrderCollection
     */
    /*public void setPurchaseOrderCollection(Collection<PurchaseOrder> purchaseOrderCollection) {
        this.purchaseOrderCollection = purchaseOrderCollection;
    }*/

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bookid != null ? bookid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Books)) {
            return false;
        }
        Books other = (Books) object;
        if ((this.bookid == null && other.bookid != null) || (this.bookid != null && !this.bookid.equals(other.bookid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Books[ bookid=" + bookid + " ]";
    }
    
}
