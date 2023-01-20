package appsec.openblock.service;

import appsec.openblock.model.Card;
import appsec.openblock.model.User;
import appsec.openblock.repository.CardRepository;
import appsec.openblock.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardServiceImpl implements CardService {
    @Autowired
    CardRepository cardRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public List<Card> getByOwner(User user) {
        return cardRepository.findByUserId(user.getId());
    }

    @Override
    public void setOwner(User user, Card card) {
        userRepository.findById(user.getId()).map(us -> {
            card.setUser(us);
            return cardRepository.save(card);
        });
    }
}
