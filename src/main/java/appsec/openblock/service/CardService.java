package appsec.openblock.service;

import appsec.openblock.model.Card;
import appsec.openblock.model.User;

import java.util.List;

public interface CardService {
    public List<Card> getByOwner(User user);

    public void setOwner(User user, Card card);
}
