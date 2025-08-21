package com.shopme.admin.setting.state;

import com.shopme.common.entity.state.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateService {
    private final StateRepository repo;

    @Autowired
   public StateService(StateRepository repo) {
        this.repo = repo;
    }
    public List<State> listAll() {
        return repo.findAll();
    }
}
