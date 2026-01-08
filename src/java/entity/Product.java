package entity;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="product")
public class Product implements Serializable{
    @Id    
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "condition_id")
    private Product_Condition product_condition;
    
    @ManyToOne
    @JoinColumn(name = "product_status_id")
    private Product_Status product_status;
    
    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private Product_Type model;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", nullable = false)
    private String description;
    
    @Column(name = "price", nullable = false)
    private double price;
    
    @Column(name = "qty", nullable = false)
    private int qty;
        
    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "date_time", nullable = false)
    private Date date_time;

    
    
    
    public Product() {
    }    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product_Condition getProduct_condition() {
        return product_condition;
    }

    public void setProduct_condition(Product_Condition product_condition) {
        this.product_condition = product_condition;
    }

    public Product_Status getProduct_status() {
        return product_status;
    }

    public void setProduct_status(Product_Status product_status) {
        this.product_status = product_status;
    }

    public Product_Type getModel() {
        return model;
    }

    public void setModel(Product_Type model) {
        this.model = model;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return qty;
    }

    public void setQuantity(int qty) {
        this.qty = qty;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate_time() {
        return date_time;
    }

    public void setDate_time(Date date_time) {
        this.date_time = date_time;
    }
    
    
}