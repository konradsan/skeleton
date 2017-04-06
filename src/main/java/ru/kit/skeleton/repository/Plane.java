package ru.kit.skeleton.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kit.skeleton.model.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mikha on 27.01.2017.
 */
public abstract class Plane implements ListRepository {

    private static final Logger LOG = LoggerFactory.getLogger(Plane.class);
    private List<Step> stepList = new ArrayList<>();
    private int counter = 0;

    @Override
    public Step get(int i) {
        if (i < stepList.size() && i >= 0) {
            Step step = stepList.get(i);
            return step;
        }

        LOG.info("get Step id = {}, {}, {}", i, null, counter);
        return null;
    }

    @Override
    public int size() {
        return stepList.size();
    }

    @Override
    public Step getNext() {
        return get(++counter);
    }

    @Override
    public Step getPrev() {
        return get(counter--);
    }

    @Override
    public Step getThis() {
        if (counter > stepList.size() - 1) counter = stepList.size() - 1;
        if (counter < 0) counter = 0;
        return get(counter);
    }

    @Override
    public List<Step> getAllStepWhichPointNotNull() {
        return stepList.stream().filter(step -> step.getPoint() != null).collect(Collectors.toList());
    }

    @Override
    public boolean isFullPoint() {
        return getAllStepWhichPointNotNull().size() == stepList.size()-1;
    }

    @Override
    public void setDefault() {
        LOG.info("set default values");
        counter = 0;
        stepList.stream().filter(step -> step.getPoint() != null).forEach(step -> step.setPoint(null));
    }

    @Override
    public Step getByName(String name) {
        return stepList.stream().filter(step -> step.getName().equals(name)).findFirst().orElse(null);
    }

    public List<Step> getStepList() {
        return stepList;
    }
}
