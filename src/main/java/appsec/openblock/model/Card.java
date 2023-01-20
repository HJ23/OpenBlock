package appsec.openblock.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "cards")
public class Card {

    @SequenceGenerator(
            name = "cards_sequence",
            sequenceName = "cards_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cards_sequence"
    )
    private Long id;

    @NotNull(message = "Card number can not be null!")
    @Column(name = "card_number")
    private String cardNumber;

    @NotNull(message = "Card owner full name can not be null!")
    @Column(name = "full_name")
    private String fullName;

    @NotNull(message = "Security code can not be null!")
    @Column(name = "security_code")
    private String securityCode;

    @NotNull(message = "Expire date can not be null!")
    @Column(name = "expire_date")
    private String expireDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber.replace(" ", "");
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
