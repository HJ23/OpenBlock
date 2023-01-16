package appsec.openblock.model;


import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="NFT")
public class NFT  {
    @SequenceGenerator(
            name = "nft_sequence",
            sequenceName = "nft_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "nft_sequence"
    )
    private Long id;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }

    @Column(name="file_path")
    private String filePath;

    @Column(name="collection")
    private String collection;

    @Column(name="is_sold")
    private boolean isSold;

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getArtistFullName() {
        return artistFullName;
    }

    public void setArtistFullName(String artistFullName) {
        this.artistFullName = artistFullName;
    }

    public String getArtistCryptoAddress() {
        return artistCryptoAddress;
    }

    public void setArtistCryptoAddress(String artistCryptoAddress) {
        this.artistCryptoAddress = artistCryptoAddress;
    }

    public String getArtistTotalSale() {
        return artistTotalSale;
    }

    public void setArtistTotalSale(String artistTotalSale) {
        this.artistTotalSale = artistTotalSale;
    }

    public String getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(String initialPrice) {
        this.initialPrice = initialPrice;
    }

    @Column(name = "artist_full_name")
    private String artistFullName;

    @Column(name = "artist_crypto_address")
    private String artistCryptoAddress;

    @Column(name = "artist_total_sale")
    private String artistTotalSale;

    @Column(name = "initial_price")
    private String initialPrice;

    @Column(name = "token",unique = true) // unique art token
    private String token;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name="end_bidding")
    private LocalDateTime endBidding;

    public LocalDateTime getEndBidding() {
        return endBidding;
    }

    public void setEndBidding(LocalDateTime endBidding) {
        this.endBidding = endBidding;
    }

    public String getToken() {
        return token;
    }

    public boolean isSold() {
        return isSold;
    }

    public void setIsSold(boolean sold) {
        isSold = sold;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
