package appsec.openblock.service;

import appsec.openblock.model.Complain;

import java.util.List;

public interface ComplainService {
    List<Complain> getComplains();

    void saveComplain(Complain complain);
}
