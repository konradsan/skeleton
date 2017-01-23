package ru.kit.skeleton.repository;

import ru.kit.skeleton.model.Step;

import java.util.List;

/**
 * Created by mikha on 13.01.2017.
 */
public interface ListRepository {

    Step get(int i);
    int size();
    Step getNext();
    Step getPrev();
    Step getThis();
    List<Step> getAllStepWhichPointNotNull();
    boolean isFullPoint();
    void setDefault();
    Step getByName(String name);
    Step changeLast();
}
