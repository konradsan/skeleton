package ru.kit.skeleton.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kit.skeleton.model.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by mikha on 13.01.2017.
 */
public class SagittalStepRepositoryImpl implements ListRepository {

    private static final Logger LOG = LoggerFactory.getLogger(SagittalStepRepositoryImpl.class);
    private List<Step> stepList = new ArrayList<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        stepList.add(new Step("Пятка", ""));
        stepList.add(new Step("Носок", ""));
        stepList.add(new Step("Талия", ""));
        stepList.add(new Step("Выпуклая часть спины", ""));
        stepList.add(new Step("Шея", ""));
    }

    @Override
    public Step get(int i) {

        if (i < stepList.size() && i >= 0) {
            Step step = stepList.get(i);
//            LOG.info("get Step id = {}, {}, {}", i, step, counter.get());
            return step;
        }

//        LOG.info("get Step id = {}, {}, {}", i, null, counter.get());
        return null;
    }

    @Override
    public int size() {
        return stepList.size();
    }

    @Override
    public Step getNext() {
        return get(counter.getAndIncrement());
    }

    @Override
    public Step getPrev() {
        return get(counter.decrementAndGet() - 1);
    }

    @Override
    public Step getThis() {
        return get(counter.get() - 1);
    }

    @Override
    public List<Step> getAllStepWhichPointNotNull() {
        return stepList.stream().filter(step -> step.getPoint() != null).collect(Collectors.toList());
    }

    @Override
    public void setDefault() {
        LOG.info("set default values");
        counter.set(0);
        stepList.stream().filter(step -> step.getPoint() != null).forEach(step -> step.setPoint(null));
    }

    @Override
    public Step getByName(String name) {
        return stepList.stream().filter(step -> step.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public boolean isFullPoint() {
        return getAllStepWhichPointNotNull().size() == stepList.size();
    }

    @Override
    public Step changeLast() {
        return stepList.get(counter.get() - 2);
    }
}
