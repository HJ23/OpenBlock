package appsec.openblock.service;

import appsec.openblock.model.Complain;
import appsec.openblock.repository.ComplainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplainServiceImpl implements ComplainService{
    @Autowired
    ComplainRepository complainRepository;

    @Override
    public List<Complain> getComplains() {
        return complainRepository.findAll();
    }

    @Override
    public void saveComplain(Complain complain) {
        complainRepository.save(complain);
    }

}
