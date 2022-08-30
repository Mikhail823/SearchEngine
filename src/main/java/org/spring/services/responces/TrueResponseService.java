package org.spring.services.responces;

public class TrueResponseService implements ResponseService {

    @Override
    public boolean getResult() {
        return true;
    }
}
